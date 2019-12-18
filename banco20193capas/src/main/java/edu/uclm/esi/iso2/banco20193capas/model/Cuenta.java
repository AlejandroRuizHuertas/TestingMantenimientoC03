package edu.uclm.esi.iso2.banco20193capas.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import edu.uclm.esi.iso2.banco20193capas.exceptions.ClienteNoAutorizadoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.ClienteNoEncontradoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.CuentaInvalidaException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.CuentaSinTitularesException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.CuentaYaCreadaException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.ImporteInvalidoException;
import edu.uclm.esi.iso2.banco20193capas.exceptions.SaldoInsuficienteException;

/**
* La clase {@code Cuenta} representa una cuenta bancaria, que ha de tener al menos un {@see Cliente} que sea titular.
* 
* 
*/
@Entity
public class Cuenta {
	@Id 
	protected Long id;

	@ManyToMany(fetch = FetchType.EAGER)
	private List<Cliente> titulares;
	
	private boolean creada;
		
	public Cuenta() {
		this.titulares=new ArrayList<>();
	}
	
	public Cuenta(Long id) {
		this();
		this.id=id;
	}
	
	public Cuenta(Integer id) {
		this(new Long(id));
	}
	
	/**
	 * Añade un cliente a la lista de titulares de esta cuenta
	 * @param cliente El cliente que se añade a la lista de titulares
	 * @throws CuentaYaCreadaException Si la cuenta ya se ha almacenado en la base de datos
	 */
	public void addTitular(Cliente cliente) throws CuentaYaCreadaException {
		if (creada)
			throw new CuentaYaCreadaException();
		this.titulares.add(cliente);
	}
	
	/**
	 * Realiza un ingreso en la cuenta
	 * @param importe	El importe que se ingresa
	 * @throws ImporteInvalidoException	Si el importe es <=0
	 */
	public void ingresar(double importe) throws ImporteInvalidoException {
		this.ingresar(importe, "Ingreso de efectivo");
	}

	private void ingresar(double importe, String concepto) throws ImporteInvalidoException {
		if (importe<=0)
			throw new ImporteInvalidoException(importe);
		MovimientoCuenta movimiento = new MovimientoCuenta(this, importe, concepto);
		Manager.getMovimientoDAO().save(movimiento);
	}
	
	/**
	 * Realiza una retirada de la cuenta
	 * @param importe	El importe que se retira
	 * @throws ImporteInvalidoException	Si el importe es <=0
	 * @throws SaldoInsuficienteException	Si el importe>getSaldo()
	 */
	public void retirar(double importe) throws ImporteInvalidoException, SaldoInsuficienteException {
		this.retirar(importe, "Retirada de efectivo");
	}
		
	private void retirar(double importe, String concepto) throws ImporteInvalidoException, SaldoInsuficienteException {
		if (importe<=0)
			throw new ImporteInvalidoException(importe);
		if (importe>getSaldo())
			throw new SaldoInsuficienteException();
		MovimientoCuenta movimiento = new MovimientoCuenta(this, -importe, concepto);
		Manager.getMovimientoDAO().save(movimiento);
	}

	/**
	 * Retira el importe de la cuenta, incluso aunque esta no tenga saldo suficiente
	 * @param importe	El importe que se retira
	 * @param concepto	El concepto del movimiento
	 */
	public void retiroForzoso(double importe, String concepto) {
		MovimientoCuenta movimiento = new MovimientoCuenta(this, -importe, concepto);
		Manager.getMovimientoDAO().save(movimiento);
	}
	
	/**
	 * Realiza una transferencia desde esta cuenta a la cuenta que se pasa como primer parámetro.
	 * Se cobra una comisión del 1%, con un mínimo de 1.5 euros
	 * @param numeroCuentaDestino	El id de la cuenta destino
	 * @param importe				El importe que se transfiere
	 * @param concepto				El concepto de la transferencia
	 * @throws CuentaInvalidaException	Si la cuenta destino es esta misma o no existe en la BD
	 * @throws ImporteInvalidoException	Si el importe es <=0
	 * @throws SaldoInsuficienteException	Si la cuenta no tiene saldo suficiente para afrontar el importe y la comisión
	 */
	public void transferir(Long numeroCuentaDestino, double importe, String concepto) throws CuentaInvalidaException, ImporteInvalidoException, SaldoInsuficienteException {
		if (this.getId().equals(numeroCuentaDestino))
			throw new CuentaInvalidaException(numeroCuentaDestino);
		this.retirar(importe, "Transferencia emitida");
		double comision = Math.max(0.01*importe, 1.5);
		this.retirar(comision, "Comisión por transferencia");
		Cuenta destino = this.load(numeroCuentaDestino);
		destino.ingresar(importe, "Transferencia recibida");
	}
	
	private Cuenta load(Long numero) throws CuentaInvalidaException {
		Optional<Cuenta> optCuenta = Manager.getCuentaDAO().findById(numero);
		if (!optCuenta.isPresent())
			throw new CuentaInvalidaException(numero);
		return optCuenta.get();
	}

	/**
	 * Devuelve el saldo de la cuenta
	 * @return	El saldo de la cuenta
	 */
	public double getSaldo() {
		List<MovimientoCuenta> mm = Manager.getMovimientoDAO().findByCuentaId(this.id);
		double saldo = 0.0;
		for (MovimientoCuenta m : mm)
			saldo = saldo + m.getImporte();
		return saldo;
	}

	/**
	 * Inserta la cuenta en la base de datos
	 * @throws CuentaSinTitularesException	Si no se ha asignado ningún titular a la cuenta
	 */
	public void insert() throws CuentaSinTitularesException {
		if (this.titulares.isEmpty())
			throw new CuentaSinTitularesException();
		this.creada = true;
		Manager.getCuentaDAO().save(this);
	}

	/**
	 * Emite una tarjeta de débito asociada a esta cuenta
	 * @param nif	NIF del cliente para el que se emite la tarjeta
	 * @return	La tarjeta de débito (@see {@link edu.uclm.esi.iso2.banco20193capas.model.TarjetaDebito})
	 * @throws ClienteNoEncontradoException	Si el cliente no está en la base de datos
	 * @throws ClienteNoAutorizadoException	Si el cliente no es titular de esta cuenta
	 */
	public TarjetaDebito emitirTarjetaDebito(String nif) throws ClienteNoEncontradoException, ClienteNoAutorizadoException {
		Optional<Cliente> optCliente = Manager.getClienteDAO().findByNif(nif);
		if (!optCliente.isPresent())
			throw new ClienteNoEncontradoException(nif);
		Cliente cliente = optCliente.get();
		boolean encontrado = false;
		for (Cliente titular : this.titulares)
			if (titular.getNif().equals(cliente.nif)) {
				encontrado = true;
				break;
			}

		if (!encontrado)
			throw new ClienteNoAutorizadoException(nif, this.id);
		TarjetaDebito tarjeta = new TarjetaDebito();
		tarjeta.setCuenta(this);
		tarjeta.setTitular(cliente);
		Manager.getTarjetaDebitoDAO().save(tarjeta);
		return tarjeta;
	}

	/**
	 * Emite una tarjeta de débito asociada a esta cuenta
	 * @param nif	El nif del cliente para el cual se emite esta tarjeta
	 * @param credito	El crédito concedido
	 * @return	La tarjeta de crédito (@see {@link edu.uclm.esi.iso2.banco20193capas.model.TarjetaCredito})
	 * @throws ClienteNoEncontradoException	Si el cliente no está en la base de datos
	 * @throws ClienteNoAutorizadoException	Si el cliente no es titular de esta cuenta
	 */
	public TarjetaCredito emitirTarjetaCredito(String nif, double credito) throws ClienteNoEncontradoException, ClienteNoAutorizadoException {
		Optional<Cliente> optCliente = Manager.getClienteDAO().findByNif(nif);
		if (!optCliente.isPresent())
			throw new ClienteNoEncontradoException(nif);
		Cliente cliente = optCliente.get();
		boolean encontrado = false;
		for (Cliente titular : this.titulares)
			if (titular.getNif().equals(cliente.nif)) {
				encontrado = true;
				break;
			}
		if (!encontrado)
			throw new ClienteNoAutorizadoException(nif, this.id);
		TarjetaCredito tarjeta = new TarjetaCredito();
		tarjeta.setCuenta(this);
		tarjeta.setTitular(cliente);
		tarjeta.setCredito(credito);
		Manager.getTarjetaCreditoDAO().save(tarjeta);
		return tarjeta;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Cliente> getTitulares() {
		return titulares;
	}

	public void setTitulares(List<Cliente> titulares) {
		this.titulares = titulares;
	}

	public boolean isCreada() {
		return creada;
	}

	public void setCreada(boolean creada) {
		this.creada = creada;
	}
}
