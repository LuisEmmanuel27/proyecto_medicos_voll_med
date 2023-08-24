package med.voll.api.domain.consulta;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    Boolean existByPacienteIdAndDataBetween(
            Long idPaciente,
            LocalDateTime primerHorario,
            LocalDateTime ultimoHorario);

    Boolean existByMedicoIdAndData(
            Long idMedico,
            LocalDateTime fecha);

}
