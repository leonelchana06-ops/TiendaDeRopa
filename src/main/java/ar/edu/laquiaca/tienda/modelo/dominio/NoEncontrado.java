package ar.edu.laquiaca.tienda.modelo.dominio;

/**
 * El registro solicitado no existe.
 * El controlador lo devuelve al navegador con el código HTTP 404.
 */
public class NoEncontrado extends RuntimeException {

    public NoEncontrado(String mensaje) {
        super(mensaje);
    }
}
