package it.prova.triage_be.web.api.exception;

public class PazienteNonAncoraDimessoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public PazienteNonAncoraDimessoException(String message) {
		super(message);
	}
}
