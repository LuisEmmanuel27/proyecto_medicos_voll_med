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
            where p.id =: idPaciente
            """)
    Boolean findActivoById(Long idPaciente);

-   El método findActivoById se agrega a PacienteRepository para obtener el estado de actividad de un paciente mediante su ID.

-   Utiliza la anotación @Query para definir una consulta personalizada en lenguaje JPQL.

    -   select p.activo indica que estamos seleccionando el valor del atributo activo en la entidad Paciente.

    -   from Paciente p establece el alias p para la entidad Paciente. Esto nos permite referirnos a la entidad de manera abreviada en la consulta.

    -   where p.id = :idPaciente es la cláusula WHERE de la consulta. Estamos filtrando las filas de la tabla Paciente donde el valor del atributo id coincide con el valor proporcionado en el parámetro idPaciente.

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
                    where m.id =: idMedico
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
