package ar.edu.laquiaca.tienda.repositorio;

import ar.edu.laquiaca.tienda.modelo.dominio.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Acceso a la tabla "productos" (Spring Data JPA genera las consultas).
 */
public interface ProductoRepositorio extends JpaRepository<Producto, Integer> {

    List<Producto> findAllByOrderByNombreAsc();

    @Query("""
            SELECT p FROM Producto p
            WHERE LOWER(p.nombre) LIKE :patron OR LOWER(p.descripcion) LIKE :patron
            ORDER BY p.nombre
            """)
    List<Producto> buscarPorNombreODescripcion(@Param("patron") String patron);

    /**
     * Bloquea la fila del producto mientras dura la transacción (SELECT ... FOR UPDATE).
     * Se usa al ajustar el stock y al registrar una venta para que dos operaciones
     * simultáneas no dejen el stock negativo.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.idProducto = :idProducto")
    Optional<Producto> buscarParaActualizar(@Param("idProducto") int idProducto);

    @Query("SELECT SUM(p.stock) FROM Producto p")
    Long sumarStock();
}
