package it.prova.triage_be.web.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import it.prova.triage_be.dto.UtenteDTO;
import it.prova.triage_be.model.Utente;
import it.prova.triage_be.security.dto.UtenteInfoJWTResponseDTO;
import it.prova.triage_be.service.utente.UtenteService;
import it.prova.triage_be.web.api.exception.IdNotNullForInsertException;
import it.prova.triage_be.web.api.exception.UtenteNotFoundException;

@RestController
@RequestMapping("/api/utente")
public class UtenteController {

	@Autowired
	private UtenteService utenteService;

	// questa mi serve solo per capire se solo ADMIN vi ha accesso
	@GetMapping("/testSoloAdmin")
	public String test() {
		return "OK";
	}

	@GetMapping(value = "/userInfo")
	public ResponseEntity<UtenteInfoJWTResponseDTO> getUserInfo() {

		// se sono qui significa che sono autenticato quindi devo estrarre le info dal
		// contesto
		String username = SecurityContextHolder.getContext().getAuthentication().getName();

		// estraggo le info dal principal
		Utente utenteLoggato = utenteService.findByUsername(username);
		List<String> ruoli = utenteLoggato.getRuoli().stream().map(item -> item.getCodice())
				.collect(Collectors.toList());

		return ResponseEntity.ok(UtenteInfoJWTResponseDTO.builder().nome(utenteLoggato.getNome())
				.cognome(utenteLoggato.getCognome()).username(utenteLoggato.getUsername()).roles(ruoli).build());
	}

	@PostMapping("/inserisci")
	@ResponseStatus(HttpStatus.CREATED)
	public UtenteDTO insert(@Valid @RequestBody UtenteDTO utenteInput) {
		if (utenteInput.getId() != null) {
			throw new IdNotNullForInsertException("Non è ammesso fornire un id per la creazione");
		}
		Utente utenteInserito = utenteService.inserisciNuovo(utenteInput.buildUtenteModel(true));
		Utente utenteAbilitato = utenteService.changeUserAbilitation(utenteInserito.getId());
		return UtenteDTO.buildUtenteDTOFromModel(utenteAbilitato);
	}

	@GetMapping
	public List<UtenteDTO> getAll() {
		return UtenteDTO.createUtenteDTOListFromModelList(utenteService.listAllUtenti());
	}

	@GetMapping("/{id}")
	public UtenteDTO findUtente(@PathVariable(value = "id", required = true) Long id) {
		Utente utenteCaricato = utenteService.caricaSingoloUtente(id);
		if (utenteCaricato == null) {
			throw new UtenteNotFoundException("Utente not found con id: " + id);
		}
		return UtenteDTO.buildUtenteDTOFromModel(utenteCaricato);
	}

	@PutMapping("/aggiorna/{id}")
	public UtenteDTO update(@Valid @RequestBody UtenteDTO utenteInput,
			@PathVariable(value = "id", required = true) Long id) {
		Utente utenteCaricato = utenteService.caricaSingoloUtente(id);
		if (utenteCaricato == null) {
			throw new UtenteNotFoundException("Utente not found con id: " + id);
		}
		utenteInput.setId(id);
		Utente utenteAggiornato = utenteService.aggiorna(utenteInput.buildUtenteModel(false));
		Utente utenteAbilitato = utenteService.changeUserAbilitation(utenteAggiornato.getId());
		return UtenteDTO.buildUtenteDTOFromModel(utenteAbilitato);
	}

	@GetMapping("/disabilita/{id}")
	public UtenteDTO disabilita(@PathVariable(value = "id", required = true) Long id) {
		Utente utenteCaricato = utenteService.caricaSingoloUtente(id);
		if (utenteCaricato == null) {
			throw new UtenteNotFoundException("Utente not found con id: " + id);
		}
		Utente utenteAbilitato = utenteService.changeUserAbilitation(utenteCaricato.getId());
		return UtenteDTO.buildUtenteDTOFromModel(utenteAbilitato);
	}

	@PostMapping("/searchWithPagination")
	public ResponseEntity<Page<UtenteDTO>> searchPaginated(@RequestBody UtenteDTO example,
			@RequestParam(defaultValue = "0") Integer pageNo, @RequestParam(defaultValue = "0") Integer pageSize,
			@RequestParam(defaultValue = "id") String sortBy) {
		Page<Utente> entityPageResults = utenteService.findByExampleWithPagination(example.buildUtenteModel(true),
				pageNo, pageSize, sortBy);

		return new ResponseEntity<Page<UtenteDTO>>(UtenteDTO.fromModelPageToDTOPage(entityPageResults), HttpStatus.OK);
	}

}
