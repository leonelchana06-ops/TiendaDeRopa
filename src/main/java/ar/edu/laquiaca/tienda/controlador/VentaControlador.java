package ar.edu.laquiaca.tienda.controlador;

import ar.edu.laquiaca.tienda.modelo.dominio.ErrorValidacion;
import ar.edu.laquiaca.tienda.modelo.dominio.ItemVenta;
import ar.edu.laquiaca.tienda.modelo.dominio.NoEncontrado;
import ar.edu.laquiaca.tienda.modelo.dominio.Venta;
import ar.edu.laquiaca.tienda.servicio.TiendaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * Rutas de ventas:
 *   POST /api/ventas          registra una venta (transacción + descuento de stock)
 *   GET  /api/ventas          historial (?desde=AAAA-MM-DD&hasta=&idCliente=&idEmpleado=)
 *   GET  /api/ventas/{id}     detalle completo
 *   GET  /api/resumen         indicadores del panel
 */
@RestController
public class VentaControlador {

    @Autowired
    private TiendaServicio tiendaServicio;

    @GetMapping("/api/ventas")
    public List<Venta> listar(@RequestParam(required = false) String desde,
                              @RequestParam(required = false) String hasta,
                              @RequestParam(required = false) String idCliente,
                              @RequestParam(required = false) String idEmpleado) {
        LocalDate fechaDesde = fecha(desde, "desde");
        LocalDate fechaHasta = fecha(hasta, "hasta");
        if (fechaDesde != null && fechaHasta != null && fechaDesde.isAfter(fechaHasta)) {
            throw new ErrorValidacion("La fecha «desde» no puede ser posterior a «hasta».");
        }
        return tiendaServicio.listarVentas(fechaDesde, fechaHasta,
                entero(idCliente, "idCliente"), entero(idEmpleado, "idEmpleado"));
    }

    @GetMapping("/api/ventas/{id}")
    public Venta buscar(@PathVariable int id) {
        return tiendaServicio.buscarVentaPorId(id)
                .orElseThrow(() -> new NoEncontrado("La venta " + id + " no existe."));
    }

    @PostMapping("/api/ventas")
    @ResponseStatus(HttpStatus.CREATED)
    public Venta registrar(@RequestBody VentaPeticion peticion) {
        return tiendaServicio.registrarVenta(peticion.idCliente(), peticion.idEmpleado(), peticion.items());
    }

    @GetMapping("/api/resumen")
    public Map<String, Object> resumen() {
        return tiendaServicio.resumen();
    }

    // ------------------------------- Utilidades -------------------------------

    private static LocalDate fecha(String valor, String nombre) {
        if (valor == null || valor.isBlank()) return null;
        try {
            return LocalDate.parse(valor.trim());
        } catch (DateTimeParseException e) {
            throw new ErrorValidacion("La fecha «" + nombre + "» no es válida (use AAAA-MM-DD).");
        }
    }

    private static Integer entero(String valor, String nombre) {
        if (valor == null || valor.isBlank()) return null;
        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            throw new ErrorValidacion("El parámetro «" + nombre + "» debe ser un número entero.");
        }
    }

    // ------------------------------- JSON de entrada -------------------------------

    public record VentaPeticion(Integer idCliente, Integer idEmpleado, List<ItemVenta> items) { }
}
