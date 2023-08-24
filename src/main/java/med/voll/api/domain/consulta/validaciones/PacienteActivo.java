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
            throw new ValidationException("NO permitir agendar citas con medicos inactivos en el sistema");
        }

    }
}
