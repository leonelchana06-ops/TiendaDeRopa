package ar.edu.laquiaca.tienda.modelo.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.regex.Pattern;

/**
 * Reglas de validación reutilizadas por todas las clases del modelo
 * (aplicación de ENCAPSULAMIENTO: los datos solo entran por métodos que validan).
 */
final class Validador {

    private static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern TELEFONO = Pattern.compile("^[0-9+\\-\\s()]{6,20}$");
    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("99999999.99");

    private Validador() { }

    /** Limpia espacios y valida obligatoriedad y longitud máxima. Devuelve null si queda vacío y es opcional. */
    static String texto(String valor, String campo, int maximo, boolean obligatorio) {
        String v = valor == null ? "" : valor.trim();
        if (obligatorio && v.isEmpty()) {
            throw new ErrorValidacion("El campo «" + campo + "» es obligatorio.");
        }
        if (v.length() > maximo) {
            throw new ErrorValidacion("El campo «" + campo + "» admite como máximo " + maximo + " caracteres.");
        }
        return v.isEmpty() ? null : v;
    }

    static BigDecimal precio(BigDecimal valor) {
        if (valor == null) throw new ErrorValidacion("El campo «Precio» es obligatorio.");
        if (valor.compareTo(BigDecimal.ZERO) <= 0 || valor.compareTo(PRECIO_MAXIMO) > 0) {
            throw new ErrorValidacion("El precio debe ser mayor a 0 y menor a 100.000.000.");
        }
        return redondear(valor);
    }

    static int stock(Integer valor) {
        if (valor == null) throw new ErrorValidacion("El campo «Stock» es obligatorio.");
        if (valor < 0) throw new ErrorValidacion("El stock no puede ser negativo.");
        return valor;
    }

    static String correo(String valor) {
        String v = texto(valor, "Correo", 100, false);
        if (v != null && !CORREO.matcher(v).matches()) {
            throw new ErrorValidacion("El correo electrónico no es válido.");
        }
        return v == null ? null : v.toLowerCase();
    }

    static String telefono(String valor) {
        String v = texto(valor, "Teléfono", 20, false);
        if (v != null && !TELEFONO.matcher(v).matches()) {
            throw new ErrorValidacion("El teléfono debe tener entre 6 y 20 caracteres: números, espacios, +, - o paréntesis.");
        }
        return v;
    }

    static BigDecimal redondear(BigDecimal monto) {
        return monto.setScale(2, RoundingMode.HALF_UP);
    }
}
