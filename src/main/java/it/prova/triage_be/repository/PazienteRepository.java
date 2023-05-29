package it.prova.triage_be.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import it.prova.triage_be.model.Paziente;


public interface PazienteRepository extends PagingAndSortingRepository<Paziente,Long>, JpaSpecificationExecutor<Paziente>{

	@Query("from Paziente p where p.codiceFiscale like ?1")
	Paziente findByCf(String codiceFiscale);
}
