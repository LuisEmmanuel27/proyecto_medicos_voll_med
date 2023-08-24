package med.voll.api.domain.consulta;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.*;
import med.voll.api.domain.medico.Especialidad;

public record DatosAgendarConsulta(
        Long id,
        @NotNull Long idPaciente,
        Long idMedico,
        @NotNull @Future @JsonFormat(pattern = "dd/MM/yyyy HH:mm") LocalDateTime fecha,
        Especialidad especialidad) {
}
