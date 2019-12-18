package edu.uclm.esi.iso2.banco20193capas.exceptions;

public class ClienteNoAutorizadoException extends Exception {
	public ClienteNoAutorizadoException(String nif, Long id) {
		super("El cliente con NIF " + nif + " no está autorizado para operar en la cuenta " + id);
	}
}
