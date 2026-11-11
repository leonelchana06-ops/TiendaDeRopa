package ar.edu.laquiaca.tienda.modelo.dominio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Empleado: id_empleado, nombre, apellido, cargo.
 * Hereda nombre y apellido de Persona.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "empleados")
public class Empleado extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_empleado")
    private int idEmpleado;

    @Column(name = "cargo", nullable = false, length = 50)
    private String cargo;

    public Empleado() { }

    public Empleado(String nombre, String apellido, String cargo) {
        super(nombre, apellido);
        this.cargo = cargo;
    }

    @Override
    public void validar() {
        validarNombreApellido();
        this.cargo = Validador.texto(cargo, "Cargo", 50, true);
    }

    /** modificarDatos(): actualiza los datos del empleado validándolos. */
    public void modificarDatos(String nombre, String apellido, String cargo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.cargo = cargo;
        validar();
    }

    public int getIdEmpleado() { return idEmpleado; }
    public String getCargo() { return cargo; }
}
