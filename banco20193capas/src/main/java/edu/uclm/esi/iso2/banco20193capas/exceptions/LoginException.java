package edu.uclm.esi.iso2.banco20193capas.exceptions;

public class LoginException extends Exception {
	public LoginException() {
		super("Credenciales inválidas");
	}
}
