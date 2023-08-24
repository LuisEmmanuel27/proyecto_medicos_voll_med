package med.voll.api.domain.paciente;

public record DatosListadoPaciente(
        Long id,
        String Nombre,
        String email,
        String documentoIdentidad) {

    public DatosListadoPaciente(Paciente paciente) {
        this(
                paciente.getId(),
                paciente.getNombre(),
                paciente.getEmail(),
                paciente.getDocumentoIdentidad());
    }
}
