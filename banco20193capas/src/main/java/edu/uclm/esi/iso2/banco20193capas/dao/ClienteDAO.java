package edu.uclm.esi.iso2.banco20193capas.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import edu.uclm.esi.iso2.banco20193capas.model.Cliente;

public interface ClienteDAO extends CrudRepository<Cliente, Long>{
	Optional<Cliente> findByNif(String nif);
}
