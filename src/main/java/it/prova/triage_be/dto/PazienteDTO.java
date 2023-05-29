package it.prova.triage_be.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.annotation.JsonInclude;

import it.prova.triage_be.model.Paziente;
import it.prova.triage_be.model.StatoPaziente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PazienteDTO {

	private Long id;

	@NotBlank(message = "{nome.notblank}")
	private String nome;

	@NotBlank(message = "{cognome.notblank}")
	private String cognome;
	@NotBlank(message = "{codiceFiscale.notblank}")
	private String codiceFiscale;

	private LocalDate dataRegistrazione;

	private StatoPaziente stato;

	private String codiceDottore;

	public Paziente buildPazienteModel() {
		Paziente result = Paziente.builder().id(this.id).nome(this.nome).cognome(this.cognome)
				.codiceFiscale(this.codiceFiscale).dataRegistrazione(this.dataRegistrazione).stato(this.stato)
				.codiceDottore(this.codiceDottore).build();
		return result;
	}

	public static PazienteDTO buildPazienteDTOFromModel(Paziente pazienteModel) {
		PazienteDTO result = PazienteDTO.builder().id(pazienteModel.getId()).nome(pazienteModel.getNome())
				.cognome(pazienteModel.getCognome()).codiceFiscale(pazienteModel.getCodiceFiscale())
				.dataRegistrazione(pazienteModel.getDataRegistrazione()).stato(pazienteModel.getStato())
				.codiceDottore(pazienteModel.getCodiceDottore()).build();
		return result;
	}

	public static List<PazienteDTO> createPazienteDTOListFromModelList(List<Paziente> modelList) {
		return modelList.stream().map(entity -> PazienteDTO.buildPazienteDTOFromModel(entity))
				.collect(Collectors.toList());
	}

	public static Page<PazienteDTO> fromModelPageToDTOPage(Page<Paziente> input) {
		return new PageImpl<>(createPazienteDTOListFromModelList(input.getContent()),
				PageRequest.of(input.getPageable().getPageNumber(), input.getPageable().getPageSize(),
						input.getPageable().getSort()),
				input.getTotalElements());
	}
}
