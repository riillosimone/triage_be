package it.prova.triage_be.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtenteAuthDTO {
	
	private String username;
	private String password;

}
