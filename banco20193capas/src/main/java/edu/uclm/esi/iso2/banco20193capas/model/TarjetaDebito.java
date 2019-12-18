package edu.uclm.esi.iso2.banco20193capas.model;

import java.security.SecureRandom;

import javax.persistence.Entity;

import edu.uclm.esi.iso2.banco20193capas.exceptions.ImporteInvalidoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.PinInvalidoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.SaldoInsuficienteException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.TarjetaBloqueadaException;

@Entity
public class TarjetaDebito extends Tarjeta {

	/**
	 * Permite sacar dinero del cajero automático
	 * @param pin	El pin que introduce el usuario
	 * @param importe	El {@code importe} que desea sacar
	 * @throws ImporteInvalidoException	Si el {@code importe<=0}
	 * @throws SaldoInsuficienteException	Si el saldo de la cuenta asociada ({@link edu.uclm.esi.iso2.banco20193capas.model.Cuenta#getSaldo() Cuenta.getSaldo()}) a la tarjeta es menor que el importe
	 * @throws TarjetaBloqueadaException	Si la tarjeta está bloqueada
	 * @throws PinInvalidoException	Si el pin introducido es distinto del pin de la tarjeta
	 */
	@Override
	public void sacarDinero(int pin, double importe) throws ImporteInvalidoException, SaldoInsuficienteException, TarjetaBloqueadaException, PinInvalidoException {
		comprobar(pin);
		this.intentos = 0;
		this.cuenta.retirar(importe);
	}

	/**
	 * Inicia una compra por Internet, que debe confirmarse después (ver {@link #confirmarCompraPorInternet(int)}) mediante el token que devuelve este método
	 * @param pin	El pin que introduce el usuario
	 * @param importe	El importe de la compra
	 * @return	Un token que debe introducirse en {@link #confirmarCompraPorInternet(int)}
	 * @throws TarjetaBloqueadaException	Si la tarjeta está bloqueada
	 * @throws PinInvalidoException	Si el pin introducido es distinto del pin de la tarjeta
	 * @throws SaldoInsuficienteException	Si el saldo de la cuenta asociada a la tarjeta es menor que el importe 
	 * @throws ImporteInvalidoException	Si el importe<=0
	 */
	@Override
	public Integer comprarPorInternet(int pin, double importe) throws TarjetaBloqueadaException, PinInvalidoException, SaldoInsuficienteException, ImporteInvalidoException {
		comprobar(pin);
		this.intentos = 0;
		SecureRandom dado = new SecureRandom();
		int token = 0;
		for (int i=0; i<=3; i++)
			token = (int) (token + dado.nextInt(10) * Math.pow(10, i));
		token =  1234;
		this.compra = new Compra(importe, token);
		return token;
	}
	
	/**
	 * Permite hacer un compra en un comercio
	 * @param pin	El pin que introduce el usuario
	 * @param importe	El importe de la compra
	 * @throws ImporteInvalidoException	Si el importe<=0
	 * @throws SaldoInsuficienteException	Si el saldo de la cuenta asociada ({@link Cuenta#getSaldo()}) a la tarjeta es menor que el importe 
	 * @throws TarjetaBloqueadaException	Si la tarjeta está bloqueada
	 * @throws PinInvalidoException	Si el pin introducido es incorrecto
	 */
	@Override
	public void comprar(int pin, double importe) throws ImporteInvalidoException, SaldoInsuficienteException, TarjetaBloqueadaException, PinInvalidoException {
		comprobar(pin);
		this.intentos = 0;
		this.cuenta.retirar(importe);
	}

	@Override
	protected void bloquear() {
		this.activa = false;
		Manager.getTarjetaDebitoDAO().save(this);
	}
	
	@Override
	public void cambiarPin(int pinViejo, int pinNuevo) throws PinInvalidoException {
		if (this.pin!=pinViejo)
			throw new PinInvalidoException();
		this.pin = pinNuevo;
		Manager.getTarjetaDebitoDAO().save(this);
	}
}
