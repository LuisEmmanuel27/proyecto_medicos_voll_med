# Clases de validacion

Como las validaciones no son tan simples como ver si algo esta o no, si no que es más verificacion de horas, fechas y demas como se especifico al principio de `notas.md` debemos resolver esto de cierta forma.

1.  primero crearemos otro paquete dentro del paquete `consulta` de nombre `validaciones`

2.  empecemos por la validacion de `El horario de atencion de la clinica es de lunes a sabado de 07:00 a 19:00 horas`, para ello dentro del paquete `validaciones` crearemos la clase `HorarioDeFuncionamientoClinica.java`

3.  dentro de la clase agregaremos el siguiente codigo:

        package med.voll.api.domain.consulta.validaciones;

        import java.time.DayOfWeek;

        import jakarta.validation.ValidationException;
        import med.voll.api.domain.consulta.DatosAgendarConsulta;

        public class HorarioDeFuncionamientoClinica {
            public void validar(DatosAgendarConsulta datos) {

                var domingo = DayOfWeek.SUNDAY.equals(datos.fecha().getDayOfWeek());
                var antesDeApretura = datos.fecha().getHour() < 7;
                var despuesDeApretura = datos.fecha().getHour() > 19;

                if (domingo || antesDeApretura || despuesDeApretura) {
                    throw new ValidationException(
                            "El horario de atencion de la clinica es de lunes a sabado de 07:00 a 19:00 horas");
                }

            }
        }

### Explicacion del codigo:

Esta clase representa una validación específica relacionada con el horario de atención de la clínica. Su objetivo es asegurarse de que la consulta se programe dentro del horario de funcionamiento establecido.

-   El método validar acepta un objeto DatosAgendarConsulta como parámetro. Este objeto contiene la información necesaria para realizar las validaciones.

-   var domingo = DayOfWeek.SUNDAY.equals(datos.fecha().getDayOfWeek());: Se verifica si la fecha proporcionada corresponde a un domingo.

-   var antesDeApretura = datos.fecha().getHour() < 7;: Se verifica si la hora de la fecha proporcionada es antes de las 7:00 AM.

-   var despuesDeApretura = datos.fecha().getHour() > 19;: Se verifica si la hora de la fecha proporcionada es después de las 7:00 PM.

-   Si cualquiera de estas tres condiciones se cumple, se lanza una excepción ValidationException con el mensaje indicando que el horario de atención de la clínica es de lunes a sábado de 07:00 a 19:00 horas.

Esta clase HorarioDeFuncionamientoClinica se utiliza para garantizar que las consultas se programen dentro del horario de atención permitido por la clínica, de acuerdo con los requisitos establecidos. Se puede agregar esta validación en el proceso de agendamiento de consultas para asegurarse de que las consultas se programen correctamente en términos de horario.

## Continuando...

4.  Ahora dentro del mismo paquete `validaciones` crearemos una nueva clase de nombre `HorarioDeAnticipacion.java`:

        package med.voll.api.domain.consulta.validaciones;

        import jakarta.validation.ValidationException;
        import med.voll.api.domain.consulta.DatosAgendarConsulta;

        import java.time.*;

        public class HorarioDeAnticipacion {
            public void validar(DatosAgendarConsulta datos) {
                var ahora = LocalDateTime.now();
                var horaDeConsulta = datos.fecha();

                var diferenciaDe30Min = Duration.between(ahora, horaDeConsulta).toMinutes() < 30;

                if (diferenciaDe30Min) {
                    throw new ValidationException("Las consultas deben programarse al menos 30 minutos de anticipacion");
                }
            }
        }

### Explicacion del codigo:

Esta clase representa una validación que verifica si una consulta se programa con al menos 30 minutos de anticipación con respecto al momento actual.

-   El método validar acepta un objeto DatosAgendarConsulta como parámetro. Este objeto contiene la información necesaria para realizar las validaciones.

-   var ahora = LocalDateTime.now();: Obtiene el momento actual.

-   var horaDeConsulta = datos.fecha();: Obtiene la fecha y hora de la consulta proporcionada en los datos.

-   var diferenciaDe30Min = Duration.between(ahora, horaDeConsulta).toMinutes() < 30;: Calcula la diferencia en minutos entre el momento actual y la hora de la consulta. Si esta diferencia es menor a 30 minutos, significa que la consulta se programó con menos de 30 minutos de anticipación.

-   Si la condición diferenciaDe30Min se cumple, se lanza una excepción ValidationException con el mensaje indicando que las consultas deben programarse al menos 30 minutos de anticipación.

Esta clase HorarioDeAnticipacion se utiliza para garantizar que las consultas se programen con suficiente anticipación antes del horario de la consulta real, de acuerdo con los requisitos establecidos. Se puede agregar esta validación en el proceso de agendamiento de consultas para asegurarse de que las consultas se programen con el tiempo adecuado de anticipación.

# validacion

1.  ahora crearemos igual dentro de `validaciones`, la clase `PacienteActivo.java`:

        package med.voll.api.domain.consulta.validaciones;

        import med.voll.api.domain.consulta.DatosAgendarConsulta;
        import med.voll.api.domain.paciente.PacienteRepository;
        import org.springframework.beans.factory.annotation.Autowired;

        import jakarta.validation.ValidationException;

        public class PacienteActivo {

            @Autowired
            private PacienteRepository pacienteRepository;

            public void validar(DatosAgendarConsulta datos) {

                if (datos.idPaciente() == null) {
                    return;
                }

                var pacienteActivo = pacienteRepository.findActivoById(datos.idPaciente());

                if (!pacienteActivo) {
                    throw new ValidationException("NO permitir agendar citas con pacientes inactivos en el sistema");
                }

            }
        }

2.  Si nos damos cuenta usamos un metodo no existente de nombre `findActivoById`, por lo cual tendremos que crearlo en el `PacienteRepository` teniendo la siguiente forma:

        package med.voll.api.domain.paciente;

        import org.springframework.data.domain.Page;
        import org.springframework.data.domain.Pageable;
        import org.springframework.data.jpa.repository.JpaRepository;
        import org.springframework.data.jpa.repository.Query;

        public interface PacienteRepository extends JpaRepository<Paciente, Long> {

            Page<Paciente> findByActivoTrue(Pageable paginacion);

            @Query("""
                    select p.activo
                    from Paciente p
                    where p.id =: idPaciente
                    """)
            Boolean findActivoById(Long idPaciente);

        }

### Explicacion del codigo:

#### Clase PacienteActivo

    package med.voll.api.domain.consulta.validaciones;

    import med.voll.api.domain.consulta.DatosAgendarConsulta;
    import med.voll.api.domain.paciente.PacienteRepository;
    import org.springframework.beans.factory.annotation.Autowired;

    import jakarta.validation.ValidationException;

    public class PacienteActivo {

        @Autowired
        private PacienteRepository pacienteRepository;

        public void validar(DatosAgendarConsulta datos) {

            if (datos.idPaciente() == null) {
                return;
            }

            var pacienteActivo = pacienteRepository.findActivoById(datos.idPaciente());

            if (!pacienteActivo) {
                throw new ValidationException("NO permitir agendar citas con pacientes inactivos en el sistema");
            }

        }
    }

La clase PacienteActivo es una clase de validación que verifica si un paciente está activo en el sistema antes de agendar una consulta.

-   El método validar acepta un objeto DatosAgendarConsulta como parámetro. Este objeto contiene la información necesaria para realizar la validación.

-   if (datos.idPaciente() == null): Verifica si el ID del paciente proporcionado en los datos de consulta es nulo. Si es nulo, significa que no se proporcionó un ID de paciente y, por lo tanto, no se realiza la validación.

-   var pacienteActivo = pacienteRepository.findActivoById(datos.idPaciente());: Utiliza el repositorio de pacientes (PacienteRepository) para buscar si el paciente con el ID proporcionado está activo.

-   Si pacienteActivo es falso (es decir, el paciente no está activo), se lanza una excepción ValidationException con el mensaje indicando que no se permiten agendar citas con pacientes inactivos en el sistema.

#### Método findActivoById en PacienteRepository

    @Query("""
            select p.activo
            from Paciente p
            where p.id=:idPaciente
            """)
    Boolean findActivoById(Long idPaciente);

-   El método findActivoById se agrega a PacienteRepository para obtener el estado de actividad de un paciente mediante su ID.

-   Utiliza la anotación @Query para definir una consulta personalizada en lenguaje JPQL.

    -   select p.activo indica que estamos seleccionando el valor del atributo activo en la entidad Paciente.

    -   from Paciente p establece el alias p para la entidad Paciente. Esto nos permite referirnos a la entidad de manera abreviada en la consulta.

    -   where p.id=:idPaciente es la cláusula WHERE de la consulta. Estamos filtrando las filas de la tabla Paciente donde el valor del atributo id coincide con el valor proporcionado en el parámetro idPaciente.

    -   :idPaciente es un parámetro de consulta. En este caso, es un parámetro nombrado que se vinculará con el valor proporcionado en el método findActivoById.

-   Boolean es el tipo de dato que se espera devolver como resultado de la consulta. En este caso, estamos esperando obtener el valor del atributo activo, que es un valor booleano (true o false).

## Continuando...

3.  hacemos lo mismo pero para medico, por lo que creamos `MedicoActivo.java` y modificaremos `MedicoRepository` casi que de la misma manera que se hizo con la validacion de `PacienteActivo`:

    `MedicoActivo.java`:

        package med.voll.api.domain.consulta.validaciones;

        import med.voll.api.domain.consulta.DatosAgendarConsulta;
        import med.voll.api.domain.medico.MedicoRepository;
        import org.springframework.beans.factory.annotation.Autowired;

        import jakarta.validation.ValidationException;

        public class MedicoActivo {

            @Autowired
            private MedicoRepository medicoRepository;

            public void validar(DatosAgendarConsulta datos) {

                if (datos.idMedico() == null) {
                    return;
                }

                var medicoActivo = medicoRepository.findActivoById(datos.idMedico());

                if (!medicoActivo) {
                    throw new ValidationException("NO permitir agendar citas con pacientes inactivos en el sistema");
                }
            }

        }

    `MedicoRepository metodo agregado`:

        @Query("""
                    select m.activo
                    from Medico m
                    where m.id=:idMedico
                        """)
            Boolean findActivoById(Long idMedico);

4.  ahora crearemos la clase `PacienteSinconsulta` dentro de `validaciones`:

        package med.voll.api.domain.consulta.validaciones;

        import med.voll.api.domain.consulta.DatosAgendarConsulta;
        import med.voll.api.domain.consulta.ConsultaRepository;
        import org.springframework.beans.factory.annotation.Autowired;

        import jakarta.validation.ValidationException;

        public class PacienteSinConsulta {

            @Autowired
            private ConsultaRepository consultaRepository;

            public void validar(DatosAgendarConsulta datos) {

                var primerHorario = datos.fecha().withHour(7);
                var ultimoHorario = datos.fecha().withHour(18);

                var pacienteConConsulta = consultaRepository.existByPacienteIdAndDataBetween(datos.idPaciente(), primerHorario,
                        ultimoHorario);

                if (pacienteConConsulta) {
                    throw new ValidationException("El paciente ya tiene una consulta para ese dia");
                }
            }
        }

5.  debido a que el metodo `existByPacienteIdAndDataBetween` no existe como tal vamos a crearlo en `ConsultaRepository`:

        public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

            Boolean existByPacienteIdAndDataBetween(
                    Long idPaciente,
                    LocalDateTime primerHorario,
                    LocalDateTime ultimoHorario);

        }

### Explicacion del codigo:

#### PacienteSinConsulta

    package med.voll.api.domain.consulta.validaciones;

    import med.voll.api.domain.consulta.DatosAgendarConsulta;
    import med.voll.api.domain.consulta.ConsultaRepository;
    import org.springframework.beans.factory.annotation.Autowired;

    import jakarta.validation.ValidationException;

    public class PacienteSinConsulta {

        @Autowired
        private ConsultaRepository consultaRepository;

        public void validar(DatosAgendarConsulta datos) {

            var primerHorario = datos.fecha().withHour(7);
            var ultimoHorario = datos.fecha().withHour(18);

            var pacienteConConsulta = consultaRepository.existByPacienteIdAndDataBetween(
                datos.idPaciente(),
                primerHorario,
                ultimoHorario
            );

            if (pacienteConConsulta) {
                throw new ValidationException("El paciente ya tiene una consulta para ese día");
            }
        }
    }

-   @Autowired private ConsultaRepository consultaRepository;
    Anotación que inyecta automáticamente una instancia de ConsultaRepository en la clase. Esto te permite utilizar métodos del repositorio sin crear una instancia manualmente.

-   public void validar(DatosAgendarConsulta datos)
    Método que realiza la validación de si un paciente ya tiene una consulta para el día especificado en los datos proporcionados.

-   var primerHorario = datos.fecha().withHour(7);
    Crea una variable primerHorario que representa la fecha y hora con la hora establecida en 7 AM.

-   var ultimoHorario = datos.fecha().withHour(18);
    Crea una variable ultimoHorario que representa la fecha y hora con la hora establecida en 6 PM.

-   var pacienteConConsulta = consultaRepository.existByPacienteIdAndDataBetween
    Invoca el método existByPacienteIdAndDataBetween del repositorio para verificar si el paciente ya tiene una consulta programada para el rango de fechas entre primerHorario y ultimoHorario.

-   datos.idPaciente(), primerHorario, ultimoHorario
    Pasa los argumentos requeridos al método existByPacienteIdAndDataBetween: el ID del paciente, el primer horario y el último horario.

-   if (pacienteConConsulta) { throw new ValidationException("El paciente ya tiene una consulta para ese día"); }
    Si pacienteConConsulta es true (lo que significa que el paciente ya tiene una consulta en ese día y horario), se lanza una excepción ValidationException con un mensaje indicando que el paciente ya tiene una consulta para ese día.

#### ConsultaRepository metodo existByPacienteIdAndDataBetween

    Boolean existByPacienteIdAndDataBetween(
        Long idPaciente,
        LocalDateTime primerHorario,
        LocalDateTime ultimoHorario);

En esta interfaz, has declarado el método existByPacienteIdAndDataBetween, que no es un método por defecto de Spring Data JPA. Sin embargo, Spring Data JPA tiene la capacidad de analizar y entender los nombres de los métodos en los repositorios y generar automáticamente consultas SQL correspondientes.

En este caso, al usar el nombre existByPacienteIdAndDataBetween, Spring Data JPA entenderá que debe crear una consulta para verificar si existen registros en la tabla de consultas donde el ID del paciente se encuentra dentro del rango de fechas especificado (primerHorario y ultimoHorario).

Es decir, Spring Data JPA analizará el nombre del método y generará una consulta SQL que verificará si existen registros que cumplan con esas condiciones. Esto es posible gracias a la convención de nombres de Spring Data JPA y a su capacidad para interpretar y generar consultas basadas en esos nombres.

## Continuamos...

6.  realizamos lo mismo para Medico con leves cambios:

    `creamos MedicoConConsulta.java`:

        package med.voll.api.domain.consulta.validaciones;

        import org.springframework.beans.factory.annotation.Autowired;

        import jakarta.validation.ValidationException;
        import med.voll.api.domain.consulta.ConsultaRepository;
        import med.voll.api.domain.consulta.DatosAgendarConsulta;

        public class MedicoConConsulta {
            @Autowired
            private ConsultaRepository consultaRepository;

            public void validar(DatosAgendarConsulta datos) {
                if (datos.idMedico() == null) {
                    return;
                }

                var medicoConConsulta = consultaRepository.existByMedicoIdAndData(datos.idMedico(), datos.fecha());

                if (medicoConConsulta) {
                    throw new ValidationException("El medico ya tiene una consulta para ese mismo horario");
                }
            }
        }

    `Creamos el metodo existByMedicoIdAndDataBetween`:

        public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

            Boolean existByPacienteIdAndDataBetween(
                    Long idPaciente,
                    LocalDateTime primerHorario,
                    LocalDateTime ultimoHorario);

            Boolean existByMedicoIdAndData(
                    Long idMedico,
                    LocalDateTime fecha);
        }

# Aplicando principios SOLID

Ahora ya que tenemos todas las clases con cada una de las validaciones toca aplicarlas al momento de hacer uso de nuestro `AgendaDeConsultaService.java`, pero si colocamos una a una en dicha clase y en algun momento por reglas del negocio se elimina una de estas validaciones tendriamos que eliminar dicha clase y estar modificando `AgendaDeConsultaService.java`, para evitar eso debemos hacer uso de polimorfismo en el sentido de los principios de `SOLID`, si nos fijamos en cada uno de los metodos de validacion tienen algo en comun y es:

    public void validar(DatosAgendarConsulta datos)

asi que en base a eso vamos a crear una interface para indicar que es un patron que se debe seguir.

1.  Para comenzar con lo anterior vamos a el paquete `validaciones` y crearemos la interface `ValidadorDeConsultas.java`

2.  Recordando que tienen en comun todos los validadores agregamos el siguiente codigo:

        package med.voll.api.domain.consulta.validaciones;

        import med.voll.api.domain.consulta.DatosAgendarConsulta;

        public interface ValidadorDeConsultas {
            public void validar(DatosAgendarConsulta datos);
        }

3.  Ahora simplemente en cada validador que hicimos colocaremos el `implements ValidadorDeConsultas`, ademas de hacer uso de la notacion @Component de spring por ejemplo:

        @Component
        public class MedicoConConsulta implements ValidadorDeConsultas

### Explicacion de lo realizado:

#### Principio SOLID - Responsabilidad Única (Single Responsibility Principle):

El principio SOLID de Responsabilidad Única establece que una clase debe tener una única razón para cambiar. En otras palabras, una clase debe tener una sola responsabilidad en el sistema. Al aplicar este principio, se busca evitar que una clase realice demasiadas tareas distintas y, en cambio, dividirlas en clases más pequeñas y especializadas.

#### Polimorfismo:

El polimorfismo es un concepto de la programación orientada a objetos que permite que diferentes objetos respondan a un mismo mensaje o llamada de método de manera distinta. En este caso, estás aplicando polimorfismo mediante una interfaz común (`ValidadorDeConsultas`) que define un método `validar`. Varios validadores diferentes implementan esta interfaz y proporcionan su propia implementación del método `validar`.

#### Cambios Realizados y Explicación:

-   Se creó una interfaz llamada ValidadorDeConsultas que define un método validar(DatosAgendarConsulta datos) común a todos los validadores.

-   Cada validador, como MedicoConConsulta, implementa la interfaz ValidadorDeConsultas, lo que significa que todos estos validadores deben proporcionar una implementación del método validar.

-   Se agregó la anotación @Component a cada validador, lo que indica que estos componentes serán gestionados por Spring y se les permitirá ser inyectados en otras clases mediante la anotación @Autowired.

-   Si un validador necesita acceder a un repositorio, como ConsultaRepository, se agregó la anotación @Autowired para permitir que Spring inyecte automáticamente la instancia del repositorio en la clase.

#### Relación con Principios SOLID:

-   Responsabilidad Única (SRP): Al definir la interfaz ValidadorDeConsultas, se está separando la responsabilidad de validar las consultas en clases independientes, cada una con su propia implementación específica.

-   Abierto/Cerrado (OCP): La estructura basada en interfaces permite agregar nuevos validadores en el futuro sin modificar el código existente, siguiendo el principio de que las clases deben estar abiertas a extensiones pero cerradas a modificaciones.

#### Ventajas de este Enfoque:

-   Reusabilidad: Al definir una interfaz común, se puede reutilizar el código al inyectar diferentes validadores donde sea necesario.

-   Flexibilidad: Se pueden agregar nuevos validadores o cambiar su lógica sin afectar la clase que agenda las consultas.

-   Mantenibilidad: Cada validador tiene una responsabilidad única y se puede modificar o extender de manera aislada sin afectar otras partes del sistema.

En resumen, al aplicar el principio SOLID del polimorfismo a través de una interfaz común y clases especializadas, estás creando una estructura que permite la reutilización, la flexibilidad y la mantenibilidad de la lógica de validación de consultas en tu aplicación.

## Continuando...

4.  Ahora con todo lo anterior realizado podemos volver a `AgendaDeConsultasService.java` y agregar las siguientes lineas de codigo:

        @Autowired
        List<ValidadorDeConsultas> valdadores;

### Explicacion del codigo:

-   @Autowired: Esta anotación le indica a Spring que debe inyectar automáticamente una instancia de la lista validadores. Spring escaneará el contexto de la aplicación en busca de todas las implementaciones de la interfaz ValidadorDeConsultas y las agregará a esta lista.

-   `List<ValidadorDeConsultas> validadores`: Aquí estás definiendo una lista de objetos que implementan la interfaz ValidadorDeConsultas. Cada elemento en esta lista será un validador específico que verifica diferentes condiciones.

#### Cómo Funciona:

-   Cuando Spring inyecta la lista validadores, busca todas las clases que implementan la interfaz ValidadorDeConsultas y las agrega automáticamente a la lista.

-   Esto se basa en el concepto de descubrimiento automático de componentes en Spring. Spring escanea el classpath en busca de clases que tengan la anotación @Component (u otras anotaciones similares) y las registra como componentes administrados por Spring.

-   Esto permite que la lista validadores contenga todas las implementaciones de validadores disponibles en el sistema.

#### Ventajas:

-   Flexibilidad: Puedes agregar nuevos validadores simplemente creando nuevas clases que implementen la interfaz ValidadorDeConsultas y anotándolas con @Component.

-   Cambios sin modificar el código: No necesitas modificar la clase AgendaDeConsultasService cada vez que agregas un nuevo validador. Esto cumple con el principio Open/Closed del principio SOLID.

## Continuando...

5.  finalmente solo colocaremos un forEach y un arrow function para hacer que se apliquen cada uno de los validadores con los datos enviados:

        public void agendar(DatosAgendarConsulta datos) {

                if (!pacienteRepository.findById(datos.idPaciente()).isPresent()) {
                    throw new ValidacionDeIntegridad("este id para el paciente no fue encontrado");
                }

                if (datos.idMedico() != null && !medicoRepository.existsById(datos.idMedico())) {
                    throw new ValidacionDeIntegridad("este id para el medico no fue encontrado");
                }

                validadores.forEach(val -> val.validar(datos)); // Arrow Function

                var paciente = pacienteRepository.findById(datos.idPaciente()).get();
                var medico = seleccionarMedico(datos);

                var consulta = new Consulta(null, medico, paciente, datos.fecha());
                consultaRepository.save(consulta);

            }

# Testeando la agenda

## Nota importante:

Al momento de compilar si dejamos las cosas como estan nos dara error, ya que hay un par de problemas a solucionar.

-   En primera modificamos los nombres de los metodos en ConsultaRepository:

    public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

          Boolean existsByPacienteIdAndFechaBetween(Long idPaciente, LocalDateTime primerHorario,
                  LocalDateTime ultimoHorario);

          Boolean existsByMedicoIdAndFecha(Long idMedico, LocalDateTime fecha);

    }

Pasamos de Data a Fecha

-   En segunda en MedicoRepository modificamos la primera query para que en vez de `m.activo = 1` sea `m.activo = true` y cambiamos el nombre del metodo igual cambiando Data por Fecha:

          @Query("""
                  select m from Medico m
                  where m.activo= true
                  and
                  m.especialidad=:especialidad
                  and
                  m.id not in(
                      select c.medico.id from Consulta c
                      where
                      c.fecha=:fecha
                  )
                  order by rand()
                  limit 1
                  """)
          Medico seleccionarMedicoConEspecialidadEnFecha(Especialidad especialidad, LocalDateTime fecha);

Una vez realizado lo anterior no deberiamos tener problemas de momento

## Continuando...

1.  Compilamos el codigo y vamos a probar cada una de las validaciones, claro enviado la informacion de manera erronea para que nos salten dichos problemas

2.  vamos al thunder, insomnia o postman y probaremos el POST de agendar consulta que creamos hace tiempo y colocaremos el siguiente cuerpo (recordando claro tener un token valido de login):

        {
            "idPaciente":"17765764",
            "idMedico":"1",
            "fecha":"2023-10-12T10:30"
        }

3.  lo anterior nos dara un 403 forbbiden y en la consola nos dara el mensaje de error que colocamos, el de: `med.voll.api.infra.errores.ValidacionDeIntegridad: este id para el paciente no fue encontrado`

4.  claro que nos seria mas util dicho error verlo en el cuerpo de thunder, insomnia o postman. Por lo que es necesario hacer un par de cambios

5.  vamos al `TratadorDeErrores.java` y en base a que en consola si nos fijamos bien en el mensaje el error lo trata el `ValidacionDeIntegridad` colocaremos el siguiente metodo:

        @ExceptionHandler(ValidacionDeIntegridad.class)
        public ResponseEntity<String> errorHandlerValidacionesDeNegocio(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

6.  compilamos y volvemos a intentar agendar erroneamente la consulta y esta vez veremos el mensaje de error en el body de la respuesta

7.  ahora probamos lo siguiente:

        {
            "idPaciente":"1",
            "idMedico":"199",
            "fecha":"2023-10-12T10:30"
        }

8.  ahora intentamos donde si debe funcionar, osea con algo como esto:

        {
            "idPaciente":"1",
            "idMedico":"2",
            "fecha":"2023-10-12T10:30"
        }

9.  Pero recordemos que en el Controller retornamos todo null asi que debemos modificar eso, para ello vovemos al `ConsultaController.java`:

        @PostMapping
        @Transactional
        public ResponseEntity<DatosDetalleConsulta> agendar(@RequestBody @Valid DatosAgendarConsulta datos) {
            var response = service.agendar(datos);
            return ResponseEntity.ok(response);
        }

10. Luego de eso iremos al `AgendaDeConsultaService.java` y realizaremos los siguientes cambios:

        public DatosDetalleConsulta agendar(DatosAgendarConsulta datos) {

            if (!pacienteRepository.findById(datos.idPaciente()).isPresent()) {
                throw new ValidacionDeIntegridad("este id para el paciente no fue encontrado");
            }

            if (datos.idMedico() != null && !medicoRepository.existsById(datos.idMedico())) {
                throw new ValidacionDeIntegridad("este id para el medico no fue encontrado");
            }

            validadores.forEach(val -> val.validar(datos));

            var paciente = pacienteRepository.findById(datos.idPaciente()).get();
            var medico = seleccionarMedico(datos);

            var consulta = new Consulta(null, medico, paciente, datos.fecha());
            consultaRepository.save(consulta);

            return new DatosDetalleConsulta(consulta);

        }

11. lo cual nos lleva a crear un cambio en `DatosDetalleConsulta.java` osea un constructor que queda de la siguiente manera:

        public DatosDetalleConsulta(Consulta consulta) {
            this(
                    consulta.getId(),
                    consulta.getMedico().getId(),
                    consulta.getPaciente().getId(),
                    consulta.getFecha());
        }

12. volvemos a compilar y probamos el POST con algo como esto:

        {
            "idPaciente":"2",
            "idMedico":"4",
            "fecha":"2023-10-12T10:30"
        }

    y veremos el body de la respuesta es algo asi:

        {
            "id": 2,
            "idPaciente": 4,
            "idMedico": 2,
            "fecha": "2023-10-12T10:30:00"
        }

13. nos daremos cuenta que con ciertas validaciones como las de que el medico ya tiene una consulta reservada en el mismo horario no vienen de `ValidacionDeIntegridad` si no de otro lado, por ejemplo ese y otros errores vienen de `ValidationException` por lo que no saldra nada en el body de la peticion, asi que tenemos que volver a `TratadorDeErrores` y agregar el metodo que resuleva eso:

        @ExceptionHandler(ValidacionDeIntegridad.class)
        public ResponseEntity<String> errorHandlerValidacionesDeIntegridad(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        @ExceptionHandler(ValidationException.class)
        public ResponseEntity<String> errorHandlerValidacionesDeNegocio(Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    De paso le cambiamos el nombre al metodo que antes creamos, cambiando Negocio por Inegridad.

14. ahora si hacemos algo como esto:

        {
            "idPaciente":"2",
            "idMedico":"",
            "especialidad": "ORTOPEDIA",
            "fecha":"2023-10-21T11:30"
        }

    en ocaciones funciona pero tambien en otras nos tira este error:

        java.sql.SQLIntegrityConstraintViolationException: Column 'medico_id' cannot be null

15. Para resolver lo anterior tendremos que volver a `AgendaDeConsultaService` y agregar una validacion que omitimos:

        var medico = seleccionarMedico(datos);
        if (medico == null) {
            throw new ValidacionDeIntegridad("No existen medicos disponibles para este horario y especialidad");
        }

    la cual es simplemente si no obtenemos un medico de la seleccion aleatorea mandamos el mensaje de error, compilamos y volvemos a probar.

# Ejercicio

Agregar la cancelacion de consultas, donde forzosamente se debe enviar motivo de cancelacion y estas deben hacerse por lo menos 24 horas antes de la misma
