package it.prova.triage_be.service.utente;

import java.util.List;

import org.springframework.data.domain.Page;

import it.prova.triage_be.model.Utente;


public interface UtenteService {
	

	public List<Utente> listAllUtenti();

	public Utente caricaSingoloUtente(Long id);

	public Utente caricaSingoloUtenteConRuoli(Long id);

	public Utente aggiorna(Utente utenteInstance);

	public Utente inserisciNuovo(Utente utenteInstance);

	public void rimuovi(Long idToRemove);

	public Page<Utente> findByExampleWithPagination(Utente example, Integer pageNo, Integer pageSize, String sortBy);

	public Utente findByUsernameAndPassword(String username, String password);

	public Utente eseguiAccesso(String username, String password);

	public Utente changeUserAbilitation(Long utenteInstanceId);

	public Utente findByUsername(String username);

}
