package ar.edu.laquiaca.tienda.modelo.dominio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Producto: id_producto, nombre, descripcion, precio, stock.
 * El estado del producto solo cambia mediante los métodos de negocio
 * (ENCAPSULAMIENTO): no hay setters públicos.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private int idProducto;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 2000)
    private String descripcion;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "stock", nullable = false)
    private int stock;

    public Producto() { }

    /** Crea un producto nuevo (aún no guardado) validando sus datos. */
    public Producto(String nombre, String descripcion, BigDecimal precio, Integer stock) {
        modificarDatos(nombre, descripcion, precio, stock);
    }

    /** modificarDatos(): actualiza la información del producto. */
    public void modificarDatos(String nombre, String descripcion, BigDecimal precio, Integer stock) {
        this.nombre = Validador.texto(nombre, "Nombre", 100, true);
        this.descripcion = Validador.texto(descripcion, "Descripción", 2000, false);
        this.precio = Validador.precio(precio);
        this.stock = Validador.stock(stock);
    }

    /**
     * actualizarStock(int cantidad): suma (cantidad &gt; 0) o resta (cantidad &lt; 0) unidades.
     * No permite que el stock resultante sea negativo.
     */
    public int actualizarStock(int cantidad) {
        if (stock + cantidad < 0) {
            throw new ErrorValidacion("Stock insuficiente para «" + nombre + "».");
        }
        this.stock += cantidad;
        return this.stock;
    }

    public int getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public int getStock() { return stock; }
}
