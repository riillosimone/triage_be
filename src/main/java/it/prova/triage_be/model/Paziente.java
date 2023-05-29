package it.prova.triage_be.model;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "paziente")
public class Paziente {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "nome")
	private String nome;
	@Column(name = "cognome")
	private String cognome;
	@Column(name = "codicefiscale")
	private String codiceFiscale;
	@Column(name = "dataregistrazione")
	private LocalDate dataRegistrazione;

	@Enumerated(EnumType.STRING)
	private StatoPaziente stato;

	@Column(name = "codicedottore")
	private String codiceDottore;

}
