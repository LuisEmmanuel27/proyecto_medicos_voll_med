package med.voll.api.domain.paciente;

import med.voll.api.domain.direccion.DatosDireccion;
import jakarta.validation.constraints.NotNull;

public record DatosActualizarPaciente(
        @NotNull Long id,
        String nombre,
        String telefono,
        DatosDireccion direccion) {

}
