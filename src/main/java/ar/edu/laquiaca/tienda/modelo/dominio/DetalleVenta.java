package ar.edu.laquiaca.tienda.modelo.dominio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DetalleVenta: id_detalle, producto, cantidad, subtotal.
 * Cada renglón de una venta (relación de COMPOSICIÓN con Venta).
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "detalles_venta")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private int idDetalle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "cantidad", nullable = false)
    private int cantidad;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    public DetalleVenta() { }

    /** Renglón nuevo: el subtotal se calcula con el precio actual del producto. */
    public DetalleVenta(Producto producto, int cantidad) {
        if (producto == null) throw new ErrorValidacion("Producto inexistente.");
        if (cantidad <= 0) throw new ErrorValidacion("La cantidad debe ser mayor a 0.");
        this.producto = producto;
        this.cantidad = cantidad;
        calcularSubtotal();
    }

    /** subtotal = precio unitario × cantidad */
    public BigDecimal calcularSubtotal() {
        this.subtotal = Validador.redondear(producto.getPrecio().multiply(BigDecimal.valueOf(cantidad)));
        return subtotal;
    }

    void sumarCantidad(int extra) {
        this.cantidad += extra;
        calcularSubtotal();
    }

    /** Precio unitario al momento de la venta. */
    public BigDecimal getPrecioUnitario() {
        return cantidad == 0 ? BigDecimal.ZERO
                : subtotal.divide(BigDecimal.valueOf(cantidad), 2, RoundingMode.HALF_UP);
    }

    public void setVenta(Venta venta) { this.venta = venta; }

    @JsonIgnore
    public Venta getVenta() { return venta; }

    public int getIdDetalle() { return idDetalle; }
    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getSubtotal() { return subtotal; }
}
