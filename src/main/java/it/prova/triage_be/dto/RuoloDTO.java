package it.prova.triage_be.dto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import it.prova.triage_be.model.Ruolo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuoloDTO {
	
	private Long id;
	private String descrizione;
	private String codice;
	
	
	
	public static RuoloDTO buildRuoloDTOFromModel(Ruolo ruoloModel) {
		RuoloDTO result = RuoloDTO.builder().id(ruoloModel.getId()).codice(ruoloModel.getCodice()).descrizione(ruoloModel.getDescrizione()).build();
		return result;
	}
	
	public static List<RuoloDTO> createRuoloDTOListFromModelSet(Set<Ruolo> modelListInput) {
		return modelListInput.stream().map(ruoloEntity -> {
			return RuoloDTO.buildRuoloDTOFromModel(ruoloEntity);
		}).collect(Collectors.toList());
	}
	
	public static List<RuoloDTO> createRuoloDTOListFromModelList(List<Ruolo> modelListInput) {
		return modelListInput.stream().map(ruoloEntity -> {
			return RuoloDTO.buildRuoloDTOFromModel(ruoloEntity);
		}).collect(Collectors.toList());
	}

}
