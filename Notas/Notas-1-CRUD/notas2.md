# Continuación de notas.md

## Notas adicionales

En algunos proyectos Java, dependiendo de la tecnología elegida, es común encontrar clases que siguen el patrón `DAO`, usado para aislar el acceso a los datos. Sin embargo, en este curso usaremos otro patrón, conocido como `Repositorio`.

Pero entonces pueden surgir algunas preguntas: ¿cuál es la diferencia entre los dos enfoques y por qué esta elección?

### Patrón DAO

El patrón de diseño DAO, también conocido como `Data Access Object`, se utiliza para la persistencia de datos, donde su objetivo principal es separar las reglas de negocio de las reglas de acceso a la base de datos. En las clases que siguen este patrón, aislamos todos los códigos que se ocupan de conexiones, comandos SQL y funciones directas a la base de datos, para que dichos códigos no se esparzan a otras partes de la aplicación, algo que puede dificultar el mantenimiento del código y también el intercambio de tecnologías y del mecanismo de persistencia.

### Implementación

Supongamos que tenemos una tabla de productos en nuestra base de datos. La implementación del patrón DAO sería la siguiente:

Primero, será necesario crear una clase básica de dominio `Producto`:

    public class Producto {
        private Long id;
        private String nombre;
        private BigDecimal precio;
        private String descripcion;

        // constructores, getters y setters
    }

A continuación, necesitaríamos crear la clase `ProductoDao`, que proporciona operaciones de persistencia para la clase de dominio `Producto`:

    public class ProductoDao {

        private final EntityManager entityManager;

        public ProductoDao(EntityManager entityManager) {
            this.entityManager = entityManager;
        }

        public void create(Producto producto) {
            entityManager.persist(producto);
        }

        public Producto read(Long id) {
            return entityManager.find(Producto.class, id);
        }

        public void update(Producto producto) {
            entityManger.merge(producto);
        }

        public void remove(Producto producto) {
            entityManger.remove(producto);
    }

    }

En el ejemplo anterior, se utilizó JPA como tecnología de persistencia de datos de la aplicación.

### Padrón Repository

Según el famoso libro Domain-Driven Design de Eric Evans:

`* El repositorio es un mecanismo para encapsular el almacenamiento, recuperación y comportamiento de búsqueda, que emula una colección de objetos. *`

En pocas palabras, un repositorio también maneja datos y oculta consultas similares a DAO. Sin embargo, se encuentra en un nivel más alto, más cerca de la lógica de negocio de una aplicación. Un repositorio está vinculado a la regla de negocio de la aplicación y está asociado con el agregado de sus objetos de negocio, devolviéndolos cuando es necesario.

Pero debemos estar atentos, porque al igual que en el patrón DAO, las reglas de negocio que están involucradas con el procesamiento de información no deben estar presentes en los repositorios. Los repositorios no deben tener la responsabilidad de tomar decisiones, aplicar algoritmos de transformación de datos o brindar servicios directamente a otras capas o módulos de la aplicación. Mapear entidades de dominio y proporcionar funcionalidades de aplicación son responsabilidades muy diferentes.

Un repositorio se encuentra entre las reglas de negocio y la capa de persistencia:

1. Proporciona una interfaz para las reglas comerciales donde se accede a los objetos como una colección;

2. Utiliza la capa de persistencia para escribir y recuperar datos necesarios para persistir y recuperar objetos de negocio.

Por lo tanto, incluso es posible utilizar uno o más DAOs en un repositorio.

### ¿Por qué el padrón repositorio en lugar de DAO usando Spring?

El patrón de repositorio fomenta un diseño orientado al dominio, lo que proporciona una comprensión más sencilla del dominio y la estructura de datos. Además, al usar el repositorio de Spring, no tenemos que preocuparnos por usar la API de JPA directamente, simplemente creando los métodos, que Spring crea la implementación en tiempo de ejecución, lo que hace que el código sea mucho más simple, pequeño y legible.

### Fin de las notas adicionales

# Migraciones flyway

`NOTA IMPORTANTE: DETENER EL CODIGO ANTES DE COMPILAR O MODIFICAR CUALQUIER ARCHIVO MIGRATION YA QUE SE PUEDEN PRODUCIR SEVEROS ERRORES, DONDE AL FINAL LO IDEAL ES IR A LA BASE DE DATOS Y BORRAR EL flyway_schema_history QUE SE GENERA, CON ESO DEBERIAMOS PODER CONTINUAR CON NORMALIDAD`

1.  dentro de resources creamos la carpeta `db` y dentro de esta otra de nombre `migration`

2.  flyway necesita un patron para identificar que archivos han sido migrados y cuales no, ya que usa archivos de extensión .sql, el patron seria `numeroVersion__nombre-descriptivo.sql`, en este caso creamos el archivo `V1__create-table-medicos.sql`, recordando que debe empezar con mayuscula.

3.  Agregamos el siguiente codigo para crear la tabla:

        CREATE TABLE
            medicos (
                id BIGINT NOT NULL AUTO_INCREMENT,
                nombre VARCHAR(100) NOT NULL,
                email VARCHAR(100) NOT NULL UNIQUE,
                documento VARCHAR(6) NOT NULL UNIQUE,
                especialidad VARCHAR(100) NOT NULL,
                calle VARCHAR(100) NOT NULL,
                distrito VARCHAR(100) NOT NULL,
                complemento VARCHAR(100),
                numero VARCHAR(20),
                ciudad VARCHAR(100) NOT NULL,
                PRIMARY KEY(id)
            );

4.  Hecho lo anterior compilamos el codigo y en consola veremos un mensaje que indica que se creo la migración y si vamos al admin de MySQL veremos que se crearon dos tablas, la de medicos y una de flyway schema history.

5.  Si detenemos el programa y volvemos a compilar veremos que ya no se vuelve a crear la tabla asi que no debemos preocuparnos por cosas de ese estilo.

6.  Ahora si volvemos a ejecutar el POST desde insomniac veremos como en la tabla medicos aparece la información que colocamos en el body de insomniac indicando que ya funciona al menos esa parte del codigo, aún quedan muchas cosas por hacer como las validaciones.

# Validaciones

1.  Volvemos al *https://start.spring.io/* y buscaremos una nueva dependencia, en este caso se llama Validation I/O y repetimos el proceso de copiar y pegar dentro del pom.xml

2.  Ahora, en donde estan llegando los datos? en el DTO de medico osea en `DatosRegistroMedico.java`, por lo que es ahi donde deberemos realizar las validaciones

3.  Estando en el DTO modificaremos el codigo de la siguiente manera:

        package med.voll.api.medico;

        import jakarta.validation.constraints.Email;
        import jakarta.validation.constraints.NotBlank;
        import jakarta.validation.constraints.NotNull;
        import jakarta.validation.constraints.Pattern;
        import med.voll.api.direccion.DatosDireccion;

        public record DatosRegistroMedico(
                @NotBlank String nombre,

                @NotBlank @Email String email,

                @NotBlank @Pattern(regexp = "\\d{4,6}") String documento,

                @NotBlank Especialidad especialidad,

                @NotNull DatosDireccion direccion) {
        }

### Explicacion del codigo:

-   @NotBlank:

    -   Esta anotación se aplica a campos de texto (String) y verifica que el valor no sea nulo ni esté compuesto solo por espacios en blanco. En otras palabras, el campo no debe estar vacío.

    -   Se utiliza en los campos nombre, email, documento y especialidad.

-   @Email:

    -   Esta anotación se aplica a campos de texto (String) y verifica que el valor tenga un formato de dirección de correo electrónico válido.

    -   Se aplica al campo email, asegurando que el valor proporcionado sea una dirección de correo electrónico válida.

-   @Pattern(regexp = "\\d{4,6}"):

    -   Esta anotación se utiliza junto con una expresión regular para verificar que el valor del campo cumpla con un patrón específico. En este caso, la expresión regular \\d{4,6} verifica que el valor tenga entre 4 y 6 dígitos numéricos.

    -   Se aplica al campo documento, asegurando que el valor sea un número con una longitud específica.

-   @NotNull:

    -   Esta anotación se aplica a campos de objeto (Object) y verifica que el valor no sea nulo.

    -   Se aplica al campo direccion, asegurando que el valor de la dirección no sea nulo.

### Importancia de las validaciones:

Las validaciones en tu código son esenciales para garantizar la integridad y la coherencia de los datos que ingresan en tu aplicación. Al aplicar estas anotaciones de validación en los campos de DatosRegistroMedico, estás asegurándote de que los datos que ingresen cumplan con ciertos criterios antes de ser procesados.

Las validaciones evitan que datos incorrectos o inválidos lleguen a la capa de lógica de negocio y a la base de datos, lo que puede conducir a problemas y errores en tu aplicación. Al implementar estas validaciones, mejoras la calidad de los datos, previenes problemas futuros y facilitas la detección temprana de errores en el proceso de ingreso de datos.

En resumen, las anotaciones de validación son una parte fundamental de la programación defensiva y contribuyen a crear aplicaciones más robustas y confiables al garantizar que los datos cumplan con las reglas establecidas antes de ser procesados.

## Continuando...

4.  Debido a que también tenemos `DatosDireccion.java` procedemos a modificar el codigo de la siguiente manera:

        package med.voll.api.direccion;

        import jakarta.validation.constraints.NotBlank;

        public record DatosDireccion(
                @NotBlank String calle,

                @NotBlank String distrito,

                @NotBlank String ciudad,

                @NotBlank int numero,

                @NotBlank String complemento) {
        }

5.  Compilamos y podemos hacer pruebas como enviar otro medico o cosas asi, PERO PERO PEROOOOO, las validaciones siguen siendo a nivel base de datos, lo cual no queremos por algo tenemos la dependencia nueva, eso es por que nos falta algo.

6.  Vamos a `MedicoController.java`

7.  Simplemente agregaremos `@Valid` con eso le estaremos diciendo que queremos validar:

        public void registrarMedico(@RequestBody @Valid DatosRegistroMedico datosRegistroMedico) {
                medicoRepository.save(new Medico(datosRegistroMedico));
            }

8.  No solo ahi tambien volviendo al DTO de Medico lo agregamos en la parte de `DatosDireccion` (`DatosRegistroMedico.java`):

        @NotNull @Valid DatosDireccion direccion

9.  y de paso hacemos una correccion aqui, pasando de NotBlank a NotNull:

        @NotNull Especialidad especialidad,

### Explicacion del cambio:

-   @NotBlank:

    -   La anotación @NotBlank se utiliza para campos de texto (String) y verifica que el valor no sea nulo ni esté compuesto solo por espacios en blanco. Esta anotación tiene sentido cuando se trata de campos de texto donde se espera que el valor tenga contenido, y no solo espacios en blanco.

    -   Aplicar @NotBlank a un campo de enum como especialidad no sería adecuado, ya que un enum no es un campo de texto, y esta anotación podría causar confusión en cuanto a su aplicación.

-   @NotNull:

    -   La anotación @NotNull se utiliza para campos de objeto (Object) y verifica que el valor no sea nulo. En el caso de un enum, @NotNull tiene más sentido, ya que el campo especialidad es un objeto enum, y lo que deseas asegurarte es que no sea nulo.

En resumen, al cambiar @NotBlank por @NotNull en el campo especialidad, estamos indicando de manera más precisa que el campo especialidad debe ser un enum no nulo. Esto refleja mejor la intención de tu validación y garantiza que el valor de especialidad no sea nulo al momento de procesar los datos en tu aplicación.

## Continuando...

10. Volvemos a compilar y mandaremos un body erroneo y veremos que pasa en consola.

11. Para este punto corregi un error que en el curso se dijo cambiar Numero de Integer a String:

        @NotBlank String numero,

    De no hacerlo no se haran las validaciones correctamente

12. Compilar el programa y el insomniac mandar un body que pueda fallar como uno si el campo nombre:

        {
            "email": "sara.lopez@voll.med",
            "documento": "111111",
            "especialidad": "ORTOPEDIA",
            "direccion": {
                "calle": "calle 2",
                "distrito": "distrito 2",
                "ciudad": "Lima",
                "numero": "1",
                "complemento": "abc"
            }
        }

    Eso nos debe mandar un Bad Request o error 400, osea que ya no se valida en el servidor, si no antes de que llegue a este osea nivel del `payload` y no a nivel del `servidor`.

# Nueva Migracion

1.  Se nos ha olvidado agregar el campo telefono, no lo tenemos presente y lo necesitamos, el medico necesita un telefono, suele suceder que nos pidan agregar nuevos campos en un ambiente real de proyecto, asi que necesitamos una nueva migracion

2.  Vamos a migration y creamos un nuevo archivo de nombre `V2__alter-table-medicos-add-telefono.sql`

3.  Agregamos el siguiente codigo:

        ALTER TABLE medicos ADD telefono VARCHAR(20) NOT NULL;

4.  Lo anterior también significa modificar otras partes del codigo, asi que vamos por partes, primero en nuestra entidad `Medico.java`:

        public class Medico {

            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;
            private String nombre;
            private String email;
            private String telefono;
            private String documento;

            @Enumerated(EnumType.STRING)
            private Especialidad especialidad;

            @Embedded
            private Direccion direccion;

            public Medico(DatosRegistroMedico datosRegistroMedico) {
                this.nombre = datosRegistroMedico.nombre();
                this.email = datosRegistroMedico.email();
                this.telefono = datosRegistroMedico.telefono();
                this.documento = datosRegistroMedico.documento();
                this.especialidad = datosRegistroMedico.especialidad();
                this.direccion = new Direccion(datosRegistroMedico.direccion());

            }

        }

5.  Esto afecta al DTO `DatosRegistroMedico.java`:

        public record DatosRegistroMedico(
                @NotBlank String nombre,

                @NotBlank @Email String email,

                @NotBlank String telefono,

                @NotBlank @Pattern(regexp = "\\d{4,6}") String documento,

                @NotNull Especialidad especialidad,

                @NotNull @Valid DatosDireccion direccion) {
        }

6.  Compilamos y veremos que en consola nos aparece algo como:

        : Successfully validated 2 migrations (execution time 00:00.173s)
        2023-08-15T22:09:27.152-06:00  INFO 14292 --- [  restartedMain] o.f.core.internal.command.DbMigrate      : Current version of schema `vollmed_api`: 1
        2023-08-15T22:09:27.298-06:00  INFO 14292 --- [  restartedMain] o.f.core.internal.command.DbMigrate      : Migrating schema `vollmed_api` to version "2 - alter-table-medicos-add-telefono"
        2023-08-15T22:09:27.532-06:00  INFO 14292 --- [  restartedMain] o.f.core.internal.command.DbMigrate      : Successfully applied 1 migration to schema `vollmed_api`, now at version v2 (execution time 00:00.397s)
        2023-08-15T22:09:28.054-06:00  INFO 14292 --- [  restartedMain] o.hibernate.jpa.internal.util.LogHelper  : H

    Indicando que al parecer todo va bien

7.  En Insomniac mandaremos nuevos datos para terminar de verificar esto:

        {
            "nombre": "Aria",
            "email": "aria.Zzz@voll.med",
            "documento": "111222",
            "especialidad": "CARDIOLOGIA",
            "direccion": {
                "calle": "calle 1",
                "distrito": "distrito 4",
                "ciudad": "Tokio",
                "numero": "2",
                "complemento": "zxv"
            }
        }

    Primero asi y veremos que nos da un Bad Request 400, indicando que nos falta el telefono

8.  Ahora si mandamos esto:

        {
            "nombre": "Aria",
            "email": "aria.Zzz@voll.med",
            "documento": "111222",
            "telefono": "7223991234",
            "especialidad": "CARDIOLOGIA",
            "direccion": {
                "calle": "calle 1",
                "distrito": "distrito 4",
                "ciudad": "Tokio",
                "numero": "2",
                "complemento": "zxv"
            }
        }

    No dara error y veremos que se agrego con exito a la base de datos.

# Produciendo Datos

Necesitamos enlistar a los medicos y puede que estos sean demasiados asi que tampoco podemos mostrar por ejemplo 500 medicos de golpe, debemos hacer cosas como mostrar de 10 en 10 medicos, osea hacer paginaciones

1.  Empezaremos por crear el nuevo metodo en `MedicoController.java`

        package med.voll.api.controller;

        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.PostMapping;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.RequestBody;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

        import jakarta.validation.Valid;
        import med.voll.api.medico.DatosRegistroMedico;
        import med.voll.api.medico.Medico;
        import med.voll.api.medico.MedicoRepository;

        import java.util.List;

        @RestController
        @RequestMapping("/medicos")
        public class MedicoController {

            @Autowired
            private MedicoRepository medicoRepository;

            @PostMapping
            public void registrarMedico(@RequestBody @Valid DatosRegistroMedico datosRegistroMedico) {
                medicoRepository.save(new Medico(datosRegistroMedico));
            }

            @GetMapping
            public List<Medico> listadoMedico() {
                return medicoRepository.findAll();
            }

        }

### Explicacion del codigo:

En este caso, `listadoMedico()` simplemente obtiene todos los médicos de la base de datos utilizando el método `findAll()` proporcionado por la interfaz `MedicoRepository`. Esto se debe a que `MedicoRepository` extiende la interfaz `JpaRepository<Medico, Long>`, que proporciona una serie de métodos predefinidos para realizar operaciones comunes en la base de datos, como consultar y manipular datos.

Algunos puntos clave:

-   GetMapping: La anotación @GetMapping indica que este método responderá a las solicitudes HTTP GET en la ruta especificada ("/medicos").

-   List<Medico>: El método devuelve una lista de objetos de tipo Medico, que representan los registros de médicos en la base de datos.

-   medicoRepository.findAll(): Este método utiliza el MedicoRepository para realizar una consulta a la base de datos y recuperar todos los registros de médicos.

-   No es necesario hacer mucho más: Debido a que estás utilizando Spring Data JPA y has definido la interfaz MedicoRepository con JpaRepository<Medico, Long>, Spring se encarga de implementar las operaciones de consulta y manipulación de datos en la base de datos automáticamente. Por lo tanto, no necesitas escribir consultas SQL personalizadas ni implementar manualmente la lógica para recuperar los datos.

En resumen, el método listadoMedico() permite recuperar todos los registros de médicos de la base de datos y devolverlos como una lista en formato JSON como respuesta a las solicitudes GET en la ruta "/medicos". Esta es una forma eficiente y conveniente de obtener los datos de la base de datos utilizando Spring Data JPA.

## Continuando...

2. Compilamos el codigo para verificar si funciona el metodo

3. Hacemos una petición GET con la url http://localhost:8080/medicos y veremos que nos lista a TODOS los medicos en la base de datos, pero pues solo queremos recibir cierta información de estos y no recibirlos a todos de golpe.

# Probando lista

1.  Como solo queremos que se muestre cierta información podemos crear un DTO para esta misma necesidad

2.  Vamos de nuevo a `MedicoController.java` y de momento solo modificaremos esta linea:

        public List<DatosListadoMedico> listadoMedico() {

3.  Como estamos creando un nuevo record pues nos dara error asi que dentro del paquete medico creamos el record `DatosListadoMedico.java`

4.  Aun nos dara un error en `MedicoController.java` pero de momento vamos a ignorarlo y vamos a terminar `DatosListadoMedico.java`

5.  Agregamos el siguiente codigo:

        package med.voll.api.medico;

        public record DatosListadoMedico(
                String nombre,
                String especialidad,
                String documento,
                String email) {
        }

6.  volvemos a `MedicoController` y terminamos de modificar el metodo:

        @GetMapping
        public List<DatosListadoMedico> listadoMedico() {
            return medicoRepository.findAll().stream().map(DatosListadoMedico::new).toList();
        }

7.  Pero la modificación anterior hace que necesitemos agregar un constructor en `DatosListadoMedico.java`:

        package med.voll.api.medico;

        public record DatosListadoMedico(
                String nombre,
                String especialidad,
                String documento,
                String email) {

            public DatosListadoMedico(Medico medico) {
                this(
                        medico.getNombre(),
                        medico.getEspecialidad().toString(),
                        medico.getDocumento(),
                        medico.getEmail());
            }

        }

### Explicacion del codigo:

#### MedicoController.java:

    @GetMapping
    public List<DatosListadoMedico> listadoMedico() {
        return medicoRepository.findAll().stream().map(DatosListadoMedico::new).toList();
    }

-   En esta modificación, hemos cambiado el método listadoMedico() para que ahora retorne una lista de objetos DatosListadoMedico.

-   medicoRepository.findAll() recupera todos los registros de médicos de la base de datos utilizando la interfaz MedicoRepository.

-   .stream() convierte la lista de médicos en un flujo (stream) de objetos.

-   .map(DatosListadoMedico::new) aplica la operación de mapeo a cada elemento del flujo. Aquí estás utilizando una referencia a un constructor (DatosListadoMedico::new) para crear un nuevo objeto DatosListadoMedico a partir de cada objeto Medico en el flujo.

-   .toList() convierte el flujo mapeado nuevamente en una lista de objetos DatosListadoMedico.

En resumen, este método transforma una lista de objetos Medico en una lista de objetos DatosListadoMedico utilizando un flujo y la operación de mapeo.

#### DatosListadoMedico.java:

    public record DatosListadoMedico(
            String nombre,
            String especialidad,
            String documento,
            String email) {

        public DatosListadoMedico(Medico medico) {
            this(
                    medico.getNombre(),
                    medico.getEspecialidad().toString(),
                    medico.getDocumento(),
                    medico.getEmail());
        }
    }

En la clase `DatosListadoMedico`, se ha creado un "record" que define una clase inmutable con campos y constructor automáticos. Esta clase se utiliza para representar los datos de los médicos en el formato deseado para el listado.

-   Los campos definidos en el record (`nombre`, `especialidad`, `documento` y `email`) son los atributos que deseas mostrar en el listado.

-   El constructor `DatosListadoMedico(Medico medico)` toma un objeto Medico y asigna sus valores a los campos correspondientes en el record.

-   Dentro de este constructor, estás utilizando métodos del objeto `Medico` para obtener los valores que deseas mostrar en el listado, como el nombre, especialidad, documento y email.

En resumen, la clase DatosListadoMedico define la estructura de los datos que se mostrarán en el listado de médicos y proporciona un constructor para crear objetos de esta clase a partir de objetos Medico.

En conjunto, estas modificaciones permiten obtener una lista de objetos DatosListadoMedico a partir de los registros de médicos en la base de datos. Estos objetos contienen la información específica que deseas mostrar en el listado, y la transformación se realiza utilizando flujos y operaciones de mapeo.

## continuando...

8. Compilamos y volvemos a hacer la peticion get y veremos que ahora solo nos retorna los valores que definimos en `DatosListadoMedico.java`

# Paginación

Bajo la suposicion de que las reglas del negocio indican que debemor ordenarlos de manera ascendente y que maximo se muestren de 10 por pagina...

1.  Para comenzar a implementar esta funcionalidad debemos volver una vez mas a `MedicoController` y modificar el metodo de listado:

        package med.voll.api.controller;

        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.data.domain.Page;
        import org.springframework.data.domain.Pageable;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.PostMapping;
        import org.springframework.web.bind.annotation.RequestBody;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

        import jakarta.validation.Valid;
        import med.voll.api.medico.DatosListadoMedico;
        import med.voll.api.medico.DatosRegistroMedico;
        import med.voll.api.medico.Medico;
        import med.voll.api.medico.MedicoRepository;

        @RestController
        @RequestMapping("/medicos")
        public class MedicoController {

            @Autowired
            private MedicoRepository medicoRepository;

            @PostMapping
            public void registrarMedico(@RequestBody @Valid DatosRegistroMedico datosRegistroMedico) {
                medicoRepository.save(new Medico(datosRegistroMedico));
            }

            @GetMapping
            public Page<DatosListadoMedico> listadoMedicos(Pageable paginacion) {
                return medicoRepository.findAll(paginacion).map(DatosListadoMedico::new);
            }

        }

### Explicacion del codigo:

Este método realiza una consulta paginada a la base de datos para obtener una lista de médicos y luego mapea los resultados a una lista de DatosListadoMedico, que es una clase creada por ti para representar la información simplificada de los médicos en la lista.

-   @GetMapping: Esta anotación indica que el método maneja peticiones HTTP GET en la ruta /medicos. Cuando se accede a esa ruta con una petición GET, este método será invocado.

-   public Page<DatosListadoMedico> listadoMedicos(Pageable paginacion): Este es el método listadoMedicos que recibe como parámetro un objeto Pageable llamado paginacion. El Pageable se utiliza para configurar la paginación y ordenamiento de los resultados de la consulta.

-   medicoRepository.findAll(paginacion): Aquí estás utilizando el medicoRepository para realizar una consulta a la base de datos y obtener una página de resultados de médicos. El objeto paginacion proporciona información sobre el número de página, el tamaño de la página y cualquier ordenamiento que se deba aplicar.

-   .map(DatosListadoMedico::new): Luego de obtener la página de médicos, estás utilizando el método map para transformar cada objeto Medico en la página en un objeto DatosListadoMedico usando el constructor que creaste en la clase DatosListadoMedico. Esto es una forma conveniente de simplificar los objetos Medico y seleccionar solo los campos necesarios para el listado.

-   return: Finalmente, el método devuelve la página de DatosListadoMedico. Esto significa que cuando alguien hace una petición GET a la ruta /medicos, obtendrá una página de resultados paginados de médicos en el formato simplificado definido en DatosListadoMedico.

En resumen, este método realiza una consulta paginada a la base de datos para obtener médicos y los transforma en objetos DatosListadoMedico que contienen solo la información relevante para mostrar en un listado. Luego, devuelve esta información paginada como respuesta a la petición GET.

## Continuando...

2. Compilamos y volvemos a realizar una peticion GET veremos que ahora nos lanza nuevas secciones como es la de "pageable" e información de la misma

3. pageable biene con valores por defecto los cuales vimos en el paso anterior en la respuesta del GET, para darle nostros valores de manera manual debemos hacer uso de las url props, para entender esto mejor en el mismo Insomnia colocaremos la url: `http://localhost:8080/medicos?size=2` con eso le estamos diciendo que queremos pages de un tamaño de 2 elementos por pagina

4. para gregar otra propiedad y ver de una mejor manera como funciona esto, podemos colocar la siguiente url en insomia: `http://localhost:8080/medicos?size=1&page=1` y luego `http://localhost:8080/medicos?size=1&page=2` cabe mencionar que estas empiezan a contar desde el 0.

# Ordenacion

1.  Por fines didacticos agregamos mas elementos a la tabla medicos, siendo un total de 6

2.  ahora necesitamos ordenar los datos retornados por nombre de manera ascendente

3.  vemos que entre las props de pages del json esta sort, asi que lo utilizamos el el url `http://localhost:8080/medicos?size=10&page=0&sort=nombre` y veremos como ahora nos retorna de manera ordenada los elementos de la tabla medicos

4.  en caso de que no queramos hacer tanto uso de las url params podemos modificar un poco mas el metodo de listado en el `MedicoController`, agregando un nuevo parametro:

        @GetMapping
        public Page<DatosListadoMedico> listadoMedicos(@PageableDefault(size = 10) Pageable paginacion) {
            return medicoRepository.findAll(paginacion).map(DatosListadoMedico::new);
        }

    vemos que ahora tiene un @PageableDefault, el cual es basicamente lo mismo que las url props pero cambia los valores por defecto, osea que si volvemos a hacer el GET de solo la url de `/medicos` en vez de ser 20 por defecto seran los 10 que colocamos en el codigo.

5.  si quisieramos ver las queries sql que se estan ejecutando cada vez que hacemos una peticion http con insomnia o thunder client, podemos ir a application.properties dentro de resources y agregar un par de lineas nuevas:

        spring.jpa.show-sql=true
        spring.jpa.properties.hibernate.format_sql=true

6.  claro que lo anterior solo es para ver que hace hibernate de manera interna, no se recomienda tener tantos logs activos ya que puede sobrecargar la consola asi que queda como una herramienta util para debbug y cosas por el estilo.

## Notas adicionales

Como aprendimos en videos anteriores, por defecto, los parámetros utilizados para realizar la paginación y el ordenamiento deben llamarse page, size y order. Sin embargo, Spring Boot permite modificar los nombres de dichos parámetros a través de la configuración en el archivo application.properties.

Por ejemplo, podríamos traducir al español los nombres de estos parámetros con las siguientes propiedades:

    spring.data.web.pageable.page-parameter=pagina
    spring.data.web.pageable.size-parameter=tamano
    spring.data.web.sort.sort-parameter=ordenCOPIA

Por lo tanto, en solicitudes que usen paginación, debemos usar estos nombres que fueron definidos. Por ejemplo, para listar los médicos de nuestra API trayendo solo 5 registros de la página 2, ordenados por email y en orden descendente, la URL de solicitud debe ser:

    http://localhost:8080/medicos?tamano=5&pagina=1&orden=email,desc

# Request PUT

Ahora se nos da la tarea de actualizar a los medicos, siendo que solo se permitira realizar cambios en los nombres, documentos y direccion. Pero no se permitira cambiar el email, especialidad y documento.

1.  Para comenzar sabemos que necesitamos del id para saber a que elemento o en este caso medico vamos a editar asi que vamos a `DatosListadoMedico.java` y vamos a agregar el id:

        package med.voll.api.medico;

        public record DatosListadoMedico(
                Long id,
                String nombre,
                String especialidad,
                String documento,
                String email) {

            public DatosListadoMedico(Medico medico) {
                this(
                        medico.getId(),
                        medico.getNombre(),
                        medico.getEspecialidad().toString(),
                        medico.getDocumento(),
                        medico.getEmail());
            }

        }

2.  realizamos un GET para ver que todo este en orden.

# Actualizando datos

1.  Ahora necesitamos otro DTO para poder hacer el PUT en `MedicoController.java`, pero antes de eso vamos a crear un nuevo record de nombre `DatosActualizarMedico.java` dentro del paquete medico

2.  Agregamos el siguiente codigo:

        package med.voll.api.medico;

        import jakarta.validation.constraints.NotNull;

        import med.voll.api.direccion.DatosDireccion;

        public record DatosActualizarMedico(
                @NotNull Long id,

                String nombre,

                String documento,

                DatosDireccion direccion) {
        }

    Siendo solo el id el unico dato que no puede ser nulo, ya que sin el no sabriamos a quien debemos modificar

3.  Ahora si vamos al `MedicoController.java` para ahora si crear el metodo de PUT

            @PutMapping
            public void actualizarMedico(@RequestBody @Valid DatosActualizarMedico datosActualizarMedico) {
                Medico medico = medicoRepository.getReferenceById(datosActualizarMedico.id());
                medico.actualizarDatos(datosActualizarMedico);
            }

4.  antes de explicar lo anterior vemos que creamos un nuevo metodo en `Medico.java` por lo que vamos a completarlo, quedando de la siguiente manera:

        public void actualizarDatos(DatosActualizarMedico datosActualizarMedico) {

                if (datosActualizarMedico.nombre() != null) {
                    this.nombre = datosActualizarMedico.nombre();
                }

                if (datosActualizarMedico.documento() != null) {
                    this.documento = datosActualizarMedico.documento();
                }

                if (datosActualizarMedico.direccion() != null) {
                    this.direccion = direccion.actualizarDatos(datosActualizarMedico.direccion());
                }

            }

5.  también vemos que agregamos un nuevo metodo dentro de `Direccion.java`, por lo que vamos ahi a terminarlo quedando de la siguiente manera:

        public Direccion actualizarDatos(DatosDireccion direccion) {
                this.calle = direccion.calle();
                this.numero = direccion.numero();
                this.distrito = direccion.distrito();
                this.complemento = direccion.complemento();
                this.ciudad = direccion.ciudad();
                return this;
            }

### Explicacion del codigo:

    @PutMapping
    public void actualizarMedico(@RequestBody @Valid DatosActualizarMedico datosActualizarMedico) {
        Medico medico = medicoRepository.getReferenceById(datosActualizarMedico.id());
        medico.actualizarDatos(datosActualizarMedico);
    }

-   @PutMapping: Esta anotación indica que el método maneja peticiones HTTP PUT en la ruta /medicos. Cuando se accede a esa ruta con una petición PUT, este método será invocado.

-   public void actualizarMedico(@RequestBody @Valid DatosActualizarMedico datosActualizarMedico): Este es el método actualizarMedico, que recibe como parámetro un objeto DatosActualizarMedico. La anotación @RequestBody indica que el objeto datosActualizarMedico proviene del cuerpo de la solicitud HTTP, y @Valid indica que se deben aplicar las validaciones de validación de Bean antes de procesar el objeto.

-   Medico medico = medicoRepository.getReferenceById(datosActualizarMedico.id()): Aquí estás utilizando el medicoRepository para obtener una referencia al objeto Medico correspondiente al ID proporcionado en datosActualizarMedico. Al igual que antes, estás obteniendo una referencia en lugar de cargar todos los datos de la base de datos.

-   medico.actualizarDatos(datosActualizarMedico): Una vez que tienes la referencia al objeto Medico, estás llamando al método actualizarDatos en ese objeto y pasando datosActualizarMedico como argumento. Esto permitirá actualizar los datos del objeto Medico con la información proporcionada en datosActualizarMedico.

#### Ahora, veamos los métodos generados:

    public void actualizarDatos(DatosActualizarMedico datosActualizarMedico) {

        if (datosActualizarMedico.nombre() != null) {
            this.nombre = datosActualizarMedico.nombre();
        }

        if (datosActualizarMedico.documento() != null) {
            this.documento = datosActualizarMedico.documento();
        }

        if (datosActualizarMedico.direccion() != null) {
            this.direccion = direccion.actualizarDatos(datosActualizarMedico.direccion());
        }

    }

`Medico.java` - La utilización de los condicionales if en el método actualizarDatos tiene como objetivo principal evitar que se sobrescriban los campos con valores nulos en el objeto Medico.

    if (datosActualizarMedico.nombre() != null) {
        this.nombre = datosActualizarMedico.nombre();
    }

En este bloque de código, se verifica si `datosActualizarMedico.nombre()` es distinto de null. Si es cierto (es decir, si se proporcionó un nuevo nombre en `datosActualizarMedico`), se actualiza el campo nombre del objeto Medico con el valor del nuevo nombre.

    if (datosActualizarMedico.documento() != null) {
        this.documento = datosActualizarMedico.documento();
    }

Este bloque sigue una lógica similar al anterior. Si `datosActualizarMedico.documento()` no es `null`, el campo documento del objeto Medico se actualiza con el nuevo valor proporcionado en datosActualizarMedico.

    if (datosActualizarMedico.direccion() != null) {
        this.direccion = direccion.actualizarDatos(datosActualizarMedico.direccion());
    }

Aquí es donde se produce una actualización más compleja. Si `datosActualizarMedico.direccion()` no es null, se llama al método`direccion.actualizarDatos(datosActualizarMedico.direccion())`. Esto permite actualizar los campos de dirección del objeto Medico con los nuevos valores proporcionados en `datosActualizarMedico.direccion()`. Es decir, la dirección del médico se actualiza solo si se proporciona una nueva dirección en datosActualizarMedico.

En resumen, la utilización de condicionales if en este método permite actualizar selectivamente los campos del objeto Medico solo si se proporcionan nuevos valores en el objeto DatosActualizarMedico. Esto evita la sobrescritura accidental de campos con valores nulos y asegura que solo se actualicen los campos específicos que se proporcionen en la solicitud de actualización.

`Direccion.java` - public Direccion actualizarDatos(DatosDireccion direccion): Este método en la clase Direccion actualiza los datos de dirección con la información proporcionada en direccion. Al igual que en Medico, esto te permite actualizar solo los campos específicos de la dirección.

En resumen, este conjunto de métodos y clases te permite actualizar los datos de un médico de manera selectiva. Solo se actualizan los campos que se proporcionan en datosActualizarMedico y DatosDireccion. Esto es útil para evitar la sobrescritura de datos no proporcionados y mantener un control preciso sobre las actualizaciones.

## Continuando...

6.  Compilamos y revisamos realizando una peticion PUT como la siguiente:

        {
        "id": "5",
        "nombre": "Dipper"
        }

7.  Notaremos en la base de datos que no funciona el PUT pese a que nos marca estatus 200, esto es porque hace falta algo más en el metodo PUT dentro de `MedicoController.java`:

        @PutMapping
        @Transactional
        public void actualizarMedico(@RequestBody @Valid DatosActualizarMedico datosActualizarMedico) {
            Medico medico = medicoRepository.getReferenceById(datosActualizarMedico.id());
            medico.actualizarDatos(datosActualizarMedico);
        }

### Explicacion del codigo:

El uso de la anotación @Transactional es importante cuando trabajas con operaciones que involucran múltiples interacciones con la base de datos, como actualizaciones, inserciones o eliminaciones. Esta anotación garantiza la consistencia de los datos y el comportamiento transaccional.

Cuando una operación está anotada con @Transactional, Spring crea una transacción alrededor del método. Si la operación se completa exitosamente, la transacción se confirma y los cambios realizados en la base de datos se guardan permanentemente. Si ocurre un error o excepción, la transacción se revierte, lo que significa que los cambios no se aplicarán a la base de datos.

En el contexto de tu método actualizarMedico, el uso de @Transactional es importante porque estás interactuando con la base de datos al recuperar el objeto Medico y luego llamando al método actualizarDatos. Si no se usa @Transactional, los cambios realizados en la base de datos podrían no ser consistentes si ocurre un error durante la actualización.

Al agregar @Transactional a tu método actualizarMedico, estás asegurando que todas las operaciones de la base de datos dentro del método se realicen en una única transacción. Si la operación es exitosa, los cambios se confirman; si hay un error, los cambios se revierten, evitando así inconsistencias en los datos.

En resumen, @Transactional se utiliza para mantener la integridad y la coherencia de los datos cuando se trabaja con operaciones de base de datos. Asegura que las operaciones se realicen en una transacción, lo que garantiza que los cambios se apliquen correctamente o se reviertan si es necesario.

## Continuando...

8. Volvemos a compilar y probamos el PUT y veremos que esta vez funciona el cambio de nombre.

# Notas adicionales

`Mass Assignment Attack` o Ataque de asignación masiva, en español, ocurre cuando un usuario logra inicializar o reemplazar parámetros que no deben ser modificados en la aplicación. Al incluir parámetros adicionales en una solicitud, si dichos parámetros son válidos, un usuario malintencionado puede generar un efecto secundario no deseado en la aplicación.

El concepto de este ataque se refiere a cuando inyectas un conjunto de valores directamente en un objeto, de ahí la asignación masiva de nombres, que sin la debida validación puede causar serios problemas.

Tomemos un ejemplo práctico. Suponga que tiene el siguiente método, en una clase Controller, utilizado para registrar un usuario en la aplicación:

    @PostMapping
    @Transactional
    public void registrar(@RequestBody @Valid Usuario usuario) {
        repository.save(usuario);
    }

Y la entidad JPA que representa al usuario:

    @Getter
    @Setter
    @NoArgsConstructor
    @EqualsAndHashCode(of = "id")
    @Entity(name = "Usuario")
    @Table(name = "usuarios")
    public class Usuario {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String nombre;
        private String email;
        private Boolean admin = false;

        //restante del código omitido…
    }

Observe que el atributo admin de la clase Usuario se inicializa como falso, lo que indica que un usuario siempre debe estar registrado como administrador. Sin embargo, si se envía el siguiente JSON en la solicitud:

    {
        “nombre” : “Rodrigo”,
        “email” : “rodrigo@email.com”,
        “admin” : true
    }

El usuario se registrará con el atributo admin con valor true. Esto sucede porque el atributo admin enviado en el JSON existe en la clase que se está recibiendo en el Controller, considerándose un atributo válido y que se llenará en el objeto Usuario que será instanciado por Spring.

Entonces, ¿cómo prevenimos este problema?

## Prevención

El uso del patrón DTO nos ayuda a evitar este problema, ya que al crear un DTO definimos solo los campos que se pueden recibir en la API, y en el ejemplo anterior el DTO no tendría el atributo admin.

Nuevamente, vemos una ventaja más de usar el patrón DTO para representar los datos que entran y salen de la API.

# Coninuacion en notas3.md
