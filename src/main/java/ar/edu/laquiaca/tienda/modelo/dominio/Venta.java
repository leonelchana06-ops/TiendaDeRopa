package ar.edu.laquiaca.tienda.modelo.dominio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Venta (cabecera): id_venta, fechaHora, total, cliente, empleado, detalles.
 * Agrupa los renglones vendidos (COMPOSICIÓN con DetalleVenta) y calcula el total.
 *
 * El guardado y el descuento de stock se hacen en el servicio, dentro de una
 * sola transacción (ver TiendaServicioImpl.registrarVenta).
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "ventas")
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venta")
    private int idVenta;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO.setScale(2);

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_empleado", nullable = false)
    private Empleado empleado;

    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    public Venta() { }

    /** Nueva venta asociada a un cliente y un empleado. */
    public Venta(Cliente cliente, Empleado empleado) {
        if (cliente == null) throw new ErrorValidacion("Debe seleccionar un cliente válido.");
        if (empleado == null) throw new ErrorValidacion("Debe seleccionar un empleado válido.");
        this.cliente = cliente;
        this.empleado = empleado;
        this.fechaHora = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    }

    /** Agrega un producto a la venta; si ya estaba, suma la cantidad. */
    public void agregarProducto(Producto producto, int cantidad) {
        if (producto == null) throw new ErrorValidacion("Producto inexistente.");
        if (cantidad <= 0) throw new ErrorValidacion("La cantidad debe ser mayor a 0.");
        for (DetalleVenta detalle : detalles) {
            if (detalle.getProducto().getIdProducto() == producto.getIdProducto()) {
                detalle.sumarCantidad(cantidad);
                calcularTotal();
                return;
            }
        }
        DetalleVenta detalle = new DetalleVenta(producto, cantidad);
        detalle.setVenta(this);
        detalles.add(detalle);
        calcularTotal();
    }

    /** total = suma de subtotales */
    public BigDecimal calcularTotal() {
        BigDecimal suma = BigDecimal.ZERO;
        for (DetalleVenta detalle : detalles) {
            suma = suma.add(detalle.getSubtotal());
        }
        this.total = Validador.redondear(suma);
        return total;
    }

    /** Valida que la venta tenga renglones y que haya stock suficiente. */
    public void validar() {
        if (detalles.isEmpty()) throw new ErrorValidacion("La venta debe tener al menos un producto.");
        for (DetalleVenta detalle : detalles) {
            if (detalle.getCantidad() > detalle.getProducto().getStock()) {
                throw new ErrorValidacion("Stock insuficiente para «" + detalle.getProducto().getNombre()
                        + "»: disponible " + detalle.getProducto().getStock()
                        + ", solicitado " + detalle.getCantidad() + ".");
            }
        }
    }

    /** Cantidad total de unidades vendidas (dato calculado que usa el listado). */
    public int getUnidades() {
        return detalles.stream().mapToInt(DetalleVenta::getCantidad).sum();
    }

    public int getIdVenta() { return idVenta; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public BigDecimal getTotal() { return total; }
    public Cliente getCliente() { return cliente; }
    public Empleado getEmpleado() { return empleado; }
    public List<DetalleVenta> getDetalles() { return detalles; }
}
