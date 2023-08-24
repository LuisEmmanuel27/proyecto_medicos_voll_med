package med.voll.api.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domain.direccion.DatosDireccion;
import med.voll.api.domain.medico.DatosActualizarMedico;
import med.voll.api.domain.medico.DatosListadoMedico;
import med.voll.api.domain.medico.DatosRegistroMedico;
import med.voll.api.domain.medico.DatosRespuestaMedico;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.medico.MedicoRepository;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoRepository medicoRepository;

    @PostMapping
    public ResponseEntity<DatosRespuestaMedico> registrarMedico(
            @RequestBody @Valid DatosRegistroMedico datosRegistroMedico,
            UriComponentsBuilder uriComponentsBuilder) {
        Medico medico = medicoRepository.save(new Medico(datosRegistroMedico));
        // * Return 201 Created */
        // * URL donde encontrar al medico */
        // * GET http://localhost:8080/medicos/{medico_id} */
        DatosRespuestaMedico respuestaMedico = construirDatosRespuestaMedico(medico);
        URI url = uriComponentsBuilder.path("medicos/{id}").buildAndExpand(medico.getId()).toUri();
        return ResponseEntity.created(url).body(respuestaMedico);
    }

    @GetMapping
    public ResponseEntity<Page<DatosListadoMedico>> listadoMedicos(@PageableDefault(size = 10) Pageable paginacion) {
        // return medicoRepository.findAll(paginacion).map(DatosListadoMedico::new);

        // despues de agregar el metodo Delete Logico...
        return ResponseEntity.ok(medicoRepository.findByActivoTrue(paginacion).map(DatosListadoMedico::new));
    }

    @PutMapping
    @Transactional
    public ResponseEntity<DatosRespuestaMedico> actualizarMedico(
            @RequestBody @Valid DatosActualizarMedico datosActualizarMedico) {
        Medico medico = medicoRepository.getReferenceById(datosActualizarMedico.id());
        medico.actualizarDatos(datosActualizarMedico);
        DatosRespuestaMedico respuestaMedico = construirDatosRespuestaMedico(medico);
        return ResponseEntity.ok(respuestaMedico);
    }

    @DeleteMapping("/{id}")
    @Transactional
    // Eliminacion logica
    public ResponseEntity<Void> eliminarMedico(@PathVariable Long id) {
        Medico medico = medicoRepository.getReferenceById(id);
        medico.desactivarMedico();
        return ResponseEntity.noContent().build();
    }

    // Eliminacion fisica
    // public void eliminarMedico(@PathVariable Long id) {
    // Medico medico = medicoRepository.getReferenceById(id);
    // medicoRepository.delete(medico);
    // }

    @GetMapping("/{id}")
    public ResponseEntity<DatosRespuestaMedico> retornaDatosMedico(@PathVariable Long id) {
        Medico medico = medicoRepository.getReferenceById(id);
        DatosRespuestaMedico respuestaMedico = construirDatosRespuestaMedico(medico);
        return ResponseEntity.ok(respuestaMedico);
    }

    /* ----------------------------------------------------------------- */
    // * De esta manera, estamos separando la lógica de construcción de la respuesta
    // * en el controlador, lo que hace que el código sea más organizado y mantenga
    // * la coherencia en la estructura.
    private DatosRespuestaMedico construirDatosRespuestaMedico(Medico medico) {
        DatosDireccion direccion = new DatosDireccion(
                medico.getDireccion().getCalle(),
                medico.getDireccion().getDistrito(),
                medico.getDireccion().getCiudad(),
                medico.getDireccion().getNumero(),
                medico.getDireccion().getComplemento());

        return new DatosRespuestaMedico(medico, direccion);
    }

}
