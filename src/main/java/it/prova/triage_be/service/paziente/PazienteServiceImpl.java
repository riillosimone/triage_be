package it.prova.triage_be.service.paziente;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.prova.triage_be.model.Paziente;
import it.prova.triage_be.model.StatoPaziente;
import it.prova.triage_be.repository.PazienteRepository;
import it.prova.triage_be.web.api.exception.PazienteNonAncoraDimessoException;
import it.prova.triage_be.web.api.exception.PazienteNotFoundException;

@Service
@Transactional(readOnly = true)
public class PazienteServiceImpl implements PazienteService {

	@Autowired
	private PazienteRepository repository;

	@Override
	public List<Paziente> listAllPazienti() {
		return (List<Paziente>) repository.findAll();
	}

	@Override
	public Paziente caricaSingoloElemento(Long id) {
		return repository.findById(id).orElse(null);
	}

	@Override
	@Transactional
	public Paziente aggiorna(Paziente pazienteInstance) {
		Paziente paziente = repository.findById(pazienteInstance.getId()).orElse(null);
		if(paziente.getDataRegistrazione() != pazienteInstance.getDataRegistrazione())
		pazienteInstance.setDataRegistrazione(null);
		if (pazienteInstance.getNome() == null)
		pazienteInstance.setNome(paziente.getNome());

		if (pazienteInstance.getCognome() == null)
		pazienteInstance.setCognome(paziente.getCognome());
		if (pazienteInstance.getCodiceDottore() == null)
		pazienteInstance.setCodiceDottore(paziente.getCodiceDottore());
		if (pazienteInstance.getCodiceFiscale() == null)
		pazienteInstance.setCodiceFiscale(paziente.getCodiceFiscale());
		if(pazienteInstance.getStato() == null)
		pazienteInstance.setStato(StatoPaziente.IN_ATTESA_VISITA);

		return repository.save(pazienteInstance);
	}

	@Override
	@Transactional
	public Paziente inserisciNuovo(Paziente pazienteInstance) {

		if (pazienteInstance.getStato() == null) {
			pazienteInstance.setStato(StatoPaziente.IN_ATTESA_VISITA);
		}
		pazienteInstance.setDataRegistrazione(LocalDate.now());
		return repository.save(pazienteInstance);
	}

	@Override
	@Transactional
	public void rimuovi(Long idToRemove) {
		Paziente pazienteReloaded = repository.findById(idToRemove).orElse(null);
		if (pazienteReloaded == null) {
			throw new PazienteNotFoundException("Paziente not found con id: " + idToRemove);
		}
		if (pazienteReloaded.getStato() != StatoPaziente.DIMESSO) {
			throw new PazienteNonAncoraDimessoException(
					"Attenzione! Il paziente che stai cercando di rimuovere non è stato ancora dimesso.");
		}
		repository.deleteById(idToRemove);
	}

	@Override
	public Page<Paziente> findByExampleWithPagination(Paziente example, Integer pageNo, Integer pageSize,
			String sortBy) {
		Specification<Paziente> specificationCriteria = (root, query, cb) -> {

			List<Predicate> predicates = new ArrayList<Predicate>();

			if (StringUtils.isNotEmpty(example.getNome()))
				predicates.add(cb.like(cb.upper(root.get("nome")), "%" + example.getNome().toUpperCase() + "%"));

			if (StringUtils.isNotEmpty(example.getCognome()))
				predicates.add(cb.like(cb.upper(root.get("cognome")), "%" + example.getCognome().toUpperCase() + "%"));

			if (StringUtils.isNotEmpty(example.getCodiceFiscale()))
				predicates.add(cb.like(cb.upper(root.get("codiceFiscale")),
						"%" + example.getCodiceFiscale().toUpperCase() + "%"));

			if (example.getDataRegistrazione() != null)
				predicates.add(cb.greaterThanOrEqualTo(root.get("dataRegistrazione"), example.getDataRegistrazione()));

			if (StringUtils.isNotEmpty(example.getCodiceDottore()))
				predicates.add(cb.like(cb.upper(root.get("codiceDottore")),
						"%" + example.getCodiceDottore().toUpperCase() + "%"));

			if (example.getStato() != null)
				predicates.add(cb.equal(root.get("stato"), example.getStato()));

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
	public Paziente findByCf(String codiceFiscale) {
		return repository.findByCf(codiceFiscale);
	}
	
	

	@Override
	@Transactional
	public Paziente ricovera(Paziente pazienteInput) {
		Paziente result = this.caricaSingoloElemento(pazienteInput.getId());
		if (result == null) {
			throw new PazienteNotFoundException("Paziente not found con id: "+pazienteInput.getId());
		}
		
		result.setStato(StatoPaziente.RICOVERATO);
		result.setCodiceDottore(null);
		return repository.save(result);
	}

	@Override
	@Transactional
	public Paziente dimetti(Paziente pazienteInput) {
		Paziente result = this.caricaSingoloElemento(pazienteInput.getId());
		if (result == null) {
			throw new PazienteNotFoundException("Paziente not found con id: "+pazienteInput.getId());
		}
		
		result.setStato(StatoPaziente.DIMESSO);
		result.setCodiceDottore(null);
		return repository.save(result);
	}

	@Override
	@Transactional
	public Paziente impostaInVisita(Paziente pazienteInput) {
		Paziente result = this.caricaSingoloElemento(pazienteInput.getId());
		result.setStato(StatoPaziente.IN_VISITA);
		result.setCodiceDottore(pazienteInput.getCodiceDottore());
		return repository.save(result);
	}

}
