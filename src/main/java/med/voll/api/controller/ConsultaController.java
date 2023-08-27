package med.voll.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.transaction.Transactional;

import med.voll.api.domain.consulta.DatosDetalleConsulta;
import med.voll.api.domain.consulta.AgendaDeConsultaService;
import med.voll.api.domain.consulta.ValidarCancelarConsulta;
import med.voll.api.domain.consulta.ConsultaRepository;
import med.voll.api.domain.consulta.DatosAgendarConsulta;
import med.voll.api.domain.consulta.DatosCancelarConsulta;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    @Autowired
    private AgendaDeConsultaService service;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private ValidarCancelarConsulta validarCancelarConsulta;

    @PostMapping
    @Transactional
    public ResponseEntity<DatosDetalleConsulta> agendar(@RequestBody @Valid DatosAgendarConsulta datos) {
        var response = service.agendar(datos);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Void> cancelar(@RequestBody @Valid DatosCancelarConsulta datosCancelarConsulta) {
        var consulta = consultaRepository.getReferenceById(datosCancelarConsulta.id());
        validarCancelarConsulta.validar(consulta);
        consulta.agregarMotivoCancelacion(datosCancelarConsulta);
        return ResponseEntity.noContent().build();
    }

}
