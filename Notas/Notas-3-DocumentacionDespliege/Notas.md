# Objetivos generales de esta parte del proyecto:

1. Funcionalidad de agendamiento de consultas
2. Documentacion de la API
3. Testes automatizados
4. Build del proyecto

# Aviso: Previo a esto debemos haver incluido todo lo referente a pacientes

# Nueva funcionalidad

Especificamente para la funcionalidad de adendamiento de consultas es lo siguiente:

-   El horario de atención de la clinica es de lunes a sábado de 07:00 a 19:00 horas
-   Las consultas tienen una duración fija de una hora
-   Las consultas deben programarse al menos 30 minutos de anticipacion
-   NO permitir agendar citas con pacientes inactivos
-   NO permitir agendar citas con medicos inactivos
-   NO permita programar mas de una consulta en el mismo dia para el mismo paciente
-   NO permitir agendar una consulta con un medico que ya tenga otra consulta con la misma fecha y hora
-   La eleccion del medico es opcional, en cuyo caso de que no exista el id el sistema debera elegir automaticamente un medico que este disponible en la fecha/hora ingresada

# Consulta controller

1.  Comenzamos por crear el controller de las consultas, asi que lo llamamos `ConsultaController.java` y de momento tiene el siguiente codigo:

        package med.voll.api.controller;

        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;
        import org.springframework.stereotype.*;

        import jakarta.validation.Valid;
        import jakarta.transaction.Transactional;

        import med.voll.api.domain.consulta.DatosDetalleConsulta;
        import med.voll.api.domain.consulta.DatosAgendarConsulta;

        @Controller
        @ResponseBody
        @RequestMapping("/consultas")
        public class ConsultaController {

            @PostMapping
            @Transactional
            public ResponseEntity<DatosDetalleConsulta> agendar(@RequestBody @Valid DatosAgendarConsulta datos) {
                return ResponseEntity.ok(new DatosDetalleConsulta(null, null, null, null));
            }

        }

2.  En dicho codigo podemos notar que tenemos `DatosDetalleConsulta` y `DatosAgendarConsulta`, dichos elementos son dos record que tienen el siguiente codigo:

    -   DatosDetalleConsulta:

              package med.voll.api.domain.consulta;

              import java.time.LocalDateTime;

              public record DatosDetalleConsulta(
                      Long id,
                      Long idPaciente,
                      Long idMedico,
                      LocalDateTime fecha) {
              }

    -   DatosAgendarConsulta:

              package med.voll.api.domain.consulta;

              import java.time.LocalDateTime;
              import jakarta.validation.constraints.*;

              public record DatosAgendarConsulta(
                      Long id,
                      @NotNull Long idPaciente,
                      Long idMedico,
                      @NotNull @Future LocalDateTime fecha) {
              }

### Explicacion del codigo:

    package med.voll.api.controller;

    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.stereotype.*;

    import jakarta.validation.Valid;
    import jakarta.transaction.Transactional;

    import med.voll.api.domain.consulta.DatosDetalleConsulta;
    import med.voll.api.domain.consulta.DatosAgendarConsulta;

    @Controller
    @ResponseBody
    @RequestMapping("/consultas")
    public class ConsultaController {

        @PostMapping
        @Transactional
        public ResponseEntity<DatosDetalleConsulta> agendar(@RequestBody @Valid DatosAgendarConsulta datos) {
            return ResponseEntity.ok(new DatosDetalleConsulta(null, null, null, null));
        }

    }

Este controlador maneja las operaciones relacionadas con las consultas médicas. Veamos los detalles:

-   @RestController: Esta anotación indica que la clase es un controlador y que los métodos manejarán las solicitudes HTTP y devolverán respuestas JSON.

-   @RequestMapping("/consultas"): Esta anotación establece la ruta base para todas las rutas definidas en este controlador. Por lo tanto, las rutas de este controlador comenzarán con "/consultas".

-   @PostMapping: Indica que este método manejará solicitudes POST.

-   @Transactional: Esta anotación asegura que la transacción se inicie antes de que se ejecute el método y se confirme después de que el método se complete. Esto es útil en contextos de bases de datos para asegurar la consistencia de los datos.

-   public ResponseEntity<DatosDetalleConsulta> agendar(@RequestBody @Valid DatosAgendarConsulta datos): Este método recibe una solicitud POST y espera un cuerpo JSON que se deserializa en un objeto DatosAgendarConsulta. Luego, crea una respuesta JSON de tipo DatosDetalleConsulta (aunque actualmente los campos son nulos) y la devuelve en una entidad de respuesta ResponseEntity.

#### DatosDetalleConsulta.java y DatosAgendarConsulta.java:

Ambos records son estructuras de datos inmutables que se utilizan para representar la información necesaria para agendar y detallar consultas médicas.

-   DatosDetalleConsulta tiene campos para el ID de la consulta, el ID del paciente, el ID del médico y la fecha de la consulta.

-   DatosAgendarConsulta tiene campos similares, pero también agrega validaciones utilizando anotaciones de validación de JavaBean. Por ejemplo, @NotNull asegura que ciertos campos no sean nulos, y @Future garantiza que la fecha sea en el futuro.

En resumen, este controlador maneja la creación de consultas médicas. Recibe información de agendamiento en formato JSON, la procesa (aunque actualmente no hace mucho) y devuelve una respuesta JSON. Los records DatosDetalleConsulta y DatosAgendarConsulta definen la estructura de los datos que se manejan en este proceso.

## Continuando...

3.  Ahora crearemos la entidad `Consulta.java` la cual tendra el siguiente codigo:

        package med.voll.api.domain.consulta;

        import jakarta.persistence.*;
        import lombok.*;
        import med.voll.api.domain.medico.Medico;
        import med.voll.api.domain.paciente.Paciente;
        import java.time.LocalDateTime;

        @Table(name = "consultas")
        @Entity(name = "Consulta")
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        @EqualsAndHashCode(of = "id")
        public class Consulta {

            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            private Long id;

            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "medico_id")
            private Medico medico;

            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "paciente_id")
            private Paciente paciente;

            private LocalDateTime data;

        }

### Explicacion del codigo:

-   @Table(name = "consultas") y @Entity(name = "Consulta") son anotaciones de JPA que indican que esta clase es una entidad que será mapeada a una tabla llamada "consultas" en la base de datos.

-   @Getter y @NoArgsConstructor son anotaciones de Lombok que generan automáticamente los métodos getter y un constructor sin argumentos.

-   @AllArgsConstructor genera un constructor que incluye todos los campos de la clase.

-   @EqualsAndHashCode(of = "id") genera los métodos equals() y hashCode() basados en el campo id, lo que permite comparar instancias de la entidad por su identificador.

-   @Id marca el campo id como clave primaria de la entidad.

-   @GeneratedValue(strategy = GenerationType.IDENTITY) indica que el valor del campo id se generará automáticamente mediante una estrategia de identidad.

-   @ManyToOne(fetch = FetchType.LAZY) establece una relación de muchos a uno con otras entidades (en este caso, Medico y Paciente). El valor FetchType.LAZY indica que los datos se cargarán solo cuando se acceda a ellos, lo que puede mejorar el rendimiento.

-   @JoinColumn(name = "medico_id") y @JoinColumn(name = "paciente_id") especifican las columnas en la tabla "consultas" que se usarán para establecer la relación con las entidades Medico y Paciente.

-   private LocalDateTime data; es un campo que representa la fecha y hora de la consulta.

En resumen, la entidad Consulta modela una consulta médica en la base de datos, estableciendo relaciones con las entidades Medico y Paciente mediante las anotaciones @ManyToOne. Los campos y las anotaciones se utilizan para definir cómo se mapean y relacionan los datos en la base de datos.

### Detalle del ManyToOne:

La anotación @ManyToOne indica una relación "muchos a uno", lo que significa que varios registros en la tabla Consultas pueden estar relacionados con un solo registro en las tablas Medicos y Pacientes.

En este caso particular, cada consulta tiene un único médico y un único paciente asociado. La relación "muchos a uno" se refiere a que varios registros en la tabla de consultas pueden apuntar al mismo médico y al mismo paciente. Por lo tanto, se establece una relación entre la entidad Consulta y las entidades Medico y Paciente.

Por ejemplo, un médico puede atender a varios pacientes en diferentes consultas, y un paciente puede tener múltiples consultas con diferentes médicos. Sin embargo, en una consulta específica, solo hay un médico y un paciente asociado.

La anotación @JoinColumn especifica cómo se almacenará la relación en la base de datos, estableciendo una columna que contendrá la clave foránea hacia las tablas Medicos y Pacientes para identificar a qué médico y paciente pertenece cada consulta.

## Continuando...

4.  Creamos ahora el repositorio de nombre `ConsultaRepository.java` y agregamos lo siguiente:

        package med.voll.api.domain.consulta;

        import org.springframework.data.jpa.repository.JpaRepository;

        public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

        }

5.  Ahora crearemos el nuevo flyway de nombre `V8__create-table-consultas.sql` el cual tendra el siguiente codigo:

        CREATE TABLE consultas (
            id BIGINT NOT NULL AUTO_INCREMENT,
            medico_id BIGINT NOT NULL,
            paciente_id BIGINT NOT NULL,
            fecha DATETIME NOT NULL,

            PRIMARY KEY (id), -- Definición de la clave primaria de la tabla 'consultas'
            FOREIGN KEY (medico_id) REFERENCES medicos (id), -- Llave foránea hacia la tabla 'medicos'
            FOREIGN KEY (paciente_id) REFERENCES pacientes (id) -- Llave foránea hacia la tabla 'pacientes'
        );

### Explicacion del codigo:

Las llaves foráneas (o claves externas) son una característica importante en las bases de datos relacionales para mantener la integridad referencial y establecer relaciones entre tablas. En el contexto de tu script SQL, las llaves foráneas se utilizan para relacionar la tabla consultas con las tablas medicos y pacientes.

-   FOREIGN KEY (medico_id) REFERENCES medicos (id): Aquí estableces una llave foránea en la columna medico_id que referencia la columna id en la tabla medicos. Esto crea una relación entre los registros en ambas tablas, lo que significa que solo puedes insertar valores en medico_id que ya existen en la tabla medicos.

-   FOREIGN KEY (paciente_id) REFERENCES pacientes (id): Similar a la llave foránea anterior, esta establece una relación entre la columna paciente_id y la columna id en la tabla pacientes.

## Continuando...

6.  Compilamos para ver que todo este en orden

7.  Ahora en thunder client o insomnia o en postman realizaremos una nueva peticion POST para una consulta, recordando generar antes el token de login y dicho POST consulta tendra un body como el siguiente:

        {
            "idPaciente":"1",
            "idMedico":"1",
            "fecha":"2023-10-12T10:30"
        }

## Notas extras

### Para saber más: anotacion @JsonAlias

Aprendimos que los nombres de los campos enviados en JSON a la API deben ser idénticos a los nombres de los atributos de las clases DTO, ya que de esta manera Spring puede completar correctamente la información recibida.

Sin embargo, puede ocurrir que un campo se envíe en JSON con un nombre diferente al atributo definido en la clase DTO. Por ejemplo, imagine que se envíe el siguiente JSON a la API:

    {
        "producto_id": 12,
        "fecha_compra": "01/01/2022"
    }

Y la clase DTO creada para recibir esta información se define de la siguiente manera:

    public record DatosCompra(
    Long idProducto,
    LocalDate fechaCompra
    ){}

Si esto ocurre, tendremos problemas porque Spring instanciará un objeto del tipo DatosCompra, pero sus atributos no se completarán y quedarán como null debido a que sus nombres son diferentes a los nombres de los campos recibidos en JSON.

Tenemos dos posibles soluciones para esta situación:

1. Renombrar los atributos en el DTO para que tengan el mismo nombre que los campos en JSON;

2. Solicitar que la aplicación cliente que envía las solicitudes a la API cambie los nombres de los campos en el JSON enviado.

La primera opción anteriormente mencionada no es recomendable, ya que los nombres de los campos en JSON no están de acuerdo con el estándar de nomenclatura de atributos utilizado en el lenguaje Java.

La segunda opción sería la más indicada, pero no siempre será posible "obligar" a los clientes de la API a cambiar el estándar de nomenclatura utilizado en los nombres de los campos en JSON.

Para esta situación, existe una tercera opción en la que ninguno de los lados (cliente y API) necesita cambiar los nombres de los campos/atributos. Para ello, solo es necesario utilizar la anotación @JsonAlias:

    public record DatosCompra(
    @JsonAlias("producto_id") Long idProducto,
    @JsonAlias("fecha_compra") LocalDate fechaCompra
    ){}
    La anotación @JsonAlias sirve para mapear "alias" alternativos para los campos que se recibirán del JSON, y es posible asignar múltiples alias:
    public record DatosCompra(
    @JsonAlias({"producto_id", "id_producto"}) Long idProducto,
    @JsonAlias({"fecha_compra", "fecha"}) LocalDate fechaCompra
    ){}

De esta manera, se resuelve el problema, ya que Spring, al recibir el JSON en la solicitud, buscará los campos considerando todos los alias declarados en la anotación @JsonAlias.

### Para saber más: formatacion de fechas

Como se demostró en el video anterior, Spring tiene un patrón de formato para campos de tipo fecha cuando se asignan a atributos de tipo LocalDateTime. Sin embargo, es posible personalizar este patrón para utilizar otros formatos que prefiramos.

Por ejemplo, imagine que necesitamos recibir la fecha/hora de la consulta en el siguiente formato: dd/mm/yyyy hh:mm. Para que esto sea posible, debemos indicar a Spring que este será el formato en el que se recibirá la fecha/hora en la API, lo que puede hacerse directamente en el DTO utilizando la anotación @JsonFormat:

    @NotNull
    @Future
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    LocalDateTime data

En el atributo pattern indicamos el patrón de formato esperado, siguiendo las reglas definidas por el estándar de fechas de Java. Puede encontrar más detalles en esta página del JavaDoc.

Esta anotación también se puede utilizar en las clases DTO que representan la información que devuelve la API, para que el JSON devuelto se formatee de acuerdo con el patrón configurado. Además, no se limita solo a la clase LocalDateTime, sino que también se puede utilizar en atributos de tipo LocalDate y LocalTime.

#### En este punto modificamos "DatosAgendarConsulta.java" para hacer uso de @JsonFormat

# Clase Service

1.  Vamos a preparar el terreno para comenzar el agendar consultas, primero que nada crearemos dentro del paquete consulta la clase `AgendaDeConsultaService.java` la cual tendra el siguiente codigo:

        package med.voll.api.domain.consulta;

        import org.springframework.stereotype.*;
        import org.springframework.beans.factory.annotation.*;
        import med.voll.api.domain.medico.MedicoRepository;
        import med.voll.api.domain.paciente.PacienteRepository;

        @Service
        public class AgendaDeConsultaService {

            @Autowired
            private ConsultaRepository consultaRepository;

            @Autowired
            private PacienteRepository pacienteRepository;

            @Autowired
            private MedicoRepository medicoRepository;

            public void agendar(DatosAgendarConsulta datos) {

                var paciente = pacienteRepository.findById(datos.idPaciente()).get();
                var medico = medicoRepository.findById(datos.idMedico()).get();

                var consulta = new Consulta(null, medico, paciente, datos.fecha());
                consultaRepository.save(consulta);

            }

        }

2.  Debido a lo anterior debemos hacer modificaciones a nuestro controller:

        package med.voll.api.controller;

        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.http.ResponseEntity;
        import org.springframework.web.bind.annotation.*;

        import jakarta.validation.Valid;
        import jakarta.transaction.Transactional;

        import med.voll.api.domain.consulta.DatosDetalleConsulta;
        import med.voll.api.domain.consulta.AgendaDeConsultaService;
        import med.voll.api.domain.consulta.DatosAgendarConsulta;

        @RestController
        @RequestMapping("/consultas")
        public class ConsultaController {

            @Autowired
            private AgendaDeConsultaService service;

            @PostMapping
            @Transactional
            public ResponseEntity<DatosDetalleConsulta> agendar(@RequestBody @Valid DatosAgendarConsulta datos) {
                service.agendar(datos);
                return ResponseEntity.ok(new DatosDetalleConsulta(null, null, null, null));
            }

        }

3.  Compilamos para ver que todo vaya en orden, posteriormente agregaremos las validaciones necesarias

### Explicacion del codigo:

#### AgendaDeConsultasService:

    @Service
    public class AgendaDeConsultaService {

        @Autowired
        private ConsultaRepository consultaRepository;

        @Autowired
        private PacienteRepository pacienteRepository;

        @Autowired
        private MedicoRepository medicoRepository;

        public void agendar(DatosAgendarConsulta datos) {

            var paciente = pacienteRepository.findById(datos.idPaciente()).get();
            var medico = medicoRepository.findById(datos.idMedico()).get();

            var consulta = new Consulta(null, medico, paciente, datos.fecha());
            consultaRepository.save(consulta);

        }

    }

Este servicio es responsable de coordinar la lógica de agendar una consulta. Aquí se realiza la interacción con los repositorios ConsultaRepository, PacienteRepository y MedicoRepository para obtener la información necesaria y luego crear y guardar la nueva consulta.

-   @Service: Anota la clase como un componente de servicio de Spring.

-   @Autowired: Anota los campos para la inyección de dependencias. Spring se encargará de proporcionar las instancias de los repositorios necesarios.

-   public void agendar(DatosAgendarConsulta datos): Este método toma los datos de agendamiento de una consulta y realiza las siguientes acciones:

    -   Utiliza los repositorios PacienteRepository y MedicoRepository para obtener las instancias de paciente y médico en función de sus IDs.

    -   Luego, crea una nueva instancia de Consulta utilizando los objetos de paciente, médico y la fecha proporcionada en los datos.

    -   Finalmente, guarda la nueva consulta utilizando consultaRepository.save(consulta).

#### ConsultaController:

    @RestController
    @RequestMapping("/consultas")
    public class ConsultaController {

        @Autowired
        private AgendaDeConsultaService service;

        @PostMapping
        @Transactional
        public ResponseEntity<DatosDetalleConsulta> agendar(@RequestBody @Valid DatosAgendarConsulta datos) {
            service.agendar(datos);
            return ResponseEntity.ok(new DatosDetalleConsulta(null, null, null, null));
        }

    }

-   public ResponseEntity<DatosDetalleConsulta> agendar(@RequestBody @Valid DatosAgendarConsulta datos): Este método recibe los datos de agendamiento como un objeto JSON en el cuerpo de la solicitud.

    -   Llama al método agendar del servicio AgendaDeConsultaService para procesar los datos y agendar la consulta.

    -   Luego, devuelve una respuesta ResponseEntity con el estado OK y un objeto DatosDetalleConsulta. En este punto, el objeto real devuelto no tiene datos, pero esto se puede ajustar según la necesidad.

En resumen, el AgendaDeConsultaService coordina la lógica de agendamiento y se encarga de interactuar con los repositorios para obtener los datos necesarios. El ConsultaController maneja las solicitudes HTTP y utiliza el servicio para agendar consultas. Estos componentes trabajan juntos para permitir el agendamiento de consultas a través de la API.

# Validaciones de integridad

1.  Comenzaremos a agregar nuestras validaciones asi que para ello vamos a modificar `AgendaDeConsultasService.java`:

        package med.voll.api.domain.consulta;

        import org.springframework.stereotype.*;
        import org.springframework.beans.factory.annotation.*;

        import med.voll.api.domain.medico.Medico;
        import med.voll.api.domain.medico.MedicoRepository;
        import med.voll.api.domain.paciente.PacienteRepository;
        import med.voll.api.infra.errores.ValidacionDeIntegridad;

        @Service
        public class AgendaDeConsultaService {

            @Autowired
            private ConsultaRepository consultaRepository;

            @Autowired
            private PacienteRepository pacienteRepository;

            @Autowired
            private MedicoRepository medicoRepository;

            public void agendar(DatosAgendarConsulta datos) {

                if (!pacienteRepository.findById(datos.idPaciente()).isPresent()) {
                    throw new ValidacionDeIntegridad("este id para el paciente no fue encontrado");
                }

                if (datos.idMedico() != null && !medicoRepository.existsById(datos.idMedico())) {
                    throw new ValidacionDeIntegridad("este id para el medico no fue encontrado");
                }

                var paciente = pacienteRepository.findById(datos.idPaciente()).get();
                var medico = seleccionarMedico(datos);

                var consulta = new Consulta(null, medico, paciente, datos.fecha());
                consultaRepository.save(consulta);

            }

            private Medico seleccionarMedico(DatosAgendarConsulta datos) {
                return null;
            }

        }

2.  Como podemos notar tenemos un `ValidacionDeIntegridad.java` el cual crearemos dentro del paquete errores:

        package med.voll.api.infra.errores;

        public class ValidacionDeIntegridad extends RuntimeException {

            public ValidacionDeIntegridad(String mensaje) {
                super(mensaje);
            }

        }

### Explicacion del codigo:

#### AgendaDeConsultaService

    @Service
    public class AgendaDeConsultaService {

        @Autowired
        private ConsultaRepository consultaRepository;

        @Autowired
        private PacienteRepository pacienteRepository;

        @Autowired
        private MedicoRepository medicoRepository;

        public void agendar(DatosAgendarConsulta datos) {

            if (!pacienteRepository.findById(datos.idPaciente()).isPresent()) {
                throw new ValidacionDeIntegridad("este id para el paciente no fue encontrado");
            }

            if (datos.idMedico() != null && !medicoRepository.existsById(datos.idMedico())) {
                throw new ValidacionDeIntegridad("este id para el medico no fue encontrado");
            }

            var paciente = pacienteRepository.findById(datos.idPaciente()).get();
            var medico = seleccionarMedico(datos);

            var consulta = new Consulta(null, medico, paciente, datos.fecha());
            consultaRepository.save(consulta);

        }

        private Medico seleccionarMedico(DatosAgendarConsulta datos) {
            return null;
        }

    }

En este servicio, se ha agregado la lógica de validación para garantizar la integridad de los datos antes de agendar una consulta.

-   if (!pacienteRepository.findById(datos.idPaciente()).isPresent()): Esta línea verifica si el paciente con el ID proporcionado existe en la base de datos. Si no existe, lanza una excepción ValidacionDeIntegridad con un mensaje explicativo.

-   if (datos.idMedico() != null && !medicoRepository.existsById(datos.idMedico())): Esta línea verifica si se proporcionó un ID de médico y si existe en la base de datos. Si existe, lanza una excepción ValidacionDeIntegridad con un mensaje explicativo.

-   En ambos casos de validación, se utiliza una excepción personalizada ValidacionDeIntegridad para señalar que ha ocurrido un problema de integridad de datos.

-   private Medico seleccionarMedico(DatosAgendarConsulta datos): Se ha dejado un método llamado seleccionarMedico sin implementar. Esto probablemente se completará en futuras etapas para seleccionar un médico apropiado para la consulta.

#### ValidacionDeIntegridad

    public class ValidacionDeIntegridad extends RuntimeException {

        public ValidacionDeIntegridad(String mensaje) {
            super(mensaje);
        }

    }

Esta es una clase personalizada de excepción que extiende RuntimeException. En este caso, se utiliza para manejar los errores de validación específicos que pueden ocurrir al agendar una consulta.

-   public ValidacionDeIntegridad(String mensaje): Este constructor permite crear una instancia de la excepción con un mensaje de error personalizado que se proporciona al momento de lanzar la excepción.

En resumen, los cambios en AgendaDeConsultaService agregaron lógica de validación para asegurar que los datos sean coherentes y válidos antes de agendar una consulta. Si se encuentran problemas de integridad de datos, se lanza una excepción ValidacionDeIntegridad. La clase ValidacionDeIntegridad es una excepción personalizada que se utiliza para manejar estos errores de validación específicos.

## Nota extra

### Para saber más: Service Pattern

El patrón Service es muy utilizado en la programación y su nombre es muy conocido. Pero a pesar de ser un nombre único, Service puede ser interpretado de varias maneras: puede ser un caso de uso (Application Service); un Domain Service, que tiene reglas de su dominio; un Infrastructure Service, que utiliza algún paquete externo para realizar tareas; etc.

A pesar de que la interpretación puede ocurrir de varias formas, la idea detrás del patrón es separar las reglas de negocio, las reglas de la aplicación y las reglas de presentación para que puedan ser fácilmente probadas y reutilizadas en otras partes del sistema.

Existen dos formas más utilizadas para crear Services. Puede crear Services más genéricos, responsables de todas las asignaciones de un Controller; o ser aún más específico, aplicando así la S del SOLID: Single Responsibility Principle (Principio de Responsabilidad Única). Este principio nos dice que una clase/función/archivo debe tener sólo una única responsabilidad.

Piense en un sistema de ventas, en el que probablemente tendríamos algunas funciones como: Registrar usuario, Iniciar sesión, Buscar productos, Buscar producto por nombre, etc. Entonces, podríamos crear los siguientes Services: RegistroDeUsuarioService, IniciarSesionService, BusquedaDeProductosService, etc.

Pero es importante estar atentos, ya que muchas veces no es necesario crear un Service y, por lo tanto, agregar otra capa y complejidad innecesarias a nuestra aplicación. Una regla que podemos utilizar es la siguiente: si no hay reglas de negocio, simplemente podemos realizar la comunicación directa entre los controllers y los repositories de la aplicación.

# Seleccion medico aleatorio

1.  Comenzaremos por modificar el metodo `seleccionarMedico` dentro de `AgendaDeConsultaService`:

        private Medico seleccionarMedico(DatosAgendarConsulta datos) {

                if (datos.idMedico() != null) {
                    return medicoRepository.getReferenceById(datos.idMedico());
                }

                if (datos.especialidad() == null) {
                    throw new ValidacionDeIntegridad("debe seleccionarse una especialidad para el medico");
                }

                return medicoRepository.seleccionarMedicoConsEspecialidadEnFecha(datos.especialidad(), datos.fecha());
            }

2.  Notaremos que ahora requerimos de la especialidad del medico, por lo que debemos agregar dicho cambio en `DatosAgendarConsulta.java`:

        public record DatosAgendarConsulta(
                Long id,
                @NotNull Long idPaciente,
                Long idMedico,
                @NotNull @Future @JsonFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime fecha,
                Especialidad especialidad) {
        }

3.  También notaremos que del repository medico estamos haciendo uso de un metodo propio, por lo que debemos crearlo, teniendo la siguiente forma `MedicoRepository`:

        public interface MedicoRepository extends JpaRepository<Medico, Long> {

            Page<Medico> findByActivoTrue(Pageable paginacion);

            @Query("""
                    select m from Medico m
                    where m.activo = 1 and
                    m.especialidad =: especialidad and
                    m.id not in(
                        select c.medico.id from Consulta c
                        c.data =: fecha
                    )
                    order by rand()
                    limit 1
                    """)
            Medico seleccionarMedicoConsEspecialidadEnFecha(Especialidad especialidad, LocalDateTime fecha);

        }

### Explicacion del codigo:

#### AgendaDeConsultaService

    private Medico seleccionarMedico(DatosAgendarConsulta datos) {

            if (datos.idMedico() != null) {
                return medicoRepository.getReferenceById(datos.idMedico());
            }

            if (datos.especialidad() == null) {
                throw new ValidacionDeIntegridad("debe seleccionarse una especialidad para el medico");
            }

            return medicoRepository.seleccionarMedicoConsEspecialidadEnFecha(datos.especialidad(), datos.fecha());
        }

El método seleccionarMedico se ha modificado para seleccionar un médico disponible para la consulta:

-   if (datos.idMedico() != null): Si se proporciona un ID de médico, se obtiene una referencia a ese médico directamente a través del método getReferenceById. Esto es útil cuando solo se conoce el ID y no se necesita cargar toda la entidad médico.

-   if (datos.especialidad() == null): Si no se proporciona una especialidad en los datos de consulta, se lanza una excepción ValidacionDeIntegridad indicando que debe seleccionarse una especialidad.

-   return medicoRepository.seleccionarMedicoConsEspecialidadEnFecha(datos.especialidad(), datos.fecha()): Si se proporciona una especialidad y no se especifica un médico por su ID, se utiliza el método seleccionarMedicoConsEspecialidadEnFecha del repositorio medicoRepository. Este método personalizado utiliza una consulta personalizada mediante la anotación @Query para buscar un médico disponible con la especialidad especificada en la fecha proporcionada.

#### MedicoRepository

    public interface MedicoRepository extends JpaRepository<Medico, Long> {

        Page<Medico> findByActivoTrue(Pageable paginacion);

        @Query("""
                select m from Medico m
                where m.activo = 1 and
                m.especialidad =: especialidad and
                m.id not in(
                    select c.medico.id from Consulta c
                    c.data =: fecha
                )
                order by rand()
                limit 1
                """)
        Medico seleccionarMedicoConsEspecialidadEnFecha(Especialidad especialidad, LocalDateTime fecha);

    }

Se ha agregado un nuevo método personalizado seleccionarMedicoConsEspecialidadEnFecha utilizando la anotación @Query:

-   @Query: Esta anotación se usa para definir consultas personalizadas en JPA. La consulta SQL personalizada se escribe dentro de la anotación y debe estar en JPQL (Java Persistence Query Language). JPQL es un lenguaje de consultas similar a SQL pero orientado a objetos.

La consulta @Query en este caso:

-   Selecciona un médico con una especialidad específica (m.especialidad =: especialidad) que esté disponible en la fecha proporcionada (c.data =: fecha).

-   La subconsulta (select c.medico.id from Consulta c where c.data =: fecha) se utiliza para excluir médicos que ya tienen una consulta agendada en la fecha dada.

-   Utiliza order by rand() para ordenar los médicos de forma aleatoria y limit 1 para seleccionar solo un médico.

En resumen, con estos cambios, el método seleccionarMedico del servicio AgendaDeConsultaService ahora selecciona un médico disponible para la consulta según los criterios de especialidad y disponibilidad en la fecha dada. La consulta personalizada se realiza mediante la anotación @Query en el repositorio MedicoRepository.

## Continuando...

4.  Ahora subiremos el proyecto a GitHub, para ello abriremos la consola de git bash e iremos colocando los siguientes comandos:

        $ git init
        $ git status
        $ git add *
        $ git status
        $ git commit -m "first commit"
        $ git remote add origin https://github.com/LuisEmmanuel27/proyecto_medicos_voll_med.git
        $ git push origin master
        $ git pull origin master --allow-unrelated-histories
        $ git push --set-upstream origin master

### Explicacion de los comandos

1. git init:
   Este comando inicializa un nuevo repositorio de Git en el directorio actual. Crea una estructura de control de versiones de Git en la carpeta y te permite comenzar a realizar seguimiento de los cambios en tus archivos.

2. git status:
   Este comando te muestra el estado actual del repositorio. Te proporciona información sobre qué archivos han sido modificados, agregados al área de preparación o están sin seguimiento.

3. git add _:
   Este comando agrega todos los archivos modificados y nuevos al área de preparación. El asterisco (_) se utiliza para seleccionar todos los archivos. Esto prepara los cambios para el próximo commit.

4. git status:
   Nuevamente, usas git status para ver cómo se ha actualizado el estado del repositorio después de agregar los archivos al área de preparación. Ahora los archivos que has agregado deberían aparecer en el área de preparación.

5. git commit -m "first commit":
   Con este comando, creas un commit que incluye los cambios en el área de preparación. El mensaje entre comillas después de -m es una descripción concisa del contenido del commit. En este caso, estás realizando el "primer commit" con los cambios que agregaste.

6. git remote add origin https://github.com/LuisEmmanuel27/proyecto_medicos_voll_med.git:
   Este comando establece una conexión entre tu repositorio local y un repositorio remoto en GitHub. "Origin" es el nombre comúnmente utilizado para referirse al repositorio remoto.

7. git push origin master:
   Con este comando, estás enviando tus commits locales al repositorio remoto en la rama "master". Esto sube tus cambios y actualizaciones al repositorio en línea en GitHub.

8. git pull origin master --allow-unrelated-histories:
   Este comando se utiliza para traer los cambios desde el repositorio remoto en la rama "master" y fusionarlos en tu rama local. La opción --allow-unrelated-histories se usa si los historiales de ambos repositorios no están relacionados. Sin embargo, es importante señalar que no es necesario ejecutar git pull inmediatamente después de haber hecho un git push. Puedes usar git pull cuando quieras actualizar tu repositorio local con los cambios del repositorio remoto.

9. git push --set-upstream origin master:
   se utiliza para establecer una relación de seguimiento entre tu rama local y una rama remota. Esto significa que después de ejecutar este comando, no necesitas especificar explícitamente la rama remota y local cuando realices futuros push o pull. En su lugar, puedes simplemente usar git push o git pull, y Git sabrá a qué rama remota y local se refiere.

    Aquí hay una explicación más detallada de cada parte del comando:

    - git push: El comando para enviar tus cambios locales al repositorio remoto.

    - --set-upstream (también conocido como -u): Esto establece la rama remota como rama de seguimiento para la rama local actual. Esto significa que tu rama local "sabe" a qué rama remota está vinculada, lo que simplifica futuras operaciones de push y pull.

    - origin: El nombre del control remoto. En este caso, estás utilizando "origin" como el nombre del control remoto que apunta al repositorio remoto en GitHub.

    - master: El nombre de la rama local que deseas vincular con la rama remota en GitHub. En este caso, estás utilizando "master".

    Entonces, cuando ejecutas git push --set-upstream origin master, estás enviando los cambios de tu rama local "master" al repositorio remoto en GitHub y estableciendo que esta rama local "master" está siguiendo a la rama remota "master" en el repositorio remoto.

    Una vez que has establecido esta relación de seguimiento, en futuros push y pull, puedes usar simplemente git push y git pull sin necesidad de especificar las ramas locales y remotas, siempre que estés trabajando en la misma rama en la que estableciste el seguimiento.

### Explicando como hacer push con los comandos:

podemos actualizar los cambios que hagamos con los siguientes comandos:

    $ git add .
    $ git commit -m "Mensaje descriptivo del cambio"
    $ git push origin <nombre-de-tu-rama>
    $ git pull origin <nombre-de-tu-rama>

En el caso de `git add .`, El punto (.) indica que se deben agregar todos los archivos modificados y nuevos en el directorio actual y sus subdirectorios.

# Continuacion en notas2.md
