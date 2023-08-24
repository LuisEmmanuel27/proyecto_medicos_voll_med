package med.voll.api.domain.paciente;

import jakarta.persistence.*;

import lombok.*;

import med.voll.api.domain.direccion.Direccion;

@Table(name = "pacientes")
@Entity(name = "Paciente")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;
    private String documentoIdentidad;
    private String telefono;
    private Boolean activo = true;

    @Embedded
    private Direccion direccion;

    public Paciente(DatosRegistroPaciente datos) {
        this.nombre = datos.nombre();
        this.email = datos.email();
        this.telefono = datos.telefono();
        this.documentoIdentidad = datos.documentoIdentidad();
        this.direccion = new Direccion(datos.direccion());
    }

    public void actualizarDatos(DatosActualizarPaciente datos) {
        if (datos.nombre() != null) {
            this.nombre = datos.nombre();
        }

        if (datos.telefono() != null) {
            this.telefono = datos.telefono();
        }

        if (datos.direccion() != null) {
            this.direccion = direccion.actualizarDatos(datos.direccion());
        }
    }

    public void desactivarPaciente() {
        this.activo = false;
    }

}
