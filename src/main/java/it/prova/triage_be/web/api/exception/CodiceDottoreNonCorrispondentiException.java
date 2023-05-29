package it.prova.triage_be.web.api.exception;

public class CodiceDottoreNonCorrispondentiException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CodiceDottoreNonCorrispondentiException(String message) {
		super(message);
	}
}
