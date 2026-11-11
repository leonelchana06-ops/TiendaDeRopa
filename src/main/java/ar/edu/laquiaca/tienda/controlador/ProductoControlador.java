package ar.edu.laquiaca.tienda.controlador;

import ar.edu.laquiaca.tienda.modelo.dominio.ErrorValidacion;
import ar.edu.laquiaca.tienda.modelo.dominio.NoEncontrado;
import ar.edu.laquiaca.tienda.modelo.dominio.Producto;
import ar.edu.laquiaca.tienda.servicio.TiendaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Rutas de productos:
 *   GET    /api/productos?buscar=&disponibles=true   consulta
 *   GET    /api/productos/{id}                       detalle
 *   POST   /api/productos                            registrar
 *   PUT    /api/productos/{id}                       modificar
 *   POST   /api/productos/{id}/stock                 ajustar stock
 *   DELETE /api/productos/{id}                       eliminar
 */
@RestController
@RequestMapping("/api/productos")
public class ProductoControlador {

    @Autowired
    private TiendaServicio tiendaServicio;

    @GetMapping
    public List<Producto> listar(@RequestParam(required = false) String buscar,
                                 @RequestParam(required = false) Boolean disponibles) {
        return tiendaServicio.listarProductos(buscar, Boolean.TRUE.equals(disponibles));
    }

    @GetMapping("/{id}")
    public Producto buscar(@PathVariable int id) {
        return tiendaServicio.buscarProductoPorId(id)
                .orElseThrow(() -> new NoEncontrado("El producto " + id + " no existe."));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto registrar(@RequestBody ProductoPeticion peticion) {
        Producto producto = new Producto(peticion.nombre(), peticion.descripcion(),
                peticion.precio(), peticion.stock());
        return tiendaServicio.registrarProducto(producto);
    }

    @PutMapping("/{id}")
    public Producto modificar(@PathVariable int id, @RequestBody ProductoPeticion peticion) {
        return tiendaServicio.modificarProducto(id, peticion.nombre(), peticion.descripcion(),
                peticion.precio(), peticion.stock());
    }

    @PostMapping("/{id}/stock")
    public Map<String, Integer> actualizarStock(@PathVariable int id, @RequestBody AjusteStockPeticion peticion) {
        if (peticion.cantidad() == null || peticion.cantidad() == 0) {
            throw new ErrorValidacion("Indique una cantidad distinta de 0.");
        }
        int stock = tiendaServicio.actualizarStockProducto(id, peticion.cantidad());
        return Map.of("stock", stock);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable int id) {
        tiendaServicio.eliminarProducto(id);
    }

    // ------------------------------- JSON de entrada -------------------------------

    public record ProductoPeticion(String nombre, String descripcion, BigDecimal precio, Integer stock) { }

    public record AjusteStockPeticion(Integer cantidad) { }
}
