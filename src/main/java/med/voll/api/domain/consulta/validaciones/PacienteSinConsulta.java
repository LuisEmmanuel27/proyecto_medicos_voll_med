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
