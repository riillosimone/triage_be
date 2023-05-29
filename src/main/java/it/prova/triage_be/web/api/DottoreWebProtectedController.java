package it.prova.triage_be.web.api;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import it.prova.triage_be.dto.DottoreDaAggiornareDTO;
import it.prova.triage_be.dto.DottoreWebDTO;
import it.prova.triage_be.web.api.exception.OperazioneNonAndataABuonFineException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/protected/dottore")
public class DottoreWebProtectedController {

	@Autowired
	private WebClient webClient;
	
	private static final Logger LOGGER = LogManager.getLogger(DottoreWebController.class);
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public DottoreWebDTO createNew(@Valid @RequestBody DottoreWebDTO dottoreInput) {
		LOGGER.info("....invocazione servizio esterno....");

		ResponseEntity<DottoreWebDTO> response = webClient.post().accept(MediaType.APPLICATION_JSON)
				.body(Mono.just(dottoreInput), DottoreWebDTO.class).retrieve().toEntity(DottoreWebDTO.class).block();

		// ANDREBBE GESTITA CON ADVICE!!!
		if (response.getStatusCode() != HttpStatus.CREATED)
			throw new OperazioneNonAndataABuonFineException("Attenzione! L'operazione non è andata a buon fine");

		LOGGER.info("....invocazione servizio esterno terminata....");
		return dottoreInput;
	}

//	
	@PutMapping("/{codiceDottore}")
	public void aggiorna(@Valid @RequestBody DottoreDaAggiornareDTO dottoreInput,
			@PathVariable(required = true, value = "codiceDottore") String codiceDottore) {

		dottoreInput.setCodiceDottore(codiceDottore);
		LOGGER.info("....invocazione servizio esterno....");
		ResponseEntity<DottoreDaAggiornareDTO> response = webClient.put().uri("/aggiorna/" + codiceDottore)
				.accept(MediaType.APPLICATION_JSON).body(Mono.just(dottoreInput), DottoreDaAggiornareDTO.class)
				.retrieve().toEntity(DottoreDaAggiornareDTO.class).block();

		// ANDREBBE GESTITA CON ADVICE!!!
		if (response.getStatusCode() != HttpStatus.NO_CONTENT)
			throw new OperazioneNonAndataABuonFineException("Attenzione! L'operazione non è andata a buon fine");
		LOGGER.info("....invocazione servizio esterno terminata....");
	}

	@DeleteMapping("/{codiceDottore}")
	public ResponseEntity<HttpStatus> delete(@PathVariable(required = true) String codiceDottore) {
		LOGGER.info("....invocazione servizio esterno....");
		ResponseEntity<HttpStatus> response = webClient.delete().uri("/elimina/" + codiceDottore).retrieve()
				.toEntity(HttpStatus.class).block();

		
		if (response.getStatusCode() == HttpStatus.UNPROCESSABLE_ENTITY) {
			throw new OperazioneNonAndataABuonFineException(
					"Attenzione! Al momento il dottore che stai cercando di rimuovere è occupato. Riprova più tardi.");
		}
		if (response.getStatusCode() != HttpStatus.NO_CONTENT) {
			throw new OperazioneNonAndataABuonFineException("Attenzione! L'operazione non è andata a buon fine");
		}
		LOGGER.info("....invocazione servizio esterno terminata....");

		return response;
	}
	
	
	
}
