package med.voll.api.domain.consulta.validaciones;

import java.time.DayOfWeek;

import jakarta.validation.ValidationException;
import med.voll.api.domain.consulta.DatosAgendarConsulta;

public class HorarioDeFuncionamientoClinica {
    public void validar(DatosAgendarConsulta datos) {

        var domingo = DayOfWeek.SUNDAY.equals(datos.fecha().getDayOfWeek());
        var antesDeApretura = datos.fecha().getHour() < 7;
        var despuesDeApretura = datos.fecha().getHour() > 19;

        if (domingo || antesDeApretura || despuesDeApretura) {
            throw new ValidationException(
                    "El horario de atencion de la clinica es de lunes a sabado de 07:00 a 19:00 horas");
        }

    }
}
