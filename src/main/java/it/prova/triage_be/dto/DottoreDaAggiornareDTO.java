package it.prova.triage_be.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DottoreDaAggiornareDTO {

	@NotBlank(message = "Il campo Nome deve essere valorizzato")
	private String nome;
	@NotBlank(message = "Il campo Cognome deve essere valorizzato")
	private String cognome;
	
	private String codiceDottore;
	
	private String codFiscalePazienteAttualmenteInVisita;

	private Boolean inVisita;

	private Boolean inServizio;

}
