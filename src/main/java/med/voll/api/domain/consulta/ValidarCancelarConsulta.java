package med.voll.api.domain.consulta;

import java.time.*;

import org.springframework.stereotype.Component;

import jakarta.validation.ValidationException;

@Component
public class ValidarCancelarConsulta {

    public void validar(Consulta consulta) {
        var ahora = LocalDateTime.now();
        var horaDeConsulta = consulta.getFecha();

        var diferencia24Horas = Duration.between(ahora, horaDeConsulta).toHours() < 24;

        if (diferencia24Horas) {
            throw new ValidationException("Las consultas deben cancelarse con al menos 24 horas anticipacion");
        }

        consulta.desactivarConsulta();
    }

}
