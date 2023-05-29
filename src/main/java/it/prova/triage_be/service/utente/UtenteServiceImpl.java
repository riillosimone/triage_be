package it.prova.triage_be.service.utente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.triage_be.model.StatoUtente;
import it.prova.triage_be.model.Utente;
import it.prova.triage_be.repository.UtenteRepository;


@Service
@Transactional(readOnly = true)
public class UtenteServiceImpl implements UtenteService{
	
	@Autowired
	private UtenteRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	@Override
	public List<Utente> listAllUtenti() {

		return (List<Utente>) repository.findAll();
	}

	@Override
	public Utente caricaSingoloUtente(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	public Utente caricaSingoloUtenteConRuoli(Long id) {
		return repository.findByIdConRuoli(id).orElse(null);
	}

	@Override
	@Transactional
	public Utente aggiorna(Utente utenteInstance) {
		// deve aggiornare solo nome, cognome, username, ruoli
		Utente utenteReloaded = repository.findById(utenteInstance.getId()).orElse(null);
		if (utenteReloaded == null)
			throw new RuntimeException("Elemento non trovato");
		utenteReloaded.setNome(utenteInstance.getNome());
		utenteReloaded.setCognome(utenteInstance.getCognome());
		utenteReloaded.setUsername(utenteInstance.getUsername());
		utenteReloaded.setRuoli(utenteInstance.getRuoli());
		return repository.save(utenteReloaded);

	}

	@Override
	@Transactional
	public Utente inserisciNuovo(Utente utenteInstance) {
		utenteInstance.setStato(StatoUtente.CREATO);
		utenteInstance.setPassword(passwordEncoder.encode(utenteInstance.getPassword()));
		utenteInstance.setDataRegistrazione(LocalDate.now());
		return repository.save(utenteInstance);

	}

	@Override
	@Transactional
	public void rimuovi(Long idToRemove) {
		repository.deleteById(idToRemove);

	}

	public Page<Utente> findByExampleWithPagination(Utente example, Integer pageNo, Integer pageSize, String sortBy) {
		Specification<Utente> specificationCriteria = (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<Predicate>();

			if (StringUtils.isNotEmpty(example.getNome()))
				predicates.add(cb.like(cb.upper(root.get("nome")),
						"%" + example.getNome().toUpperCase() + "%"));
			if (StringUtils.isNotEmpty(example.getCognome()))
				predicates.add(cb.like(cb.upper(root.get("cognome")),
						"%" + example.getCognome().toUpperCase() + "%"));

			if (StringUtils.isNotEmpty(example.getUsername()))
				predicates.add(cb.like(cb.upper(root.get("username")),
						"%" + example.getUsername().toUpperCase() + "%"));
			
			if (example.getDataRegistrazione() != null)
				predicates.add(cb.greaterThanOrEqualTo(root.get("dataRegistrazione"), example.getDataRegistrazione()));



			return cb.and(predicates.toArray(new Predicate[predicates.size()]));
		};

		Pageable paging = null;
		// se non passo parametri di paginazione non ne tengo conto
		if (pageSize == null || pageSize < 10)
			paging = Pageable.unpaged();
		else
			paging = PageRequest.of(pageNo, pageSize, Sort.by(sortBy));

		return repository.findAll(specificationCriteria, paging);
	}

	@Override
	public Utente findByUsernameAndPassword(String username, String password) {
		return repository.findByUsernameAndPassword(username, password);
	}

	@Override
	public Utente eseguiAccesso(String username, String password) {
		return repository.findByUsernameAndPasswordAndStato(username, password, StatoUtente.ATTIVO);
	}

	@Override
	@Transactional
	public Utente changeUserAbilitation(Long utenteInstanceId) {
		Utente utenteInstance = caricaSingoloUtente(utenteInstanceId);
		if (utenteInstance == null)
			throw new RuntimeException("Elemento non trovato.");

		if (utenteInstance.getStato() == null || utenteInstance.getStato().equals(StatoUtente.CREATO))
			utenteInstance.setStato(StatoUtente.ATTIVO);
		else if (utenteInstance.getStato().equals(StatoUtente.ATTIVO))
			utenteInstance.setStato(StatoUtente.DISABILITATO);
		else if (utenteInstance.getStato().equals(StatoUtente.DISABILITATO))
			utenteInstance.setStato(StatoUtente.ATTIVO);

		return utenteInstance;
	}

	@Override
	public Utente findByUsername(String username) {
		return repository.findByUsername(username).orElse(null);
	}


}
