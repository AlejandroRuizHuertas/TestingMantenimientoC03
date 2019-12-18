package edu.uclm.esi.iso2.banco20193capas.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Representa un movimiento en una cuenta bancaria
 * 
 */
@Entity
public class MovimientoCuenta {
	@Id @GeneratedValue(strategy = GenerationType.AUTO) 
	private Long id;
	@ManyToOne
	private Cuenta cuenta;
	
	private double importe;
	private String concepto;
	
	public MovimientoCuenta() {
	}

	public MovimientoCuenta(Cuenta cuenta, double importe, String concepto) {
		this.importe = importe;
		this.concepto = concepto;
		this.cuenta = cuenta;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cuenta getCuenta() {
		return cuenta;
	}

	public void setCuenta(Cuenta cuenta) {
		this.cuenta = cuenta;
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
}
