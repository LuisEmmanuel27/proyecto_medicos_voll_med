# Continuando notas...

## Para saber más: ¿PUT o PATCH?

Elegir entre el método HTTP PUT o PATCH es una pregunta común que surge cuando estamos desarrollando APIs y necesitamos crear un endpoint para la actualización de recursos. Comprendamos las diferencias entre las dos opciones y cuándo usar cada una.

## PUT

El método PUT reemplaza todos los datos actuales de un recurso con los datos enviados en la solicitud, es decir, estamos hablando de una actualización completa. Entonces, con él, hacemos la actualización completa de un recurso en una sola solicitud.

## PATCH

El método PATCH, a su vez, aplica modificaciones parciales a un recurso. Por lo tanto, es posible modificar solo una parte de un recurso. Con PATCH, entonces, realizamos actualizaciones parciales, lo que flexibiliza las opciones de actualización.

## ¿Cuál elegir?

En la práctica, es difícil saber qué método usar, porque no siempre sabremos si un recurso se actualizará parcial o completamente en una solicitud, a menos que lo verifiquemos, algo que no se recomienda.

Entonces, lo más común en las aplicaciones es usar el método PUT para las solicitudes de actualización de recursos en una API, que es nuestra elección en el proyecto utilizado a lo largo de este curso.

# Request DELTE

Para este caso realizaremos un DELTE `logico` y no `fisico`, que queremos decir con esto? es que vamos a eliminar el campo de manera que al listar no aparezca pero no sera eliminado de la base de datos.

Pero primero haremos el `fisico` para despues modificarlo y solo haga el `logico`.

1.  Vamos a MedicoController y simplemente y de momento agregaremos lo siguiente:

        @DeleteMapping("/3")
        public void eliminarMedico() {

        }

2.  notese que podemos agregar una prop url desde mos mapping en este caso agregamos el /3 para que podamos usar la url en el DELETE `http://localhost:8080/medicos/3`

3.  vamos al thunder client o insomnia, creamos un nuevo HTTP request DELETE y colocamos la url anterior

4.  si realizamos la peticion nos mandara un 200 OK pero no hara realmente nada por lo mismo de que el metodo no esta completo

5.  vamos a volverlo dinamico (como con react router) modificando de la siguiente manera:

        @DeleteMapping("/{id}")
        public void eliminarMedico() {

        }

6.  ahora si podemos terminar el metodo quedando de la siguiente manera:

        @DeleteMapping("/{id}")
        @Transactional
        public void eliminarMedico(@PathVariable Long id) {
            Medico medico = medicoRepository.getReferenceById(id);
            medicoRepository.delete(medico);
        }

### Explicacion del codigo:

-   @DeleteMapping("/{id}"): Esta anotación indica que este método responderá a las solicitudes HTTP DELETE en la URL que coincide con el patrón /{id}. El valor {id} es una variable de ruta que corresponderá al ID del médico que se desea eliminar.

-   @Transactional: Al igual que en otros métodos que involucran interacciones con la base de datos, aquí también se utiliza la anotación @Transactional. Esto asegura que todas las operaciones realizadas en este método se realicen dentro de una única transacción. Si la operación es exitosa, los cambios se confirmarán; si hay un error, los cambios se revierten, garantizando la integridad de los datos.

-   public void eliminarMedico(@PathVariable Long id): Este es el método que se ejecutará cuando se reciba una solicitud DELETE en la URL correspondiente. La anotación @PathVariable se usa para vincular el valor del parámetro de la URL ({id}) a la variable id.

-   Medico medico = medicoRepository.getReferenceById(id);: Aquí, se recupera el objeto Medico utilizando el método getReferenceById del repositorio medicoRepository. Este método busca una referencia al objeto en lugar de cargar todos los datos del objeto en la memoria, lo que puede ser útil cuando solo necesitas el objeto para realizar una operación específica, como eliminar.

-   medicoRepository.delete(medico);: Luego, se utiliza el método delete del repositorio medicoRepository para eliminar el objeto Medico de la base de datos. Al haber iniciado la transacción con la anotación @Transactional, la eliminación se llevará a cabo dentro de la misma transacción, asegurando la consistencia de los datos.

En resumen, este método DELETE se encarga de eliminar un registro de médico de la base de datos. Utiliza la anotación @Transactional para garantizar la consistencia de los datos y opera dentro de una transacción para asegurarse de que la eliminación se realice correctamente y que los cambios sean confirmados o revertidos en caso de error.

## Continuando...

7. Compilamos y probamos de nuevo el request DELETE y veremos que se elimina, para comprobar podemos hacer uso del GET o revisar la base de datos directamente.

# Exclusión logica

1.  Recordemos que queriamos un DELETE `logico` asi que debemos hacer unos cuantos cambios, lo ideal seria agregar un flag, un campo nuevo.

2.  Como lo anterior es un migration con flyway debemos asegurarnos de tener cerrado el programa, primero antes iremos a nuestra entidad `Medico.java` y agregaremos un nuevo campo de tipo Boolean:

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String nombre;
        private String email;
        private String telefono;
        private String documento;
        private Boolean activo;

        @Enumerated(EnumType.STRING)
        private Especialidad especialidad;

        @Embedded
        private Direccion direccion;

        public Medico(DatosRegistroMedico datosRegistroMedico) {
            this.activo = true;
            this.nombre = datosRegistroMedico.nombre();
            this.email = datosRegistroMedico.email();
            this.telefono = datosRegistroMedico.telefono();
            this.documento = datosRegistroMedico.documento();
            this.especialidad = datosRegistroMedico.especialidad();
            this.direccion = new Direccion(datosRegistroMedico.direccion());
        }

3.  Creamos un nuevo archivo .sql de nombre `V3__alter-table-medicos-add-activos.sql` y colocamos las siguientes lineas que crean el nuevo parametro y ademas actualiza a todos los medicos para que lo tengan con el valor de 1 osea true:

        ALTER TABLE medicos ADD activo TINYINT NOT NULL;

        UPDATE medicos SET activo = 1;

4.  compilamos y revisamos la base de datos para veridicar que todo este en orden

5.  ahora ya modificamos el metodo de eliminar de la siguiente manera:

        @DeleteMapping("/{id}")
        @Transactional
        // Eliminacion logica
        public void eliminarMedico(@PathVariable Long id) {
            Medico medico = medicoRepository.getReferenceById(id);
            medico.desactivarMedico(medico);
        }

6.  Nos daremos cuenta que creamos un nuevo metodo en `Medico.java` de nombre `desactivarMedico()` asi que vamos a terminar dicho metodo

7.  Queda de la siguiente manera:

        public void desactivarMedico(Medico medico) {
            medico.activo = false;
        }

8.  compilamos y probamos de nuevo con thunder client o insomnia pero veremos algo curioso...

### Expliacion del codigo

El cambio que se ha realizado implica cambiar la eliminación física de un médico (donde se eliminaría el registro de la base de datos) por una eliminación lógica (donde se marca al médico como inactivo sin eliminar el registro). Veamos en detalle cómo funcionan ambos métodos:

#### En el controlador MedicoController:

    @DeleteMapping("/{id}")
    @Transactional
    // Eliminacion logica
    public void eliminarMedico(@PathVariable Long id) {
        Medico medico = medicoRepository.getReferenceById(id);
        medico.desactivarMedico();
    }

-   @DeleteMapping("/{id}"): Como antes, esta anotación define que este método manejará las solicitudes DELETE para la URL que tenga el patrón /{id}, donde {id} es una variable de ruta que corresponde al ID del médico a eliminar.

-   @Transactional: Al igual que antes, se utiliza la anotación @Transactional para asegurar que las operaciones dentro de este método se realicen dentro de una única transacción, lo que garantiza la consistencia de los datos.

-   Medico medico = medicoRepository.getReferenceById(id);: Al igual que antes, aquí se obtiene una referencia al objeto Medico utilizando el método getReferenceById del repositorio medicoRepository.

-   medico.desactivarMedico();: En lugar de eliminar físicamente el registro, ahora se llama al método desactivarMedico en el objeto Medico. Este método es parte de la entidad Medico. La función de este método es cambiar el estado del médico a "inactivo" mediante la asignación de false a la propiedad activo.

#### En la clase Medico:

    public void desactivarMedico() {
        this.activo = false;
    }

El método desactivarMedico es una implementación de eliminación lógica. En lugar de eliminar físicamente el registro de la base de datos, simplemente cambia el estado del médico a "inactivo". Esto suele ser útil para mantener un historial de registros y para permitir la posibilidad de reactivar el médico en el futuro si es necesario. En este caso, la eliminación lógica se logra modificando la propiedad activo de la entidad Medico y estableciéndola en false.

Con este enfoque, en lugar de eliminar físicamente el registro de médico de la base de datos, estás cambiando una propiedad (activo) para indicar que el médico está inactivo. Esto puede ser útil para mantener un registro histórico y no perder información relevante, a pesar de que el médico ya no esté activo en el sistema.

## Continuando...

9.  Notaremos que si usamos el DELETE y comprobamos con el GET aun aparece el elemento que segun eliminamos, pero si vamos a la base de datos ya lo marca como 0, asi que solo debemos modificar el GET para que no muestre los que tengan false el valor de activo

10. modificamos el metodo GET de la siguiente manera:

        @GetMapping
        public Page<DatosListadoMedico> listadoMedicos(@PageableDefault(size = 10) Pageable paginacion) {
            // return medicoRepository.findAll(paginacion).map(DatosListadoMedico::new);

            // despues de agregar el metodo Delete Logico...
            return medicoRepository.findByActivoTrue(paginacion).map(DatosListadoMedico::new);
        }

11. debido a que `findByActivoTrue` no existe como tal en el repository deberemos crearlo:

        Page<Medico> findByActivoTrue(Pageable paginacion);

### Explicacion del codigo:

Antes de implementar la eliminación lógica, el método GET simplemente recuperaba todos los médicos utilizando medicoRepository.findAll(paginacion), que devolvía una lista de todos los registros sin considerar su estado de activación o desactivación.

Después de implementar la eliminación lógica, cuando desactivas un médico, su estado se establece en "inactivo" (por ejemplo, la propiedad activo se establece en false). Sin embargo, si seguimos utilizando medicoRepository.findAll(paginacion) en tu método GET, seguirá recuperando todos los registros, tanto los médicos activos como los inactivos.

#### Modificacion del GET

        @GetMapping
        public Page<DatosListadoMedico> listadoMedicos(@PageableDefault(size = 10) Pageable paginacion) {
            return medicoRepository.findByActivoTrue(paginacion).map(DatosListadoMedico::new);
        }

Ahora, en lugar de usar medicoRepository.findAll(paginacion), se esta utilizando medicoRepository.findByActivoTrue(paginacion). Esto significa que solo recuperará los médicos con el estado de activo igual a true, es decir, los médicos que no han sido desactivados.

#### Modificacion del repositorio MedicoRepository

    public interface MedicoRepository extends JpaRepository<Medico, Long> {
        Page<Medico> findByActivoTrue(Pageable paginacion);
    }

Aquí se ha agregado un nuevo método personalizado findByActivoTrue, que utiliza la convención de nombres de Spring Data para crear una consulta basada en el estado de activación (activo). Esto filtra los médicos por su estado activo.

En resumen, estos cambios se realizaron para que tu método GET y tu repositorio respeten la eliminación lógica implementada. Ahora, solo recuperarás y mostrarás los médicos que están marcados como activos (activo = true), lo que asegura que los médicos desactivados no aparezcan en la lista. Esto es importante para mantener la coherencia y la integridad en tu sistema médico.

## Continuando...

12. compilamos y volvemos a probar el GET y veremos que ya no aparece el elemento de la base de datos que tiene el false en activo

# FIN
