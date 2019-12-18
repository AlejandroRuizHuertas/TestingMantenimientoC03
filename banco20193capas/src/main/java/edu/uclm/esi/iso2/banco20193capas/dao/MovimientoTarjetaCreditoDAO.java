package edu.uclm.esi.iso2.banco20193capas.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import edu.uclm.esi.iso2.banco20193capas.model.MovimientoTarjetaCredito;

public interface MovimientoTarjetaCreditoDAO extends CrudRepository<MovimientoTarjetaCredito, Long>{
	List<MovimientoTarjetaCredito> findByTarjetaId(Long id);
}
