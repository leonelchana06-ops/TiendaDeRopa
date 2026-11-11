package ar.edu.laquiaca.tienda.repositorio;

import ar.edu.laquiaca.tienda.modelo.dominio.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a la tabla "empleados" (Spring Data JPA genera las consultas).
 */
public interface EmpleadoRepositorio extends JpaRepository<Empleado, Integer> {

    List<Empleado> findAllByOrderByApellidoAscNombreAsc();
}
