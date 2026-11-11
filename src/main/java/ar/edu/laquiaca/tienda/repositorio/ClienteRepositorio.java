package ar.edu.laquiaca.tienda.repositorio;

import ar.edu.laquiaca.tienda.modelo.dominio.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Acceso a la tabla "clientes" (Spring Data JPA genera las consultas).
 */
public interface ClienteRepositorio extends JpaRepository<Cliente, Integer> {

    List<Cliente> findAllByOrderByApellidoAscNombreAsc();

    @Query("""
            SELECT c FROM Cliente c
            WHERE LOWER(c.nombre) LIKE :patron
               OR LOWER(c.apellido) LIKE :patron
               OR LOWER(c.correo) LIKE :patron
               OR c.telefono LIKE :patron
            ORDER BY c.apellido, c.nombre
            """)
    List<Cliente> buscar(@Param("patron") String patron);
}
