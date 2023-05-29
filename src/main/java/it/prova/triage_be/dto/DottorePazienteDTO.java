package it.prova.triage_be.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DottorePazienteDTO {
	
	@NotBlank(message = "{codiceDottore.notblank}")
	private String codiceDottore;
	@NotBlank(message = "{codiceCfPaziente.notblank}")
	private String codFiscalePazienteAttualmenteInVisita;
	
	

	

}
