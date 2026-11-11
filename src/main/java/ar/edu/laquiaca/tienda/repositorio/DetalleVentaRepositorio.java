package ar.edu.laquiaca.tienda.repositorio;

import ar.edu.laquiaca.tienda.modelo.dominio.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a la tabla "detalles_venta".
 * Los renglones se guardan junto con su venta (cascada), pero este repositorio
 * permite consultarlos para impedir borrar productos que ya fueron vendidos.
 */
public interface DetalleVentaRepositorio extends JpaRepository<DetalleVenta, Integer> {

    boolean existsByProducto_IdProducto(int idProducto);
}
