package ar.edu.laquiaca.tienda.servicio;

import ar.edu.laquiaca.tienda.modelo.dominio.Cliente;
import ar.edu.laquiaca.tienda.modelo.dominio.Empleado;
import ar.edu.laquiaca.tienda.modelo.dominio.ItemVenta;
import ar.edu.laquiaca.tienda.modelo.dominio.Producto;
import ar.edu.laquiaca.tienda.modelo.dominio.Venta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Reglas de negocio del sistema: productos, clientes, empleados y ventas.
 * Los controladores solo reciben la petición y llaman a estos métodos.
 */
public interface TiendaServicio {

    // ------------------------------- Productos -------------------------------
    public Producto registrarProducto(Producto producto);

    public Producto modificarProducto(int idProducto, String nombre, String descripcion,
                                      BigDecimal precio, Integer stock);

    public Optional<Producto> buscarProductoPorId(int idProducto);

    public List<Producto> listarProductos(String buscar, boolean soloDisponibles);

    public int actualizarStockProducto(int idProducto, int cantidad);

    public void eliminarProducto(int idProducto);

    // ------------------------------- Clientes --------------------------------
    public Cliente registrarCliente(Cliente cliente);

    public Cliente modificarCliente(int idCliente, String nombre, String apellido,
                                    String telefono, String correo);

    public Optional<Cliente> buscarClientePorId(int idCliente);

    public List<Cliente> listarClientes(String buscar);

    public List<Venta> consultarComprasCliente(int idCliente);

    public void eliminarCliente(int idCliente);

    // ------------------------------- Empleados -------------------------------
    public Empleado registrarEmpleado(Empleado empleado);

    public Empleado modificarEmpleado(int idEmpleado, String nombre, String apellido, String cargo);

    public Optional<Empleado> buscarEmpleadoPorId(int idEmpleado);

    public List<Empleado> listarEmpleados();

    public List<Venta> consultarVentasEmpleado(int idEmpleado);

    public void eliminarEmpleado(int idEmpleado);

    // -------------------------------- Ventas ---------------------------------
    public Venta registrarVenta(Integer idCliente, Integer idEmpleado, List<ItemVenta> items);

    public List<Venta> listarVentas(LocalDate desde, LocalDate hasta, Integer idCliente, Integer idEmpleado);

    public Optional<Venta> buscarVentaPorId(int idVenta);

    public Map<String, Object> resumen();
}
