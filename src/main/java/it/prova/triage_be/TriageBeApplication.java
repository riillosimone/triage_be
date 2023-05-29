package it.prova.triage_be;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import it.prova.triage_be.model.Ruolo;
import it.prova.triage_be.model.Utente;
import it.prova.triage_be.service.ruolo.RuoloService;
import it.prova.triage_be.service.utente.UtenteService;


@SpringBootApplication
public class TriageBeApplication implements CommandLineRunner{

	@Autowired
	private RuoloService ruoloServiceInstance;
	@Autowired
	private UtenteService utenteServiceInstance;
	
	public static void main(String[] args) {
		SpringApplication.run(TriageBeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", Ruolo.ROLE_ADMIN) == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Administrator", Ruolo.ROLE_ADMIN));
		}

		
		
		if (ruoloServiceInstance.cercaPerDescrizioneECodice("Sub Operator", Ruolo. ROLE_SUB_OPERATOR) == null) {
			ruoloServiceInstance.inserisciNuovo(new Ruolo("Sub Operator", Ruolo. ROLE_SUB_OPERATOR));
		}

		// a differenza degli altri progetti cerco solo per username perche' se vado
		// anche per password ogni volta ne inserisce uno nuovo, inoltre l'encode della
		// password non lo
		// faccio qui perche gia lo fa il service di utente, durante inserisciNuovo
		if (utenteServiceInstance.findByUsername("admin") == null) {
			Utente admin = Utente.builder().username("admin").password("admin").nome("Giovanni").cognome("Cipolla").dataRegistrazione(LocalDate.now()).build();
			admin.getRuoli().add(ruoloServiceInstance.cercaPerDescrizioneECodice("Administrator", Ruolo.ROLE_ADMIN));
			utenteServiceInstance.inserisciNuovo(admin);
			// l'inserimento avviene come created ma io voglio attivarlo
			utenteServiceInstance.changeUserAbilitation(admin.getId());
		}

		if (utenteServiceInstance.findByUsername("user") == null) {
			Utente classicUser = Utente.builder().username("user").password("user").nome("Antonio").cognome("Verdi").dataRegistrazione(LocalDate.now()).build();
			classicUser.getRuoli()
					.add(ruoloServiceInstance.cercaPerDescrizioneECodice("Sub Operator", Ruolo. ROLE_SUB_OPERATOR));
			utenteServiceInstance.inserisciNuovo(classicUser);
			// l'inserimento avviene come created ma io voglio attivarlo
			utenteServiceInstance.changeUserAbilitation(classicUser.getId());
		}

		if (utenteServiceInstance.findByUsername("user1") == null) {
			Utente classicUser1 = Utente.builder().username("user1").password("user1").nome("Mario").cognome("Rossi").dataRegistrazione(LocalDate.now()).build();
			classicUser1.getRuoli()
					.add(ruoloServiceInstance.cercaPerDescrizioneECodice("Sub Operator", Ruolo. ROLE_SUB_OPERATOR));
			utenteServiceInstance.inserisciNuovo(classicUser1);
			// l'inserimento avviene come created ma io voglio attivarlo
			utenteServiceInstance.changeUserAbilitation(classicUser1.getId());
		}

		if (utenteServiceInstance.findByUsername("user2") == null) {
			Utente classicUser2 = Utente.builder().username("user2").password("user2").nome("Simone").cognome("Riillo").dataRegistrazione(LocalDate.now()).build();
			classicUser2.getRuoli()
					.add(ruoloServiceInstance.cercaPerDescrizioneECodice("Sub Operator", Ruolo. ROLE_SUB_OPERATOR));
			utenteServiceInstance.inserisciNuovo(classicUser2);
			// l'inserimento avviene come created ma io voglio attivarlo
			utenteServiceInstance.changeUserAbilitation(classicUser2.getId());
		}
	}

}
