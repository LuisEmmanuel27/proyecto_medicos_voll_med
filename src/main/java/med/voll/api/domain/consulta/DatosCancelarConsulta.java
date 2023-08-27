package med.voll.api.domain.consulta;

import jakarta.validation.constraints.NotNull;

public record DatosCancelarConsulta(
                @NotNull Long id,

                @NotNull String motivoCancelacion

) {
}
