Si bien el CRUD funciona, hay temas de seguridad y estandarización que son importantes a tratar, ya sea por que son necesarios o son estandares ya puestos en la industria.

# Estandarizando retornos API

1.  Comencemos por modificar el DELETE, cuando nosotros lo ocupamos recibimos un 200 de estatus en nuestro thunder client o insomnia, pero lo ideal es que sea un 204 ya que el metodo DELETE no retorna nada como tal

2.  Vamos a `MedicoController.java` y modificamos el metodo con ayuda de spring:

        @DeleteMapping("/{id}")
        @Transactional
        // Eliminacion logica
        public ResponseEntity<Void> eliminarMedico(@PathVariable Long id) {
            Medico medico = medicoRepository.getReferenceById(id);
            medico.desactivarMedico();
            return ResponseEntity.noContent().build();
        }

### Explicacion del codigo:

-   @PathVariable Long id: Este parámetro anotado con @PathVariable indica que el valor del parámetro id se obtendrá de la URL. Especificar Long como tipo del parámetro indica que se espera un valor numérico largo en la URL.

-   Medico medico = medicoRepository.getReferenceById(id): Aquí, obtienes una referencia al objeto Medico correspondiente al id proporcionado. Esta es una operación ligera que no carga todo el objeto en memoria, sino que crea una referencia proxy al objeto en la base de datos.

-   medico.desactivarMedico(): Llamas al método desactivarMedico() en el objeto Medico obtenido. Esto cambia el estado del médico a "inactivo" utilizando la eliminación lógica que has implementado previamente.

-   return ResponseEntity.noContent().build(): En este punto, has realizado con éxito la eliminación lógica del médico. Usas ResponseEntity.noContent() para indicar que la respuesta no tiene cuerpo (no necesitas enviar datos adicionales). Luego, build() construye la instancia de ResponseEntity que se enviará como respuesta.

## Continuamos...

3.  Compilamos y ejecutamos el DELETE, veremos que ahora nos retorna el 204

4.  Ahora por ejemplo en el PUT podriamos necesitar retornar al medico, pero como se ha dicho antes no es nada bueno retornar como tal la entidad medico, asi que vamos a crear un DTO que nos ayude con esto, para ello crearemos un record de nombre `DatosRespuestaMedico.java`:

        package med.voll.api.medico;

        import med.voll.api.direccion.DatosDireccion;

        public record DatosRespuestaMedico(
                Long id,
                String nombre,
                String mail,
                String telefono,
                String documento,
                Especialidad especialidad,
                DatosDireccion direccion) {

            public DatosRespuestaMedico(Medico medico, DatosDireccion direccion) {
                    this(medico.getId(), medico.getNombre(), medico.getEmail(),
                                    medico.getTelefono(), medico.getDocumento(),
                                    medico.getEspecialidad(), direccion);
            }
        }

5.  Ahora volvemos a `MedicoController` y modificamos el metodo PUT:

        @PutMapping
        @Transactional
        public ResponseEntity<DatosRespuestaMedico> actualizarMedico(
                @RequestBody @Valid DatosActualizarMedico datosActualizarMedico) {
            Medico medico = medicoRepository.getReferenceById(datosActualizarMedico.id());
            medico.actualizarDatos(datosActualizarMedico);

            DatosRespuestaMedico respuestaMedico = new DatosRespuestaMedico(
                    medico, // Aquí pasas directamente el objeto Medico
                    new DatosDireccion(medico.getDireccion().getCalle(), medico.getDireccion().getDistrito(),
                            medico.getDireccion().getCiudad(), medico.getDireccion().getNumero(),
                            medico.getDireccion().getComplemento()));

            return ResponseEntity.ok(respuestaMedico);
        }

### Explicacion de los codigos:

#### Método DatosRespuestaMedico (Constructor con encadenamiento):

En esta parte, estás creando una clase de datos (record) llamada DatosRespuestaMedico. En Java, los records tienen la capacidad de generar constructores automáticos, pero también puedes personalizarlos. En este caso, estás utilizando un constructor personalizado que utiliza el encadenamiento de constructores.

    public record DatosRespuestaMedico(
            Long id,
            String nombre,
            String mail,
            String telefono,
            String documento,
            Especialidad especialidad,
            DatosDireccion direccion) {

        public DatosRespuestaMedico(Medico medico, DatosDireccion direccion) {
            this(medico.getId(), medico.getNombre(), medico.getEmail(),
                medico.getTelefono(), medico.getDocumento(),
                medico.getEspecialidad(), direccion);
        }
    }

-   Has creado un registro llamado DatosRespuestaMedico con varios campos para almacenar información sobre un médico en formato de respuesta.

-   Has creado un constructor personalizado DatosRespuestaMedico(Medico medico, DatosDireccion direccion) que acepta un objeto Medico y una instancia de DatosDireccion. Esto te permite crear un DatosRespuestaMedico utilizando un objeto Medico ya existente y los datos de dirección proporcionados.

-   Dentro de este constructor personalizado, estás utilizando el constructor principal de DatosRespuestaMedico (el generado automáticamente por el record) para inicializar los campos con los valores adecuados del objeto Medico y la instancia de DatosDireccion.

#### Método actualizarMedico del Controlador:

Ahora, veamos cómo se aplica el constructor con encadenamiento en tu método actualizarMedico del controlador.

    @PutMapping
    @Transactional
    public ResponseEntity<DatosRespuestaMedico> actualizarMedico(@RequestBody @Valid DatosActualizarMedico datosActualizarMedico) {
        Medico medico = medicoRepository.getReferenceById(datosActualizarMedico.id());
        medico.actualizarDatos(datosActualizarMedico);

        DatosRespuestaMedico respuestaMedico = new DatosRespuestaMedico(
                medico, // Aquí pasas directamente el objeto Medico
                new DatosDireccion(medico.getDireccion().getCalle(), medico.getDireccion().getDistrito(),
                        medico.getDireccion().getCiudad(), medico.getDireccion().getNumero(),
                        medico.getDireccion().getComplemento()));

        return ResponseEntity.ok(respuestaMedico);
    }

-   En este método, estás recibiendo una solicitud PUT para actualizar la información de un médico.
    Obtienes una referencia al objeto Medico utilizando medicoRepository.getReferenceById(datosActualizarMedico.id()).

-   Luego, llamas al método actualizarDatos del objeto Medico, que actualiza los campos del médico con la información proporcionada en datosActualizarMedico.

-   Luego, estás creando una instancia de DatosRespuestaMedico utilizando el constructor con encadenamiento que creamos anteriormente. Aquí, pasas directamente el objeto Medico y una instancia de DatosDireccion generada con los datos de dirección del médico.

-   Finalmente, estás devolviendo una respuesta HTTP 200 OK con la instancia de DatosRespuestaMedico creada como parte del cuerpo de la respuesta.

En resumen, el uso del constructor con encadenamiento y la creación de una instancia de DatosRespuestaMedico directamente a partir de un objeto Medico te permite simplificar y limpiar tu código, evitando la duplicación de información y mejorando la legibilidad.

# Devolviendo el codigo 201

1.  Ahora nos toca modificar el POST, donde debemos retornar un 201 Created, para ello debemos hacer ciertos cambios

2.  En `MedicoController` vamos a modificar el POST y de paso volveremos a hacer uso de DatosRespuestaMedico:

        @PostMapping
        public ResponseEntity<DatosRespuestaMedico> registrarMedico(
                @RequestBody @Valid DatosRegistroMedico datosRegistroMedico,
                UriComponentsBuilder uriComponentsBuilder) {
            Medico medico = medicoRepository.save(new Medico(datosRegistroMedico));
            // * Return 201 Created */
            // * URL donde encontrar al medico */
            // * GET http://localhost:8080/medicos/{medico_id} */
            DatosRespuestaMedico respuestaMedico = new DatosRespuestaMedico(
                    medico, // Aquí pasas directamente el objeto Medico
                    new DatosDireccion(medico.getDireccion().getCalle(), medico.getDireccion().getDistrito(),
                            medico.getDireccion().getCiudad(), medico.getDireccion().getNumero(),
                            medico.getDireccion().getComplemento()));

            URI url = uriComponentsBuilder.path("medicos/{id}").buildAndExpand(medico.getId()).toUri();
            return ResponseEntity.created(url).body(respuestaMedico);
        }

### Explicacion del codigo:

-   @PostMapping: Anota el método para que sea invocado cuando se recibe una solicitud HTTP POST en la ruta especificada.

-   @RequestBody @Valid DatosRegistroMedico datosRegistroMedico: Anota el parámetro datosRegistroMedico para que se deserialice automáticamente desde el cuerpo JSON de la solicitud. @Valid indica que se deben aplicar las validaciones de javax.validation en el objeto deserializado.

-   UriComponentsBuilder uriComponentsBuilder: Este parámetro permite construir URIs para su uso en la respuesta.

-   Medico medico = medicoRepository.save(new Medico(datosRegistroMedico));: Aquí, creas un nuevo objeto Medico a partir de los datos recibidos en la solicitud y lo guardas en la base de datos usando el repositorio.

-   DatosRespuestaMedico respuestaMedico = new DatosRespuestaMedico(...);: Creas un objeto DatosRespuestaMedico que contiene la información del nuevo médico y su dirección. Esto será utilizado en la respuesta.

-   URI url = uriComponentsBuilder.path("medicos/{id}").buildAndExpand(medico.getId()).toUri();: Construyes una URI para el médico recién registrado utilizando uriComponentsBuilder. La plantilla path("medicos/{id}") indica cómo se debe formar la URI, y buildAndExpand(medico.getId()) completa la plantilla con el ID del médico recién registrado.

-   return ResponseEntity.created(url).body(respuestaMedico);: Aquí, creas una respuesta ResponseEntity con el código de estado 201 (Created), indicando que la operación de registro ha tenido éxito. Además, estableces la cabecera Location en la URI del médico registrado para que el cliente pueda encontrar la nueva ubicación del recurso. El cuerpo de la respuesta incluye el objeto respuestaMedico que contiene los detalles del médico recién registrado.

### Que es una URI?

Una URI (Uniform Resource Identifier) es una cadena de caracteres que identifica un recurso en la web de manera única. En términos más sencillos, una URI es una dirección que se utiliza para localizar recursos en internet, como páginas web, imágenes, archivos, servicios web, entre otros.

#### Existen dos tipos principales de URI:

-   URL (Uniform Resource Locator): Es una forma común de URI que proporciona la dirección exacta de un recurso en internet. Una URL consta de varios componentes, como el esquema (http, https, ftp, etc.), el dominio o dirección IP del servidor, el puerto, la ruta y los parámetros. Por ejemplo, https://www.ejemplo.com/pagina.

-   URN (Uniform Resource Name): Es una forma de URI que identifica un recurso de manera única pero no proporciona su ubicación exacta. En cambio, se centra en dar un nombre significativo al recurso, independientemente de dónde se encuentre. Por ejemplo, urn:isbn:0451450523 podría identificar un libro mediante su número ISBN.

En el contexto del desarrollo web y de APIs, las URIs se utilizan para acceder a recursos y servicios a través de los métodos HTTP (GET, POST, PUT, DELETE, etc.). En el ejemplo que mencionaste anteriormente, la URI se utiliza para construir la dirección donde se encuentra el recurso del médico recién registrado en tu aplicación.

En resumen, una URI es una cadena de texto que identifica de manera única un recurso en internet. Puede ser una dirección completa (URL) o simplemente un nombre identificativo (URN), y es esencial para acceder a recursos en la web y en aplicaciones.

## Continuamos...

3. Compilamos y realizamos una petición POST y veremos que ahora nos retorna el 201 Created y el body con los datos de `DatosRespuestaMedico`.

## Notas Extras

### Para saber más: códigos de protocolo HTTP

El protocolo HTTP (Hypertext Transfer Protocol, RFC 2616) es el protocolo encargado de realizar la comunicación entre el cliente, que suele ser un navegador, y el servidor. De esta forma, para cada “solicitud” realizada por el cliente, el servidor responde sí tuvo éxito o no. Si no tiene éxito, la mayoría de las veces, la respuesta del servidor será una secuencia numérica acompañada de un mensaje. Si no sabemos qué significa el código de respuesta, difícilmente sabremos cuál es el problema, por eso es muy importante saber qué son los códigos HTTP y qué significan.

### Categoría de código

Los códigos HTTP (o HTTPS) tienen tres dígitos, y el primer dígito representa la clasificación dentro de las cinco categorías posibles.

-   1XX: Informativo: la solicitud fue aceptada o el proceso aún está en curso;
-   2XX: Confirmación: la acción se completó o se comprendió;
-   3XX: Redirección: indica que se debe hacer o se debió hacer algo más para completar la solicitud;
-   4XX: Error del cliente: indica que la solicitud no se puede completar o contiene una sintaxis incorrecta;
-   5XX: Error del servidor: el servidor falló al concluir la solicitud.

### Principales códigos de error.

Como se mencionó anteriormente, conocer los principales códigos de error HTTP lo ayudará a identificar problemas en sus aplicaciones, además de permitirle comprender mejor la comunicación de su navegador con el servidor de la aplicación a la que intenta acceder.

#### Error 403

El código 403 es el error "Prohibido". Significa que el servidor entendió la solicitud del cliente, pero se niega a procesarla, ya que el cliente no está autorizado para hacerlo.

#### Error 404

Cuando ingresa una URL y recibe un mensaje de Error 404, significa que la URL no lo llevó a ninguna parte. Puede ser que la aplicación ya no exista, que la URL haya cambiado o que haya ingresado una URL incorrecta.

#### Error 500

Es un error menos común, pero aparece de vez en cuando. Este error significa que hay un problema con una de las bases que hace que se ejecute una aplicación. Básicamente, este error puede estar en el servidor que mantiene la aplicación en línea o en la comunicación con el sistema de archivos, que proporciona la infraestructura para la aplicación.

#### Error 503

El error 503 significa que el servicio al que se accede no está disponible temporalmente. Las causas comunes son un servidor que está fuera de servicio por mantenimiento o sobrecargado. Los ataques maliciosos como DDoS causan mucho este problema.

#### Un consejo final:

Difícilmente podemos guardar en nuestra cabeza lo que significa cada código, por lo que hay sitios web en Internet que tienen todos los códigos y significados para que podamos consultar cuando sea necesario. Hay dos sitios muy conocidos que usan los desarrolladores, uno para cada preferencia: si te gustan los gatos, puedes usar HTTP Cats; ya, si prefieres perros, usa HTTP Dogs.

# Detallando los datos de API

Pensariamos que con lo realizado en el POST de generar las URI podriamos acceder a estas con GET, pues la la respuesta es si y no, podriamos acceder por que las url estan creadas, pero no podemos ya que no contamos con un "permiso" a estas, por lo que debemos crear un nuevo metodo GET para poder acceder a estas

1.  Vamos a `MedicoController.java` y en base al DELETE creamos el metodo GET:

        @GetMapping("/{id}")
        public ResponseEntity<DatosRespuestaMedico> retornaDatosMedico(@PathVariable Long id) {
            Medico medico = medicoRepository.getReferenceById(id);

            var respuestaMedico = new DatosRespuestaMedico(
                    medico, // Aquí pasas directamente el objeto Medico
                    new DatosDireccion(medico.getDireccion().getCalle(), medico.getDireccion().getDistrito(),
                            medico.getDireccion().getCiudad(), medico.getDireccion().getNumero(),
                            medico.getDireccion().getComplemento()));

            return ResponseEntity.ok(respuestaMedico);
        }

### Explicacion del codigo:

-   @GetMapping("/{id}"): Esta anotación indica que este método manejará las peticiones HTTP GET dirigidas a la ruta /medicos/{id}, donde {id} es una variable que captura el valor del ID del médico en la URL.

-   public `ResponseEntity<DatosRespuestaMedico>` retornaDatosMedico(@PathVariable Long id): Aquí defines el método. Utilizas @PathVariable para capturar el valor del ID pasado en la URL y lo asignas a la variable id. El tipo de retorno es `ResponseEntity<DatosRespuestaMedico>`, que es una respuesta HTTP que contendrá los datos del médico en el cuerpo.

-   Medico medico = medicoRepository.getReferenceById(id);: Aquí obtienes el objeto Medico correspondiente al ID proporcionado. Utilizas el método getReferenceById del repositorio para buscar el médico en la base de datos.

-   var respuestaMedico = new DatosRespuestaMedico(...): Creas una instancia de DatosRespuestaMedico para preparar los datos que se enviarán como respuesta. Aquí estás construyendo un objeto que contiene información del médico y su dirección.

-   return ResponseEntity.ok(respuestaMedico);: Finalmente, creas una respuesta HTTP 200 OK utilizando ResponseEntity.ok(). En el cuerpo de la respuesta, colocas el objeto respuestaMedico, que contiene los detalles del médico y su dirección.

En resumen, este método toma un ID de médico de la URL, busca el médico en la base de datos, crea una respuesta con los datos del médico y su dirección, y devuelve la respuesta HTTP 200 OK con esa información en el cuerpo. Esto permite que los clientes de la API obtengan los detalles de un médico específico utilizando su ID.

### Explicacion extra:

Como podemos notar estamos haciendo una y otra vez la parte de:

    DatosRespuestaMedico respuestaMedico = new DatosRespuestaMedico(
                    medico, // Aquí pasas directamente el objeto Medico
                    new DatosDireccion(medico.getDireccion().getCalle(), medico.getDireccion().getDistrito(),
                            medico.getDireccion().getCiudad(), medico.getDireccion().getNumero(),
                            medico.getDireccion().getComplemento()));

Aun que en el caso anterior usamos var, pero es exactamente lo mismo, para evitar eso mejor abstraemos dicha parte en un metodo nuevo dentro del mismo Controller:

    // * De esta manera, estás separando la lógica de construcción de la respuesta
    // * en el controlador, lo que hace que el código sea más organizado y mantenga la
    // * coherencia en la estructura.
    private DatosRespuestaMedico construirDatosRespuestaMedico(Medico medico) {
        DatosDireccion direccion = new DatosDireccion(
                medico.getDireccion().getCalle(),
                medico.getDireccion().getDistrito(),
                medico.getDireccion().getCiudad(),
                medico.getDireccion().getNumero(),
                medico.getDireccion().getComplemento());

        return new DatosRespuestaMedico(medico, direccion);
    }

Ahora simplemente en cada metodo anterior que hemos modificado debemos cambiarlos de la siguiente manera:

    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaMedico> retornaDatosMedico(@PathVariable Long id) {
        Medico medico = medicoRepository.getReferenceById(id);
        DatosRespuestaMedico respuestaMedico = construirDatosRespuestaMedico(medico);
        return ResponseEntity.ok(respuestaMedico);
    }

mas simple y facil de leer 🦆

## Continuamos...

4.  Compilamos lo anterior para verificar que todo este en orden

5.  Solo nos queda modificar el GET que lista todos los medicos pero esto ya es algo más sencillo, asi que podemos hacer lo siguiente:

        @GetMapping
        public ResponseEntity<Page<DatosListadoMedico>> listadoMedicos(@PageableDefault(size = 10) Pageable paginacion) {
            // return medicoRepository.findAll(paginacion).map(DatosListadoMedico::new);

            // despues de agregar el metodo Delete Logico...
            return ResponseEntity.ok(medicoRepository.findByActivoTrue(paginacion).map(DatosListadoMedico::new));
        }

6.  Compilamos y veremos que todo esta en orden

# Tratando los errores de API

Como hemos visto a lo largo de esto los errores que nos lanza thunder o insomnia son muy extensos a cuento explicación o contenido y no vamos a querer cosas asi y menos para que el cliente lo pueda ver, asi que tenemos que mejorar ese apartado y el como se manejan los errores.

1.  Vamos a esta pagina: `https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html` la cual contiene toda la lista de properties que podemos colocar en resources > application.properties

2.  En la seccion `Server properties` encontraremos la opción de `server.error.include-stacktrace` y más adelante el valor que le podemos dar el cual es `never`

3.  La copiamos y pegamos en nuestro `application.properties` y le damos el valor de never:

        server.error.include-stacktrace=never

4.  Compilamos y hacemor alguna de nuestras peticiones pero a modo que fallen y veremos que ahora ya no nos muestra toda la información del error más que un par de mensajes al respecto, por ejemplo un GET de medico fallido:

        {
            "timestamp": "2023-08-19T16:33:34.503+00:00",
            "status": 500,
            "error": "Internal Server Error",
            "message": "Unable to find med.voll.api.medico.Medico with id 70",
            "path": "/medicos/70"
        }

    Bastante similar a cuando usamos try-catch y solo mostramos el mensaje de error

# Tratando el error 404

Recordemos que es mejor validar los errores a nivel cliente osea "400" y no a nivel servidor como es el caso anterior que es "500", aún más ideal seria mandar un error 404 debido a que es por que no se encontro el valor buscado

1. Primero que nada vamos a crear un nuevo paquete de nombre `domain` y vamos a mover los paquetes de medico y direccion dentro de este

### Explicacion de lo anterior:

#### Tratamiento de Errores:

Al manejar errores, es importante proporcionar respuestas claras y específicas a los diferentes tipos de errores que puedan ocurrir. Usar excepciones y códigos de estado HTTP adecuados (como 404 para "no encontrado" y 500 para "error interno del servidor") es parte de una buena práctica en el diseño de API.

#### Evitando Try-Catch en el Controlador:

La razón por la que se sugiere evitar el uso excesivo de bloques try-catch en los controladores es que puede hacer que el código se vuelva más complicado y difícil de mantener. Además, capturar y manejar excepciones en cada método puede llevar a la duplicación de código. En cambio, se recomienda usar un mecanismo centralizado para manejar errores.

#### Dominio y Controladores Separados:

Al mover las clases "Medico" y "Direccion" al paquete "dominio", estás creando una separación clara entre la lógica de negocio (dominio) y la lógica de presentación y controladores. Esto hace que el código sea más modular y permite que el paquete "api" se enfoque en manejar las solicitudes y respuestas de la API.

#### Manejo Centralizado de Errores:

Al crear un paquete llamado "dominio" en el que puedes manejar y lanzar excepciones específicas para tu lógica de negocio, puedes usar un mecanismo centralizado para manejar estos errores. Esto puede ser a través de un "manejador de excepciones" que convierte las excepciones en respuestas HTTP apropiadas, como 404 o 500.

## Continuando...

2. Ahora creamos un nuevo paquete dentro de voll.api de nombre `infra` y dentro de este creamos la clase `TratadorDeErrores.java`, por que queremos controlar los errores de manera `global`

#### Mini Nota:

Para entender mejor algunos aspectos de `TratadorDeErrores` se recomienda investigar más sobre `Programación orientada a aspectos`

## Continuamos...

3.  Ahora iremos modificando TratadorDeErrores de la siguiente manera:

        package med.voll.api.infra;

        import jakarta.persistence.EntityNotFoundException;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.ExceptionHandler;
        import org.springframework.web.bind.annotation.RestControllerAdvice;

        @RestControllerAdvice
        public class TratadorDeErrores {

            @ExceptionHandler(EntityNotFoundException.class)
            public ResponseEntity<Void> tratarError404() {
                return ResponseEntity.notFound().build();
            }

        }

### Explicacion del codigo:

#### Método tratarError404:

Este método es un manejador de excepciones que se encarga de manejar las excepciones del tipo EntityNotFoundException. Esta excepción se lanza cuando una entidad (por ejemplo, un objeto "Medico") no se encuentra en la base de datos.

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Void> tratarError404() {
        return ResponseEntity.notFound().build();
    }

-   @ExceptionHandler(EntityNotFoundException.class): Esta anotación indica que este método manejará excepciones del tipo EntityNotFoundException. Cuando se lanza esta excepción en algún lugar de tu aplicación, Spring MVC redirigirá la ejecución a este método para manejarla.

-   `public ResponseEntity<Void> tratarError404()`: Este método devuelve un ResponseEntity con un código de estado 404 ("no encontrado"). No se necesita un cuerpo en la respuesta (Void), ya que simplemente estás indicando que el recurso solicitado no existe.

-   return ResponseEntity.notFound().build();: Aquí estás construyendo y devolviendo una respuesta con el código de estado 404 utilizando ResponseEntity.notFound(). El método .build() finaliza la construcción de la respuesta.

### Programación Orientada a Entidades:

La programación orientada a entidades es un enfoque en el diseño y desarrollo de software que se centra en modelar los conceptos y objetos del mundo real como "entidades". En el contexto de una aplicación de gestión médica, una "entidad" podría ser un "Medico", una "Direccion", etc.

Al utilizar la programación orientada a entidades, se busca modelar la estructura y el comportamiento de estas entidades de manera coherente y organizada. Las entidades generalmente contienen atributos que representan sus propiedades y métodos que definen su comportamiento. Esto facilita la encapsulación, la reutilización y el mantenimiento del código.

En el contexto de tu aplicación, las clases como "Medico" y "Direccion" son entidades. Estas entidades encapsulan la información y el comportamiento relacionados con médicos y direcciones. Al utilizar estas entidades en tu diseño, estás siguiendo un enfoque orientado a entidades para modelar y organizar la lógica de tu aplicación. El uso de excepciones y el manejo de errores global que has implementado en TratadorDeErrores es una buena práctica para mantener la coherencia y la robustez de tu aplicación en situaciones de error.

## Continuamos...

4. Si compilamos y volvemos a probar el GET de medicos con id que no exista ya nos devolvera un 404 como esperabamos por el metodo antes creado.

# Tratando el error 400

Vamos a ver nuestra peticion POST y hagamosla fallar a proposito, si bien da un estatus de 400 lo cual es lo que queremos, el cuerpo del error es donde esta el problema ya que es un codigo relativamente extenso y que no es algo que los usuasios comunes puedan entender, asi que tenemos que arreglar eso

1.  dentro de `TratadorDeErrores.java` vamos a agregar un nuevo metodo para eso:

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Void> tratarError400() {
            return ResponseEntity.badRequest().build();
        }

2.  Compilamos volvemos a probar el POST y veremos que ya solo nos lanza el error 400 a secas

3.  Pero tampoco seria bueno que el usuario no sepa donde o como se provoca el error, asi que vamos a corregir eso

4.  Para decirle donde se ha equivocado vamos a retornar un body, modificando el metodo anterior de la siguiente manera:

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<List<DatosErrorValidacion>> tratarError400(MethodArgumentNotValidException e) {
            var errores = e.getFieldErrors().stream().map(DatosErrorValidacion::new).toList();
            return ResponseEntity.badRequest().body(errores);
        }

5.  Pero lo anterior generara que se lance una vez más toda la lista de errores que en un principo queriamos evitar, pero ahora podemos hacer uso de un DTO para solo tomar lo que realmente queremos, dentro de la misma clase crearemos dicho DTO quedando de la siguiente manera:

        private record DatosErrorValidacion(String campo, String error) {
            public DatosErrorValidacion(FieldError error) {
                this(
                        error.getField(),
                        error.getDefaultMessage());
            }
        }

### Explicacion el codigo:

-   @ExceptionHandler(MethodArgumentNotValidException.class): Esta anotación marca este método como el manejador de excepciones para MethodArgumentNotValidException, que se lanza cuando hay errores de validación en las solicitudes.

-   `public ResponseEntity<List<DatosErrorValidacion>> tratarError400(MethodArgumentNotValidException e)`: La firma del método indica que aceptará una excepción de tipo MethodArgumentNotValidException como parámetro y devolverá una instancia de ResponseEntity que contiene una lista de DatosErrorValidacion.

-   var errores = e.getFieldErrors().stream().map(DatosErrorValidacion::new).toList();: Aquí se procesan los errores de validación que están presentes en la excepción MethodArgumentNotValidException. getFieldErrors() devuelve una lista de errores de campo que ocurrieron durante la validación. Luego, se crea un flujo (Stream) a partir de esta lista y se mapea cada error de campo a un objeto DatosErrorValidacion utilizando el constructor de ese registro. Finalmente, el flujo se convierte en una lista usando toList().

-   return ResponseEntity.badRequest().body(errores);: Se crea y devuelve una instancia de ResponseEntity con un código de estado HTTP 400 (Bad Request) utilizando badRequest(). El cuerpo de la respuesta se establece como la lista de errores de validación que se creó anteriormente.

El método DatosErrorValidacion se utiliza para representar de manera estructurada cada error de validación. Es un registro con dos campos: campo y error, que representan el nombre del campo y el mensaje de error respectivamente. El constructor del registro toma un objeto FieldError (que proviene de Spring Validation) y extrae el nombre del campo y el mensaje de error para inicializar los campos del registro.

El método DatosErrorValidacion se utiliza para representar de manera estructurada cada error de validación. Es un registro con dos campos: campo y error, que representan el nombre del campo y el mensaje de error respectivamente. El constructor del registro toma un objeto FieldError (que proviene de Spring Validation) y extrae el nombre del campo y el mensaje de error para inicializar los campos del registro.

## Continuamos...

6.  Compilamos y probamos de nuevo el POST fallido y ahora el body de la respuesta ser vera de la siguiente manera:

        [
            {
                "campo": "nombre",
                "error": "no debe estar vacío"
            },
            {
                "campo": "documento",
                "error": "no debe estar vacío"
            },
            {
                "campo": "especialidad",
                "error": "no debe ser nulo"
            },
            {
                "campo": "telefono",
                "error": "no debe estar vacío"
            },
            {
                "campo": "email",
                "error": "no debe estar vacío"
            }
        ]

    Mas simple y facil de entender.

## Notas Extras

### Para saber más: personalización de mensajes de error

Es posible que haya notado que Bean Validation tiene un mensaje de error para cada una de sus anotaciones. Por ejemplo, cuando la validación falla en algún atributo anotado con @NotBlank, el mensaje de error será: must not be blank.

Estos mensajes de error no se definieron en la aplicación, ya que son mensajes de error estándar de Bean Validation. Sin embargo, si lo desea, puede personalizar dichos mensajes.

Una de las formas de personalizar los mensajes de error es agregar el atributo del mensaje a las anotaciones de validación:

    public record DatosCadastroMedico(
        @NotBlank(message = "Nombre es obligatorio")
        String nombre,

        @NotBlank(message = "Email es obligatorio")
        @Email(message = "Formato de email es inválido")
        String email,

        @NotBlank(message = "Teléfono es obligatorio")
        String telefono,

        @NotBlank(message = "CRM es obligatorio")
        @Pattern(regexp = "\\d{4,6}", message = "Formato do CRM es inválido")
        String crm,

        @NotNull(message = "Especialidad es obligatorio")
        Especialidad especialidad,

        @NotNull(message = "Datos de dirección son obligatorios")
        @Valid DatosDireccion direccion) {}

Otra forma es aislar los mensajes en un archivo de propiedades, que debe tener el nombre ValidationMessages.properties y estar creado en el directorio src/main/resources:

    nombre.obligatorio=El nombre es obligatorio
    email.obligatorio=Correo electrónico requerido
    email.invalido=El formato del correo electrónico no es válido
    phone.obligatorio=Teléfono requerido
    crm.obligatorio=CRM es obligatorio
    crm.invalido=El formato CRM no es válido
    especialidad.obligatorio=La especialidad es obligatoria
    address.obligatorio=Los datos de dirección son obligatorios

Y, en las anotaciones, indicar la clave de las propiedades por el propio atributo message, delimitando con los caracteres { e }:

    public record DatosRegistroMedico(
        @NotBlank(message = "{nombre.obligatorio}")
        String nombre,

        @NotBlank(message = "{email.obligatorio}")
        @Email(message = "{email.invalido}")
        String email,

        @NotBlank(message = "{telefono.obligatorio}")
        String telefono,

        @NotBlank(message = "{crm.obligatorio}")
        @Pattern(regexp = "\\d{4,6}", message = "{crm.invalido}")
        String crm,

        @NotNull(message = "{especialidad.obligatorio}")
        Especialidad especialidad,

        @NotNull(message = "{direccion.obligatorio}")
        @Valid DatosDireccion direccion) {}

# Autenticación y autorización

De manera resumida ahora buscaremos resolver problemas referente a la autenticación autorización y proteccion contra ataques como CSFR y clickjacking.

# Agregando Srping Security

1.  Volvemos a `https://start.spring.io/` y buscamos la dependencia de spring security y lo agregamos a nuestro pom.xml como lo hemos hecho antes

2.  Una vez agregadas las dependencias compilamos y si revisamos la consola veremos un par de mensajes nuevos:

        Using generated security password: 90c3b196-c4ed-4237-8101-90efa2fd5d91

        This generated password is for development use only. Your security configuration must be updated before running your application in production.

3.  Ahora lo importante aqui es que si volvemos a intentar alguna de nuestras peticiones nos daran un error de 402 Unauthorized, osea ya no tenemos autorizacion para ello

4.  Para entender aun mejor esto hay que intentar ingresar desde el navegador con nuestra url de medicos, nos redireccionara a la siguiente url: `http://localhost:8080/login` y veremos un menu de login

5.  Para acceder solo debemos colocar:

        user: user
        password: 90c3b196-c4ed-4237-8101-90efa2fd5d91

    Si la contraseña es lo que nos dio spring en la linea de comando

6.  Pero como tal ese login no nos sirve ya que la idea es usar json tokens

# Las entidades usuario y migration

1.  Primero deberemos hacer la creacion de Usuarios que puedan hacer las peticiones y con ello hacer uso de los json tokens

2.  Vamos al paquete de domain y dentro de este creamos el paquete de `usuarios`

3.  Dentro de dicho paquete creamos la clase `Usuario.java`

4.  Agregamos el siguiente codigo:

        package med.voll.api.domain.usuarios;

        import jakarta.persistence.*;

        import lombok.*;

        @Table(name = "usuarios")
        @Entity(name = "Usuario")
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @EqualsAndHashCode(of = "id")
        public class Usuario {

            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;
            private String login;
            private String clave;

        }

5.  Ahora crearemos un nuevo migration de nombre `V4__create-table-usuarios.sql`, recordemos detener el programa para este punto

6.  Agregamos el siguiente codigo en el migration:

        CREATE TABLE
            usuarios (
                id BIGINT NOT NULL AUTO_INCREMENT,
                login VARCHAR(100) NOT NULL,
                clave VARCHAR(300) NOT NULL,
                PRIMARY KEY(id)
            );

7.  Compilamos y revisamos la base de datos que se haya creado la tabla de usuarios

## Notas extras

### Para saber más: hash de contraseña

Al implementar una funcionalidad de autenticación en una aplicación, independientemente del lenguaje de programación utilizado, deberá tratar con los datos de inicio de sesión y contraseña de los usuarios, y deberán almacenarse en algún lugar, como, por ejemplo, una base de datos.

Las contraseñas son información confidencial y no deben almacenarse en texto sin formato, ya que si una persona malintencionada logra acceder a la base de datos, podrá acceder a las contraseñas de todos los usuarios. Para evitar este problema, siempre debe usar algún algoritmo hash en las contraseñas antes de almacenarlas en la base de datos.

Hashing no es más que una función matemática que convierte un texto en otro texto totalmente diferente y difícil de deducir. Por ejemplo, el texto “Mi nombre es Rodrigo” se puede convertir en el texto 8132f7cb860e9ce4c1d9062d2a5d1848, utilizando el algoritmo hash MD5.

Un detalle importante es que los algoritmos de hash deben ser unidireccionales, es decir, no debe ser posible obtener el texto original a partir de un hash. Así, para saber si un usuario ingresó la contraseña correcta al intentar autenticarse en una aplicación, debemos tomar la contraseña que ingresó y generar su hash, para luego compararla con el hash que está almacenado en la base de datos.

Hay varios algoritmos hashing que se pueden usar para transformar las contraseñas de los usuarios, algunos de los cuales son más antiguos y ya no se consideran seguros en la actualidad, como MD5 y SHA1. Los principales algoritmos actualmente recomendados son:

-   Bcrypt
-   Scrypt
-   Argon2
-   PBKDF2

A lo largo del curso utilizaremos el algoritmo BCrypt, que es bastante popular hoy en día. Esta opción también tiene en cuenta que Spring Security ya nos proporciona una clase que lo implementa.

# Repository Service

1.  Ahora dentro del paquete `usuarios` vamos a crear la interface `UsuarioRepository.java`:

2.  Como queremos tratar la seguridad y demás de una forma más global iremos al paquete infra, donde crearemos dentro de este 2 nuevos, un paquete `errores` y un paquete `security` dentro de errores movemos nuestro `TratadorDeErrores` y dentro de `security` creamos una clase `AutenticacionService.java`

3.  lo anterior tendra el siguiente codigo:

        package med.voll.api.infra.security;

        import org.springframework.security.core.userdetails.UserDetails;
        import org.springframework.security.core.userdetails.UserDetailsService;
        import org.springframework.security.core.userdetails.UsernameNotFoundException;
        import org.springframework.stereotype.Service;

        @Service
        public class AutenticacionService implements UserDetailsService {

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'loadUserByUsername'");
            }

        }

4.  Ahora lo anterior necesita de la implementacion de u metodo para obtener el usuario, para ello vamos a modificar el codigo de la siguiente manera:

        package med.voll.api.infra.security;

        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.security.core.userdetails.UserDetails;
        import org.springframework.security.core.userdetails.UserDetailsService;
        import org.springframework.security.core.userdetails.UsernameNotFoundException;
        import org.springframework.stereotype.Service;

        import med.voll.api.domain.usuarios.UsuarioRepository;

        @Service
        public class AutenticacionService implements UserDetailsService {

            @Autowired
            private UsuarioRepository usuarioRepository;

            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return usuarioRepository.findByLogin(username);
            }

        }

5.  Lo anterior tiene un findByLogin() el cual al no ser un metodo existente le tenemos que decir que lo cree, por lo que ahora en `UsuarioRepository` aparece lo siguiente:

        package med.voll.api.domain.usuarios;

        import org.springframework.data.jpa.repository.JpaRepository;
        import org.springframework.security.core.userdetails.UserDetails;

        public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

            UserDetails findByLogin(String username);

        }

### Explicacion de todo lo anterior:

#### Resumen de los cambios:

En resumen, los cambios realizados permiten a Spring Security acceder a los datos de los usuarios almacenados en la base de datos para llevar a cabo el proceso de autenticación. La implementación de AutenticacionService es crucial ya que proporciona la lógica necesaria para cargar los detalles del usuario durante la autenticación. La interfaz UserDetailsService es parte esencial de Spring Security y se encarga de cargar los datos del usuario.

Este enfoque te permite centralizar la gestión de usuarios en la base de datos y utilizar Spring Security para manejar el proceso de autenticación de manera más efectiva. Los usuarios ahora pueden autenticarse en función de los datos almacenados en la base de datos y se utiliza la interfaz UserDetailsService para cargar los detalles del usuario durante el proceso de autenticación.

#### Implementación de Autenticación y Seguridad:

-   Creamos una interfaz UsuarioRepository en el paquete usuarios que extiende JpaRepository para proporcionar métodos de acceso a la base de datos para la entidad Usuario. Además, agregamos un método personalizado findByUsername(String username) que se utilizará para buscar usuarios por su nombre de usuario (login).

-   En el paquete infra.security, creamos una clase llamada AutenticacionService. Esta clase implementa la interfaz UserDetailsService de Spring Security, que se utiliza para cargar información de usuarios durante el proceso de autenticación.

-   En el método loadUserByUsername(String username) de AutenticacionService, solicitamos el repositorio de usuarios (UsuarioRepository) para buscar un usuario por su nombre de usuario. Si el usuario no se encuentra, se lanza una excepción.

-   Ahora, el método loadUserByUsername devuelve un objeto UserDetails que representa al usuario autenticado. Esto incluye su nombre de usuario, contraseña y roles/permisos asociados.

## Explicacion extra:

En el contexto de la seguridad y la autenticación en aplicaciones web, los términos "stateless" y "stateful" se refieren a dos enfoques diferentes para administrar la información de la sesión y el estado del usuario. Estos conceptos son importantes para comprender cómo funcionan los mecanismos de seguridad, como los tokens de autenticación, en una aplicación.

#### Stateless (Sin estado):

En un enfoque stateless, el servidor no almacena información de sesión o estado del usuario. En cambio, toda la información necesaria para autenticar y autorizar a un usuario se incluye en cada solicitud que envía el cliente (generalmente en forma de tokens de autenticación, como JSON Web Tokens o JWT). El servidor verifica la validez del token en cada solicitud y toma decisiones basadas en esa información.

#### Ventajas del enfoque stateless:

-   Escalabilidad más fácil: No es necesario mantener información de sesión en el servidor, lo que facilita la escalabilidad horizontal.

-   Menos carga en el servidor: No se requiere almacenamiento de sesión, lo que reduce la carga en el servidor.

#### Stateful (Con estado):

En un enfoque stateful, el servidor almacena información de sesión y estado del usuario. Cada vez que el cliente realiza una solicitud, el servidor consulta su información de sesión para tomar decisiones sobre autenticación y autorización.

#### Ventajas del enfoque stateful:

-   Manejo más completo de la sesión: El servidor puede realizar un seguimiento exhaustivo del estado y la actividad del usuario.

-   Mayor control: El servidor tiene un control más directo sobre la sesión del usuario.

#### Relación con la Seguridad:

El enfoque stateless es comúnmente preferido en la seguridad de aplicaciones web modernas. Los tokens de autenticación stateless (como JWT) contienen la información necesaria para validar la identidad y los permisos del usuario en cada solicitud. Esto significa que el servidor no necesita almacenar información de sesión, lo que lo hace más escalable y eficiente.

En contraste, en un enfoque stateful, el servidor debe mantener información de sesión, lo que puede limitar la escalabilidad y requerir más recursos para el almacenamiento y la gestión de sesiones.

En resumen, cuando se menciona que el proceso de autenticación y seguridad se está diseñando para ser "stateless", significa que se está siguiendo un enfoque en el que la información de sesión y estado se maneja en el cliente (en los tokens de autenticación) en lugar de mantenerla en el servidor. Esto ofrece beneficios de escalabilidad y eficiencia en aplicaciones web modernas.

#### Como se aplica en este proyecto?

En el contexto del proyecto y la creación de la entidad "Usuario", la idea de ser "stateless" está relacionada con cómo se maneja la autenticación y la sesión de los usuarios en la aplicación.

#### Proyecto y Entidad Usuario:

En el proyecto, se está implementando medidas de seguridad, autenticación y autorización para proteger tus recursos y datos. Para lograrlo, se esta utilizando Spring Security, que es un marco de seguridad ampliamente utilizado en aplicaciones Java. Para hacer que la autenticación sea más segura y escalable, estás siguiendo un enfoque "stateless".

#### Enfoque Stateless en el Proyecto:

Cuando hablamos de ser "stateless" en el contexto de la seguridad y la autenticación, significa que no almacenamos información de sesión en el servidor. En lugar de eso, toda la información necesaria para autenticar y autorizar a un usuario se incluye en cada solicitud que el cliente (por ejemplo, un navegador web) envía al servidor. Esto se logra mediante el uso de tokens de autenticación, como JSON Web Tokens (JWT).

En el proyecto, esto se refleja en cómo manejas la autenticación de los usuarios:

-   Creamos la entidad "Usuario" en el paquete de dominio, que almacena la información básica de los usuarios, como el nombre de usuario y la contraseña.

-   Implementamos una interfaz llamada "UsuarioRepository" que proporciona métodos para acceder a la información de los usuarios, como buscar usuarios por su nombre de usuario.

-   Creamos una clase llamada "AutenticacionService" en el paquete de seguridad que implementa la interfaz "UserDetailsService". Esta clase se encarga de cargar los detalles del usuario (como su nombre de usuario y contraseña) según la solicitud del cliente.

#### Cómo se relaciona con el enfoque Stateless:

Al implementar la autenticación mediante el enfoque stateless, estás asegurando que no necesitas almacenar información de sesión en el servidor. En lugar de eso, cuando un usuario inicia sesión, tu sistema genera un token de autenticación (JWT) que contiene los detalles del usuario. Este token se envía al cliente, y luego el cliente incluye este token en las solicitudes posteriores al servidor. Cada vez que una solicitud llega al servidor, se verifica la validez del token y se determina si el usuario tiene permiso para acceder a los recursos solicitados.

En resumen, el enfoque "stateless" en tu proyecto significa que cada solicitud contiene toda la información necesaria para autenticar y autorizar al usuario, lo que elimina la necesidad de almacenar información de sesión en el servidor. Esto hace que tu sistema sea más escalable y eficiente en términos de seguridad y autenticación.

# Configuración de seguridad

1.  Falata una configuracion (o mas de una) en especifico pero que no son a nivel de colocarlas en el application.properties

2.  Para ello debemos crear una nueva clase en nuestro paquete de security de nombre `SecurityConfigurations`

3.  Agregamos el siguiente codigo:

        package med.voll.api.infra.security;

        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.security.config.annotation.web.builders.HttpSecurity;
        import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
        import org.springframework.security.config.http.SessionCreationPolicy;
        import org.springframework.security.web.SecurityFilterChain;

        @Configuration
        @EnableWebSecurity
        public class SecurityConfigurations {

            @Bean
            public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
                return httpSecurity.csrf().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        .and().build();
            }

        }

### Explicacion del codigo:

-   @Configuration y @EnableWebSecurity: Estas anotaciones indican que esta clase es una configuración para la seguridad web en tu aplicación.

-   SecurityFilterChain: Es una clase que representa una cadena de filtros de seguridad. Define cómo deben ser aplicados los filtros de seguridad en las diferentes rutas de tu aplicación.

-   httpSecurity.csrf().disable(): Este método desactiva la protección CSRF (Cross-Site Request Forgery), que es una medida de seguridad para prevenir ataques de falsificación de solicitudes entre sitios.

-   sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS): Establece la política de manejo de sesiones como "STATELESS", lo que significa que Spring Security no creará sesiones para los usuarios y tratará cada solicitud como independiente, lo que es útil para hacer la autenticación basada en tokens.

### Problema no mencionado en el video:

En cuanto a las alternativas a los métodos csrf() y sessionManagement(), efectivamente, en versiones más recientes de Spring Security, estos métodos están marcados como obsoletos y se recomienda utilizar una configuración más centrada en OAuth 2.0 y JWT (JSON Web Tokens). Puedes utilizar una configuración basada en OAuth 2.0 y JWT para la autenticación y autorización en tu aplicación, lo que te permitirá tener un flujo más seguro y moderno.

Este tipo de configuración implicaría el uso de clases como JwtFilter, JwtTokenProvider, OAuth2AuthorizationServer, entre otras. Estos componentes te ayudarían a manejar la autenticación y autorización utilizando tokens JWT en lugar de las sesiones basadas en cookies. Esto es especialmente útil para aplicaciones stateless y APIs RESTful.

### Codigo alternativo sin problemas de depreciacion:

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
            return httpSecurity.csrf(csrf -> {
                try {
                    csrf.disable().sessionManagement(
                            management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).build();
        }

-   Configuración de Seguridad: En la clase SecurityConfigurations, estamos configurando la seguridad de nuestra aplicación utilizando Spring Security. Esto es esencial para definir cómo se manejarán la autenticación y la autorización.

-   @Configuration y @EnableWebSecurity: @Configuration indica que esta clase es una fuente de configuración. @EnableWebSecurity habilita la seguridad web en nuestra aplicación.

-   @Bean Método de Configuración: En Spring, los métodos anotados con @Bean se utilizan para definir y configurar objetos que se administran dentro del contenedor de Spring, como componentes, controladores y más. En este caso, estamos definiendo un SecurityFilterChain personalizado que configura cómo se manejarán los filtros de seguridad en nuestra aplicación.

-   securityFilterChain Método: Este método está anotado con @Bean, lo que indica que producirá un objeto SecurityFilterChain que Spring administrará. Recibe un parámetro HttpSecurity httpSecurity, que es la configuración de seguridad principal que utilizaremos para definir nuestras reglas de seguridad.

-   csrf.disable(): Aquí estamos desactivando el filtro CSRF (Cross-Site Request Forgery), que es una medida de seguridad para prevenir ataques de falsificación de solicitudes entre sitios.

-   sessionManagement: Estamos configurando cómo se gestionarán las sesiones en nuestra aplicación. sessionCreationPolicy(SessionCreationPolicy.STATELESS) establece que no se creará ninguna sesión para las solicitudes, lo que hace que nuestra aplicación sea stateless (sin estado), lo que es una buena práctica para APIs REST.

-   Manejo de Excepciones: Notarás que estamos manejando las excepciones utilizando un bloque try-catch. En este caso, estamos atrapando cualquier excepción que pueda ocurrir al intentar deshabilitar el filtro CSRF y configurar la gestión de sesiones. Sin embargo, sería más apropiado manejar estas excepciones de manera adecuada, como registrarlas o lanzarlas a un nivel superior.

En resumen, este código configura la seguridad de la aplicación, desactivando el filtro CSRF y configurando la gestión de sesiones para que la aplicación sea stateless. El método anotado con @Bean crea y administra un objeto SecurityFilterChain personalizado que encapsula estas configuraciones de seguridad.

## Continuamos...

4. Cuando compilemos reintentaremos algun metodo y veremos que otra vez funcionan con normalidad, osea como si no se hubiera hecho nada, pero la magia en esto es que ahora tenermos el control sobre la forma de autenticar

# Continuacion en notras2.md
