package med.voll.api.domain.medico;

import med.voll.api.domain.direccion.DatosDireccion;

public record DatosRespuestaMedico(
		Long id,
		String nombre,
		String email,
		String telefono,
		String documento,
		Especialidad especialidad,
		DatosDireccion direccion) {

	public DatosRespuestaMedico(Medico medico, DatosDireccion direccion) {
		this(medico.getId(), medico.getNombre(), medico.getEmail(),
				medico.getTelefono(), medico.getDocumento(),
				medico.getEspecialidad(), direccion);
	}
}
