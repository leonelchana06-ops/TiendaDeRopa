package ar.edu.laquiaca.tienda.modelo.dominio;

/**
 * Error de validación de los datos de negocio.
 * El controlador lo devuelve al navegador con el código HTTP 400.
 */
public class ErrorValidacion extends RuntimeException {

    public ErrorValidacion(String mensaje) {
        super(mensaje);
    }
}
