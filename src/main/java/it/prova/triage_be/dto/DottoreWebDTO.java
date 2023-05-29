package it.prova.triage_be.dto;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DottoreWebDTO {
	private Long id;

	@NotBlank(message = "Il campo Nome deve essere valorizzato")
	private String nome;
	@NotBlank(message = "Il campo Cognome deve essere valorizzato")
	private String cognome;
	@NotBlank(message = "Il campo Codice Dottore  deve essere valorizzato")
	private String codiceDottore;

	private String codFiscalePazienteAttualmenteInVisita;

	private Boolean inVisita;

	private Boolean inServizio;

	public DottoreWebDTO buildDottoreWebDTOFromDottoreDaAggiornare(DottoreDaAggiornareDTO dto) {
		DottoreWebDTO result = DottoreWebDTO.builder().nome(dto.getNome()).cognome(dto.getCognome())
				.codFiscalePazienteAttualmenteInVisita(dto.getCodFiscalePazienteAttualmenteInVisita())
				.inServizio(dto.getInServizio()).inVisita(dto.getInVisita()).build();
		return result;
	}

}
