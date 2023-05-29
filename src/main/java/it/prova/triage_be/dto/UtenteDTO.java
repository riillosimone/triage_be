package it.prova.triage_be.dto;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import it.prova.triage_be.model.Ruolo;
import it.prova.triage_be.model.StatoUtente;
import it.prova.triage_be.model.Utente;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UtenteDTO {

	private Long id;

	@NotBlank(message = "{username.notblank}")
	@Size(min = 3, max = 15, message = "Il valore inserito '${validatedValue}' deve essere lungo tra {min} e {max} caratteri")
	private String username;

	@NotBlank(message = "{password.notblank}")
	@Size(min = 8, max = 15, message = "Il valore inserito deve essere lungo tra {min} e {max} caratteri")
	private String password;

	@JsonIgnore(value = true)
	private String confermaPassword;

	@NotBlank(message = "{nome.notblank}")
	private String nome;

	@NotBlank(message = "{cognome.notblank}")
	private String cognome;

	private LocalDate dataRegistrazione;


	private StatoUtente stato;

	@JsonIgnore(value = true)
	private Long[] ruoliIds;

	public Utente buildUtenteModel(boolean includeRoles) {
		Utente result = Utente.builder().id(this.id).username(this.username).password(this.password).nome(this.nome)
				.cognome(this.cognome).dataRegistrazione(this.dataRegistrazione).stato(this.stato).build();
		if (includeRoles && ruoliIds != null) {
			result.setRuoli(Arrays.asList(ruoliIds).stream().map(id -> new Ruolo(id)).collect(Collectors.toSet()));
		}
		return result;
	}

	public static UtenteDTO buildUtenteDTOFromModel(Utente utenteModel) {
		UtenteDTO result = UtenteDTO.builder().id(utenteModel.getId()).username(utenteModel.getUsername())
				.nome(utenteModel.getNome()).cognome(utenteModel.getCognome())
				.dataRegistrazione(utenteModel.getDataRegistrazione()).stato(utenteModel.getStato()).build();
		
		if (!utenteModel.getRuoli().isEmpty()) {
			result.ruoliIds = utenteModel.getRuoli().stream().map(r -> r.getId()).collect(Collectors.toList()).toArray(new Long [] {});
		}
		return result;
	}
	
	public static List<UtenteDTO> createUtenteDTOListFromModelList(List<Utente> modelList) {
		return modelList.stream().map(entity -> UtenteDTO.buildUtenteDTOFromModel(entity)).collect(Collectors.toList());
	}
	
	public static Page<UtenteDTO> fromModelPageToDTOPage(Page<Utente> input) {
		return new PageImpl<>(createUtenteDTOListFromModelList(input.getContent()),
				PageRequest.of(input.getPageable().getPageNumber(), input.getPageable().getPageSize(),
						input.getPageable().getSort()),
				input.getTotalElements());
	}

}
