package it.prova.triage_be.web.api;

import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import it.prova.triage_be.dto.DottoreDaAggiornareDTO;
import it.prova.triage_be.dto.DottorePazienteDTO;
import it.prova.triage_be.dto.PazienteDTO;
import it.prova.triage_be.model.Paziente;
import it.prova.triage_be.service.paziente.PazienteService;
import it.prova.triage_be.web.api.exception.IdNotNullForInsertException;
import it.prova.triage_be.web.api.exception.OperazioneNonAndataABuonFineException;
import it.prova.triage_be.web.api.exception.PazienteNotFoundException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/paziente")
public class PazienteController {

	@Autowired
	private PazienteService pazienteService;

	@Autowired
	private WebClient webClient;

	private static final Logger LOGGER = LogManager.getLogger(DottoreWebController.class);

	@GetMapping
	public List<PazienteDTO> getAll() {
		return PazienteDTO.createPazienteDTOListFromModelList(pazienteService.listAllPazienti());
	}

	@GetMapping("/{id}")
	public PazienteDTO findUtente(@PathVariable(value = "id", required = true) Long id) {
		Paziente pazienteCaricato = pazienteService.caricaSingoloElemento(id);
		if (pazienteCaricato == null) {
			throw new PazienteNotFoundException("Paziente not found con id: " + id);
		}
		return PazienteDTO.buildPazienteDTOFromModel(pazienteCaricato);
	}

	@PostMapping("/inserisci")
	@ResponseStatus(HttpStatus.CREATED)
	public PazienteDTO insert(@Valid @RequestBody PazienteDTO pazienteInput) {
		if (pazienteInput.getId() != null) {
			throw new IdNotNullForInsertException("Non è ammesso fornire un id per la creazione");
		}

		Paziente pazienteInserito = pazienteService.inserisciNuovo(pazienteInput.buildPazienteModel());
		return PazienteDTO.buildPazienteDTOFromModel(pazienteInserito);
	}

	@PutMapping("/aggiorna/{id}")
	public PazienteDTO update(@Valid @RequestBody PazienteDTO pazienteInput,
			@PathVariable(value = "id", required = true) Long id) {
		Paziente pazienteCaricato = pazienteService.caricaSingoloElemento(id);
		if (pazienteCaricato == null) {
			throw new PazienteNotFoundException("Paziente not found con id: " + id);
		}
		pazienteInput.setId(id);
		Paziente pazienteAggiornato = pazienteService.aggiorna(pazienteInput.buildPazienteModel());
		return PazienteDTO.buildPazienteDTOFromModel(pazienteAggiornato);
	}

	@DeleteMapping("/elimina/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable(value = "id", required = true) Long id) {
		Paziente pazienteCaricato = pazienteService.caricaSingoloElemento(id);
		if (pazienteCaricato == null) {
			throw new PazienteNotFoundException("Paziente not found con id: " + id);
		}
		pazienteService.rimuovi(id);
	}

	@PostMapping("/searchWithPagination")
	public ResponseEntity<Page<PazienteDTO>> searchPaginated(@RequestBody PazienteDTO example,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "0") Integer pageSize,
			@RequestParam(defaultValue = "id") String sortBy) {
		Page<Paziente> entityPageResults = pazienteService.findByExampleWithPagination(example.buildPazienteModel(),
				pageNo, pageSize, sortBy);

		return new ResponseEntity<Page<PazienteDTO>>(PazienteDTO.fromModelPageToDTOPage(entityPageResults),
				HttpStatus.OK);
	}

	@PostMapping("/assegnaPaziente")
	public PazienteDTO assegnaPaziente(@Valid @RequestBody DottorePazienteDTO dottorePazienteDTO) {
		LOGGER.info("....invocazione servizio esterno....");
		
		ResponseEntity<DottorePazienteDTO> response = webClient.get().uri("/verifica/"+dottorePazienteDTO.getCodiceDottore())
				.retrieve().toEntity(DottorePazienteDTO.class).block();
		if (response.getStatusCode() != HttpStatus.OK)
			throw new OperazioneNonAndataABuonFineException("Attenzione! L'operazione non è andata a buon fine");
		
		ResponseEntity<DottorePazienteDTO> response1 = webClient.post().uri("/impostaInVisita")
				.accept(MediaType.APPLICATION_JSON).body(Mono.just(dottorePazienteDTO), DottoreDaAggiornareDTO.class)
				.retrieve().toEntity(DottorePazienteDTO.class).block();

		// ANDREBBE GESTITA CON ADVICE!!!
		if (response1.getStatusCode() != HttpStatus.NO_CONTENT)
			throw new OperazioneNonAndataABuonFineException("Attenzione! L'operazione non è andata a buon fine");
		LOGGER.info("....invocazione servizio esterno terminata....");
		Paziente pazienteCaricato = pazienteService.findByCf(dottorePazienteDTO.getCodFiscalePazienteAttualmenteInVisita());
		pazienteCaricato.setCodiceDottore(dottorePazienteDTO.getCodiceDottore());
		Paziente pazienteDaVisitare = pazienteService.impostaInVisita(pazienteCaricato);
		return PazienteDTO.buildPazienteDTOFromModel(pazienteDaVisitare);
	}

	@PostMapping("/ricovera/{id}")
	public PazienteDTO ricovera(@PathVariable(required = true, value = "id") Long id) {
		Paziente paziente = pazienteService.caricaSingoloElemento(id);
		if (paziente == null) {
			throw new PazienteNotFoundException("Paziente not found con id: " + id);
		}
//		

		DottorePazienteDTO dottorePazienteDTO = DottorePazienteDTO.builder()
				.codFiscalePazienteAttualmenteInVisita(paziente.getCodiceFiscale())
				.codiceDottore(paziente.getCodiceDottore()).build();

		LOGGER.info("....invocazione servizio esterno....");
		ResponseEntity<DottorePazienteDTO> response = webClient.post().uri("/terminaVisita")
				.accept(MediaType.APPLICATION_JSON).body(Mono.just(dottorePazienteDTO), DottoreDaAggiornareDTO.class)
				.retrieve().toEntity(DottorePazienteDTO.class).block();

		// ANDREBBE GESTITA CON ADVICE!!!
		if (response.getStatusCode() != HttpStatus.NO_CONTENT)
			throw new OperazioneNonAndataABuonFineException("Attenzione! L'operazione non è andata a buon fine");
		LOGGER.info("....invocazione servizio esterno terminata....");

		Paziente pazienteRicoverato = pazienteService.ricovera(paziente);
		return PazienteDTO.buildPazienteDTOFromModel(pazienteRicoverato);
	}
	
	@PostMapping("/dimetti/{id}")
	public PazienteDTO dimetti(@PathVariable(required = true, value = "id") Long id) {
		Paziente paziente = pazienteService.caricaSingoloElemento(id);
		if (paziente == null) {
			throw new PazienteNotFoundException("Paziente not found con id: " + id);
		}
//		

		DottorePazienteDTO dottorePazienteDTO = DottorePazienteDTO.builder()
				.codFiscalePazienteAttualmenteInVisita(paziente.getCodiceFiscale())
				.codiceDottore(paziente.getCodiceDottore()).build();

		LOGGER.info("....invocazione servizio esterno....");
		ResponseEntity<DottorePazienteDTO> response = webClient.post().uri("/terminaVisita")
				.accept(MediaType.APPLICATION_JSON).body(Mono.just(dottorePazienteDTO), DottoreDaAggiornareDTO.class)
				.retrieve().toEntity(DottorePazienteDTO.class).block();

		// ANDREBBE GESTITA CON ADVICE!!!
		if (response.getStatusCode() != HttpStatus.NO_CONTENT)
			throw new OperazioneNonAndataABuonFineException("Attenzione! L'operazione non è andata a buon fine");
		LOGGER.info("....invocazione servizio esterno terminata....");

		Paziente pazienteDimesso = pazienteService.dimetti(paziente);
		return PazienteDTO.buildPazienteDTOFromModel(pazienteDimesso);
	}
}
