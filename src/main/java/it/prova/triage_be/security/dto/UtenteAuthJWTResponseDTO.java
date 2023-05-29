package it.prova.triage_be.security.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UtenteAuthJWTResponseDTO {
	
	private String token;
	@Builder.Default
	private String type = "Bearer";
	private String username;
	private String email;
	private List<String> roles;

}
