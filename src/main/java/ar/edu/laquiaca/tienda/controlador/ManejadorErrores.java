package ar.edu.laquiaca.tienda.controlador;

import ar.edu.laquiaca.tienda.modelo.dominio.ErrorValidacion;
import ar.edu.laquiaca.tienda.modelo.dominio.NoEncontrado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

/**
 * Convierte las excepciones en la misma respuesta JSON que espera el frontend:
 *   { "error": "mensaje en español" }
 */
@RestControllerAdvice
public class ManejadorErrores {

    private static final Logger registro = LoggerFactory.getLogger(ManejadorErrores.class);

    @ExceptionHandler(ErrorValidacion.class)
    public ResponseEntity<Map<String, String>> errorValidacion(ErrorValidacion e) {
        return responder(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(NoEncontrado.class)
    public ResponseEntity<Map<String, String>> errorNoEncontrado(NoEncontrado e) {
        return responder(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> errorCuerpo(HttpMessageNotReadableException e) {
        return responder(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es un JSON válido.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> errorParametro(MethodArgumentTypeMismatchException e) {
        return responder(HttpStatus.BAD_REQUEST,
                "El valor del parámetro «" + e.getName() + "» no es válido.");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> errorIntegridad(DataIntegrityViolationException e) {
        registro.error("Violación de integridad de datos", e);
        return responder(HttpStatus.BAD_REQUEST,
                "No se puede completar la operación porque el registro está vinculado a otros datos.");
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> errorBaseDatos(DataAccessException e) {
        registro.error("Error de base de datos", e);
        Throwable causa = e.getMostSpecificCause();
        String detalle = causa == null || causa.getMessage() == null ? "" : ": " + causa.getMessage();
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Error de base de datos" + detalle);
    }

    private static ResponseEntity<Map<String, String>> responder(HttpStatus estado, String mensaje) {
        String texto = mensaje == null || mensaje.isBlank() ? "No se pudo completar la operación." : mensaje;
        return ResponseEntity.status(estado).body(Map.of("error", texto));
    }
}
