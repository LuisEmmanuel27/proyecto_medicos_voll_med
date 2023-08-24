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
