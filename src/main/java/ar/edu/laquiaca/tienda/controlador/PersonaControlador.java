package ar.edu.laquiaca.tienda.controlador;

import ar.edu.laquiaca.tienda.modelo.dominio.Cliente;
import ar.edu.laquiaca.tienda.modelo.dominio.Empleado;
import ar.edu.laquiaca.tienda.modelo.dominio.NoEncontrado;
import ar.edu.laquiaca.tienda.modelo.dominio.Venta;
import ar.edu.laquiaca.tienda.servicio.TiendaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Rutas de clientes y empleados (registro, modificación, consulta y baja).
 *   /api/clientes[/{id}]   GET · POST · PUT · DELETE    + GET /api/clientes/{id}/compras
 *   /api/empleados[/{id}]  GET · POST · PUT · DELETE    + GET /api/empleados/{id}/ventas
 */
@RestController
public class PersonaControlador {

    @Autowired
    private TiendaServicio tiendaServicio;

    // ------------------------------- Clientes -------------------------------

    @GetMapping("/api/clientes")
    public List<Cliente> listarClientes(@RequestParam(required = false) String buscar) {
        return tiendaServicio.listarClientes(buscar);
    }

    @GetMapping("/api/clientes/{id}")
    public Cliente buscarCliente(@PathVariable int id) {
        return tiendaServicio.buscarClientePorId(id)
                .orElseThrow(() -> new NoEncontrado("El cliente " + id + " no existe."));
    }

    @GetMapping("/api/clientes/{id}/compras")
    public List<Venta> comprasDelCliente(@PathVariable int id) {
        return tiendaServicio.consultarComprasCliente(id);
    }

    @PostMapping("/api/clientes")
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente registrarCliente(@RequestBody ClientePeticion peticion) {
        Cliente cliente = new Cliente(peticion.nombre(), peticion.apellido(),
                peticion.telefono(), peticion.correo());
        return tiendaServicio.registrarCliente(cliente);
    }

    @PutMapping("/api/clientes/{id}")
    public Cliente modificarCliente(@PathVariable int id, @RequestBody ClientePeticion peticion) {
        return tiendaServicio.modificarCliente(id, peticion.nombre(), peticion.apellido(),
                peticion.telefono(), peticion.correo());
    }

    @DeleteMapping("/api/clientes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarCliente(@PathVariable int id) {
        tiendaServicio.eliminarCliente(id);
    }

    // ------------------------------- Empleados -------------------------------

    @GetMapping("/api/empleados")
    public List<Empleado> listarEmpleados() {
        return tiendaServicio.listarEmpleados();
    }

    @GetMapping("/api/empleados/{id}")
    public Empleado buscarEmpleado(@PathVariable int id) {
        return tiendaServicio.buscarEmpleadoPorId(id)
                .orElseThrow(() -> new NoEncontrado("El empleado " + id + " no existe."));
    }

    @GetMapping("/api/empleados/{id}/ventas")
    public List<Venta> ventasDelEmpleado(@PathVariable int id) {
        return tiendaServicio.consultarVentasEmpleado(id);
    }

    @PostMapping("/api/empleados")
    @ResponseStatus(HttpStatus.CREATED)
    public Empleado registrarEmpleado(@RequestBody EmpleadoPeticion peticion) {
        Empleado empleado = new Empleado(peticion.nombre(), peticion.apellido(), peticion.cargo());
        return tiendaServicio.registrarEmpleado(empleado);
    }

    @PutMapping("/api/empleados/{id}")
    public Empleado modificarEmpleado(@PathVariable int id, @RequestBody EmpleadoPeticion peticion) {
        return tiendaServicio.modificarEmpleado(id, peticion.nombre(), peticion.apellido(), peticion.cargo());
    }

    @DeleteMapping("/api/empleados/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarEmpleado(@PathVariable int id) {
        tiendaServicio.eliminarEmpleado(id);
    }

    // ------------------------------- JSON de entrada -------------------------------

    public record ClientePeticion(String nombre, String apellido, String telefono, String correo) { }

    public record EmpleadoPeticion(String nombre, String apellido, String cargo) { }
}
