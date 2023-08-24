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
