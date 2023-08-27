package med.voll.api.domain.consulta;

import jakarta.persistence.*;
import jakarta.validation.ValidationException;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.paciente.Paciente;

import java.time.LocalDateTime;

@Table(name = "consultas")
@Entity(name = "Consulta")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Consulta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medico_id")
    private Medico medico;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paciente_id")
    private Paciente paciente;

    private LocalDateTime fecha;

    private String motivoCancelacion;
    private Boolean activo = true;

    public Consulta(Medico medico, Paciente paciente, LocalDateTime fecha) {
        this.medico = medico;
        this.paciente = paciente;
        this.fecha = fecha;
    }

    public void agregarMotivoCancelacion(DatosCancelarConsulta datosCancelarConsulta) {
        if (datosCancelarConsulta.motivoCancelacion() != null && datosCancelarConsulta.motivoCancelacion() != "") {
            this.motivoCancelacion = datosCancelarConsulta.motivoCancelacion();
        } else {
            throw new ValidationException("No estas especificando el motivo de cancelacion");
        }
    }

    public void desactivarConsulta() {
        this.activo = false;
    }

}
