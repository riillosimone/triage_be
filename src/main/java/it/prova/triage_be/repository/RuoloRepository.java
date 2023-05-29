package it.prova.triage_be.repository;

import org.springframework.data.repository.CrudRepository;

import it.prova.triage_be.model.Ruolo;


public interface RuoloRepository extends CrudRepository<Ruolo, Long>{
	Ruolo findByDescrizioneAndCodice(String descrizione, String codice);
}
