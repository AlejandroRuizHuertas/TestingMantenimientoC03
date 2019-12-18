package edu.uclm.esi.iso2.banco20193capas.exceptions;

public class ClienteNoEncontradoException extends Exception {
	public ClienteNoEncontradoException(String nif) {
		super("No se encuentra el cliente con NIF " + nif);
	}
}
