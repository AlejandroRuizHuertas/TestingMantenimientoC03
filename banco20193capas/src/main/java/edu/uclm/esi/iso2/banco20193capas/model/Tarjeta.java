package edu.uclm.esi.iso2.banco20193capas.model;

import java.security.SecureRandom;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import edu.uclm.esi.iso2.banco20193capas.exceptions.ImporteInvalidoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.PinInvalidoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.SaldoInsuficienteException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.TarjetaBloqueadaException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.TokenInvalidoException;

/**
 * Representa una tarjeta bancaria, bien de débito o bien de crédito.
 * Una {@code Tarjeta} está asociada a un {@code Cliente} y a una {@Cuenta}.
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Tarjeta {
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	protected Long id;
	
	protected Integer pin;
	protected Boolean activa;
	protected Integer intentos;

	@Transient
	protected Compra compra;
	
	@ManyToOne
	protected Cliente titular;
	
	@ManyToOne
	protected Cuenta cuenta;
	
	public Tarjeta() {
		activa = true;
		this.intentos = 0;
		SecureRandom dado = new SecureRandom();
		pin = 0;
		for (int i=0; i<=3; i++)
			pin = (int) (pin + dado.nextInt(10) * Math.pow(10, i));	
	}
	
	protected void comprobar(int pin) throws TarjetaBloqueadaException, PinInvalidoException {
		if (!this.isActiva())
			throw new TarjetaBloqueadaException();
		if (this.pin!=pin) {
			this.intentos++;
			if (intentos == 3)
				bloquear();
			throw new PinInvalidoException();
		}		
	}
	
	/**
	 * Permite confirmar una compra que se ha iniciado por Internet. El método {@link #comprarPorInternet(int, double)} devuelve un token que debe ser introducido en este método.
	 * @param token	El token que introduce el usuario. Para que la compra se confirme, ha de coincidir con el token devuelto por {@link #comprarPorInternet(int, double)}
	 * @throws TokenInvalidoException	Si el {@code token} introducido es distinto del recibido desde {@link #comprarPorInternet(int, double)}
	 * @throws ImporteInvalidoException	 Si el importe<=0
	 * @throws SaldoInsuficienteException	Si el saldo de la cuenta asociada a la tarjeta (en el caso de {@link TarjetaDebito}) es menor que el importe, o 
	 * 									si el crédito disponible en la tarjeta de crédito es menor que el importe	
	 * @throws TarjetaBloqueadaException	Si la tarjeta está bloqueada
	 * @throws PinInvalidoException	Si el pin que se introdujo es inválido
	 */
	public void confirmarCompraPorInternet(int token) throws TokenInvalidoException, ImporteInvalidoException, SaldoInsuficienteException, TarjetaBloqueadaException, PinInvalidoException {
		if (token!=this.compra.getToken()) {
			this.compra = null;
			throw new TokenInvalidoException();
		}
		this.comprar(this.pin, this.compra.getImporte());
	}

	protected abstract void bloquear();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPin() {
		return pin;
	}

	public void setPin(Integer pin) {
		this.pin = pin;
	}

	public Cliente getTitular() {
		return titular;
	}

	public void setTitular(Cliente titular) {
		this.titular = titular;
	}

	public Cuenta getCuenta() {
		return cuenta;
	}

	public void setCuenta(Cuenta cuenta) {
		this.cuenta = cuenta;
	}

	/**
	 * 
	 * @return true si la tarjeta está activa; false si está bloqueada
	 */
	public Boolean isActiva() {
		return activa;
	}

	public void setActiva(Boolean activa) {
		this.activa = activa;
	}

	public abstract void sacarDinero(int pin, double importe) throws ImporteInvalidoException, SaldoInsuficienteException, TarjetaBloqueadaException, PinInvalidoException;
	
	public abstract Integer comprarPorInternet(int pin, double importe) throws TarjetaBloqueadaException, PinInvalidoException, SaldoInsuficienteException, ImporteInvalidoException;

	public abstract void comprar(int pin, double importe) throws ImporteInvalidoException, SaldoInsuficienteException, TarjetaBloqueadaException, PinInvalidoException;

	/**
	 * Permite cambiar el pin de la tarjeta
	 * @param pinViejo	El pin actual
	 * @param pinNuevo	El pin nuevo (el que desea establecer el usuario)
	 * @throws PinInvalidoException	Si el {@code pinViejo} es incorrecto
	 */
	public abstract void cambiarPin(int pinViejo, int pinNuevo) throws PinInvalidoException;
}
