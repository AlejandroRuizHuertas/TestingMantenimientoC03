package edu.uclm.esi.iso2.banco20193capas.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Cliente {
	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Column(unique = true)
	protected String nif;
	
	private String nombre;
	private String apellidos;

	
	public Cliente() {
	}
	
	/**
	 * Crea un cliente 
	 * @param nif	NIF del cliente
	 * @param nombre	Nombre del cliente
	 * @param apellidos	Apellidos del cliente
	 */
	public Cliente(String nif, String nombre, String apellidos) {
		this.nif = nif;
		this.nombre = nombre;
		this.apellidos = apellidos;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellidos() {
		return apellidos;
	}
	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}
	
	/**
	 * Inserta un cliente en la base de datos
	 */
	public void insert() {
		Manager.getClienteDAO().save(this);
	}
}
