package ar.edu.laquiaca.tienda.config;

import ar.edu.laquiaca.tienda.modelo.dominio.Cliente;
import ar.edu.laquiaca.tienda.modelo.dominio.Empleado;
import ar.edu.laquiaca.tienda.modelo.dominio.Producto;
import ar.edu.laquiaca.tienda.servicio.TiendaServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Carga productos, clientes y empleados de ejemplo la primera vez que se
 * inicia el sistema (solo si las tablas están vacías).
 * Se puede desactivar con datos.ejemplo=false en application.properties.
 */
@Component
public class DatosIniciales implements CommandLineRunner {

    private static final Logger registro = LoggerFactory.getLogger(DatosIniciales.class);

    private final TiendaServicio tiendaServicio;

    @Value("${datos.ejemplo:true}")
    private boolean cargarDatos;

    public DatosIniciales(TiendaServicio tiendaServicio) {
        this.tiendaServicio = tiendaServicio;
    }

    @Override
    public void run(String... args) {
        if (!cargarDatos) return;
        try {
            cargarProductos();
            cargarClientes();
            cargarEmpleados();
        } catch (Exception e) {
            registro.warn("No se pudieron cargar los datos de ejemplo: {}", e.getMessage());
        }
    }

    private void cargarProductos() {
        if (!tiendaServicio.listarProductos(null, false).isEmpty()) return;

        tiendaServicio.registrarProducto(new Producto("Remera básica",
                "Algodón 100%, color blanco, talle M", new BigDecimal("12000.00"), 30));
        tiendaServicio.registrarProducto(new Producto("Jean recto",
                "Denim azul clásico, talle 42", new BigDecimal("38500.00"), 15));
        tiendaServicio.registrarProducto(new Producto("Campera de abrigo",
                "Polar con capucha, color gris", new BigDecimal("54000.00"), 8));
        tiendaServicio.registrarProducto(new Producto("Gorro de lana",
                "Lana de llama tejida a mano", new BigDecimal("9500.00"), 20));
        tiendaServicio.registrarProducto(new Producto("Buzo canguro",
                "Frisa, color bordó, talle L", new BigDecimal("27900.00"), 4));

        registro.info("Productos de ejemplo cargados.");
    }

    private void cargarClientes() {
        if (!tiendaServicio.listarClientes(null).isEmpty()) return;

        tiendaServicio.registrarCliente(new Cliente("Final", "Consumidor", null, null));
        tiendaServicio.registrarCliente(new Cliente("María", "Quispe", "3885 123456", "maria.quispe@mail.com"));
        tiendaServicio.registrarCliente(new Cliente("Jorge", "Mamani", "3885 987654", null));

        registro.info("Clientes de ejemplo cargados.");
    }

    private void cargarEmpleados() {
        if (!tiendaServicio.listarEmpleados().isEmpty()) return;

        tiendaServicio.registrarEmpleado(new Empleado("Lucía", "Cruz", "Vendedora"));
        tiendaServicio.registrarEmpleado(new Empleado("Carlos", "Flores", "Encargado"));

        registro.info("Empleados de ejemplo cargados.");
    }
}
