package it.prova.triage_be.web.api;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import it.prova.triage_be.dto.DottoreWebDTO;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/dottore")
public class DottoreWebController {

	@Autowired
	private WebClient webClient;

	private static final Logger LOGGER = LogManager.getLogger(DottoreWebController.class);

	@GetMapping("/{codiceDottore}")
	public DottoreWebDTO findById(@PathVariable(required = true, value = "codiceDottore") String codiceDottore) {
		LOGGER.info("....invocazione servizio esterno....con CF: " + codiceDottore);
		DottoreWebDTO dottoreDTO = webClient.get().uri("/" + codiceDottore).retrieve().bodyToMono(DottoreWebDTO.class)
				.block();
		LOGGER.info("....invocazione servizio esterno terminata....");
		return dottoreDTO;
	}

	@GetMapping
	public List<DottoreWebDTO> findAll() {
		LOGGER.info("....invocazione servizio esterno....");

		Mono<List<DottoreWebDTO>> response = webClient.get().accept(MediaType.APPLICATION_JSON).retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<DottoreWebDTO>>() {
				});

		List<DottoreWebDTO> listaDottori = response.block();
		LOGGER.info("....invocazione servizio esterno terminata....");
		return listaDottori;
	}

	

}
