package edu.uclm.esi.iso2.banco20193capas.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Representa un movimiento asociado a una tarjeta de crédito
 *
 */
@Entity
public class MovimientoTarjetaCredito {
	@Id @GeneratedValue(strategy = GenerationType.AUTO) 
	private Long id;
	@ManyToOne
	private TarjetaCredito tarjeta;
	
	private double importe;
	private String concepto;
	private boolean liquidado;
	
	public MovimientoTarjetaCredito() {
	}

	public MovimientoTarjetaCredito(TarjetaCredito tarjeta, double importe, String concepto) {
		this.importe = importe;
		this.concepto = concepto;
		this.tarjeta = tarjeta;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TarjetaCredito getTarjeta() {
		return tarjeta;
	}

	public void setTarjeta(TarjetaCredito tarjeta) {
		this.tarjeta = tarjeta;
	}

	public double getImporte() {
		return importe;
	}

	public void setImporte(double importe) {
		this.importe = importe;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public boolean isLiquidado() {
		return liquidado;
	}

	public void setLiquidado(boolean liquidado) {
		this.liquidado = liquidado;
	}
	
	
}
