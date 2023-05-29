package it.prova.triage_be.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "ruolo")
public class Ruolo {
	
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String  ROLE_SUB_OPERATOR = " ROLE_SUB_OPERATOR";
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "descrizione")
	private String descrizione;
	@Column(name = "codice")
	private String codice;
	public static String getRoleAdmin() {
		return ROLE_ADMIN;
	}
	public static String getRoleSubOperator() {
		return  ROLE_SUB_OPERATOR;
	}
	public Ruolo(Long id) {
		super();
		this.id = id;
	}
	public Ruolo(String descrizione, String codice) {
		super();
		this.descrizione = descrizione;
		this.codice = codice;
	}
	


}
