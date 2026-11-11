package ar.edu.laquiaca.tienda.modelo.dominio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Cliente: id_cliente, nombre, apellido, telefono, correo.
 * Hereda nombre y apellido de Persona.
 */
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "clientes")
public class Cliente extends Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private int idCliente;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "correo", length = 100)
    private String correo;

    public Cliente() { }

    public Cliente(String nombre, String apellido, String telefono, String correo) {
        super(nombre, apellido);
        this.telefono = telefono;
        this.correo = correo;
    }

    @Override
    public void validar() {
        validarNombreApellido();
        this.telefono = Validador.telefono(telefono);
        this.correo = Validador.correo(correo);
    }

    /** modificarDatos(): actualiza los datos del cliente validándolos. */
    public void modificarDatos(String nombre, String apellido, String telefono, String correo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.correo = correo;
        validar();
    }

    public int getIdCliente() { return idCliente; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
}
