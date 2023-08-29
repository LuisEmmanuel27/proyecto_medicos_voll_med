package med.voll.api.domain.consulta;

import java.time.LocalDateTime;

import jakarta.validation.constraints.*;
import med.voll.api.domain.medico.Especialidad;

public record DatosAgendarConsulta(
        Long id,

        @NotNull Long idPaciente,

        Long idMedico,

        @NotNull @Future LocalDateTime fecha,

        Especialidad especialidad) {
}
