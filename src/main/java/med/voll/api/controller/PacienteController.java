package med.voll.api.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.RequestBody;

import med.voll.api.domain.paciente.DatosRegistroPaciente;
import med.voll.api.domain.paciente.DatosRespuestaPaciente;
import med.voll.api.domain.direccion.DatosDireccion;
import med.voll.api.domain.paciente.DatosActualizarPaciente;
import med.voll.api.domain.paciente.DatosListadoPaciente;
import med.voll.api.domain.paciente.Paciente;
import med.voll.api.domain.paciente.PacienteRepository;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @PostMapping
    public ResponseEntity<DatosRespuestaPaciente> registrarPaciente(@RequestBody @Valid DatosRegistroPaciente datos,
            UriComponentsBuilder uriComponentsBuilder) {
        Paciente paciente = pacienteRepository.save(new Paciente(datos));
        DatosRespuestaPaciente datosRespuestaPaciente = construirDatosRespuestaPaciente(paciente);
        URI url = uriComponentsBuilder.path("pacientes/{id}").buildAndExpand(paciente.getId()).toUri();
        return ResponseEntity.created(url).body(datosRespuestaPaciente);
    }

    @GetMapping
    public ResponseEntity<Page<DatosListadoPaciente>> listadoPacientes(
            @PageableDefault(size = 10) Pageable paginacion) {
        return ResponseEntity.ok(pacienteRepository.findByActivoTrue(paginacion).map(DatosListadoPaciente::new));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<DatosRespuestaPaciente> actualizarPaciente(
            @RequestBody @Valid DatosActualizarPaciente datos) {
        Paciente paciente = pacienteRepository.getReferenceById(datos.id());
        paciente.actualizarDatos(datos);
        DatosRespuestaPaciente datosRespuestaPaciente = construirDatosRespuestaPaciente(paciente);
        return ResponseEntity.ok(datosRespuestaPaciente);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminarPaciente(@PathVariable Long id) {
        Paciente paciente = pacienteRepository.getReferenceById(id);
        paciente.desactivarPaciente();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaPaciente> retornaDatosPaciente(@PathVariable Long id) {
        Paciente paciente = pacienteRepository.getReferenceById(id);
        DatosRespuestaPaciente datosRespuestaPaciente = construirDatosRespuestaPaciente(paciente);
        return ResponseEntity.ok(datosRespuestaPaciente);
    }

    private DatosRespuestaPaciente construirDatosRespuestaPaciente(Paciente paciente) {
        DatosDireccion direccion = new DatosDireccion(
                paciente.getDireccion().getCalle(),
                paciente.getDireccion().getDistrito(),
                paciente.getDireccion().getCiudad(),
                paciente.getDireccion().getNumero(),
                paciente.getDireccion().getComplemento());

        return new DatosRespuestaPaciente(paciente, direccion);
    }

}
