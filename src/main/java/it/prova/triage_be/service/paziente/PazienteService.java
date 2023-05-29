package it.prova.triage_be.service.paziente;

import java.util.List;

import org.springframework.data.domain.Page;

import it.prova.triage_be.model.Paziente;


public interface PazienteService {
	public List<Paziente> listAllPazienti();

	public Paziente caricaSingoloElemento(Long id);

	public Paziente aggiorna(Paziente utenteInstance);

	public Paziente inserisciNuovo(Paziente utenteInstance);

	public void rimuovi(Long idToRemove);

	public Page<Paziente> findByExampleWithPagination(Paziente example, Integer pageNo, Integer pageSize, String sortBy);
	
	public Paziente findByCf(String codiceFiscale);
	
	public Paziente ricovera(Paziente pazienteInput);
	
	public Paziente dimetti(Paziente pazienteInput);
	
	public Paziente impostaInVisita(Paziente pazienteInput);

}
