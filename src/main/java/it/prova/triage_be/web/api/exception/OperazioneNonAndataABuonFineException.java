package it.prova.triage_be.web.api.exception;

public class OperazioneNonAndataABuonFineException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public OperazioneNonAndataABuonFineException(String message) {
		super(message);
	}
}
