package ar.edu.laquiaca.tienda.modelo.dominio;

/**
 * Renglón solicitado al registrar una venta: qué producto y cuántas unidades.
 * Se usa para recibir el JSON que envía el navegador.
 */
public record ItemVenta(Integer idProducto, Integer cantidad) {
}
