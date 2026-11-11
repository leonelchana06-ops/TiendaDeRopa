package ar.edu.laquiaca.tienda.repositorio;

import ar.edu.laquiaca.tienda.modelo.dominio.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Acceso a la tabla "ventas".
 */
public interface VentaRepositorio extends JpaRepository<Venta, Integer> {

    /**
     * Historial con filtros opcionales (fechas inclusive) y con los datos
     * relacionados cargados: cliente, empleado, renglones y productos.
     */
    @Query("""
            SELECT DISTINCT v FROM Venta v
            JOIN FETCH v.cliente
            JOIN FETCH v.empleado
            LEFT JOIN FETCH v.detalles d
            LEFT JOIN FETCH d.producto
            WHERE (:desde IS NULL OR v.fechaHora >= :desde)
              AND (:hasta IS NULL OR v.fechaHora < :hasta)
              AND (:idCliente IS NULL OR v.cliente.idCliente = :idCliente)
              AND (:idEmpleado IS NULL OR v.empleado.idEmpleado = :idEmpleado)
            ORDER BY v.fechaHora DESC, v.idVenta DESC
            """)
    List<Venta> buscar(@Param("desde") LocalDateTime desde,
                       @Param("hasta") LocalDateTime hasta,
                       @Param("idCliente") Integer idCliente,
                       @Param("idEmpleado") Integer idEmpleado);

    long countByFechaHoraGreaterThanEqual(LocalDateTime desde);

    @Query("SELECT SUM(v.total) FROM Venta v WHERE v.fechaHora >= :desde")
    BigDecimal sumarTotalDesde(@Param("desde") LocalDateTime desde);

    @Query("SELECT SUM(v.total) FROM Venta v")
    BigDecimal sumarTotal();

    boolean existsByCliente_IdCliente(int idCliente);

    boolean existsByEmpleado_IdEmpleado(int idEmpleado);
}
