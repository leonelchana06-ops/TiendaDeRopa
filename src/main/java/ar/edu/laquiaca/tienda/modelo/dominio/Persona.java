package ar.edu.laquiaca.tienda.modelo.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * Clase base abstracta con los datos comunes de Cliente y Empleado
 * (aplicación de HERENCIA y ENCAPSULAMIENTO).
 *
 * @MappedSuperclass indica que sus columnas (nombre y apellido) se guardan
 * en la tabla de cada subclase, sin crear una tabla propia.
 */
@MappedSuperclass
public abstract class Persona {

    @Column(name = "nombre", nullable = false, length = 100)
    protected String nombre;

    @Column(name = "apellido", nullable = false, length = 100)
    protected String apellido;

    protected Persona() { }

    protected Persona(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
    }

    /** Valida y normaliza nombre y apellido (obligatorios, máx. 100). */
    protected void validarNombreApellido() {
        this.nombre = Validador.texto(nombre, "Nombre", 100, true);
        this.apellido = Validador.texto(apellido, "Apellido", 100, true);
    }

    /** Cada subclase define sus propias reglas (POLIMORFISMO). */
    public abstract void validar();

    public String getNombreCompleto() {
        return apellido + ", " + nombre;
    }

    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
}
