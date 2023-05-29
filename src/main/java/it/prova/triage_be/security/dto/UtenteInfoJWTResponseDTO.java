package it.prova.triage_be.security.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UtenteInfoJWTResponseDTO {
	
	private String nome;
	
	private String cognome;
	
	@Builder.Default
	@JsonIgnore(value = true)
	private String type = "Bearer";
	
	private String username;
	
	private List<String> roles;

}
