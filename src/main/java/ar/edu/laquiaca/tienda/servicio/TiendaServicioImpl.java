package ar.edu.laquiaca.tienda.servicio;

import ar.edu.laquiaca.tienda.modelo.dominio.Cliente;
import ar.edu.laquiaca.tienda.modelo.dominio.DetalleVenta;
import ar.edu.laquiaca.tienda.modelo.dominio.Empleado;
import ar.edu.laquiaca.tienda.modelo.dominio.ErrorValidacion;
import ar.edu.laquiaca.tienda.modelo.dominio.ItemVenta;
import ar.edu.laquiaca.tienda.modelo.dominio.NoEncontrado;
import ar.edu.laquiaca.tienda.modelo.dominio.Producto;
import ar.edu.laquiaca.tienda.modelo.dominio.Venta;
import ar.edu.laquiaca.tienda.repositorio.ClienteRepositorio;
import ar.edu.laquiaca.tienda.repositorio.DetalleVentaRepositorio;
import ar.edu.laquiaca.tienda.repositorio.EmpleadoRepositorio;
import ar.edu.laquiaca.tienda.repositorio.ProductoRepositorio;
import ar.edu.laquiaca.tienda.repositorio.VentaRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

/**
 * Implementación de las reglas de negocio.
 * Cada operación se ejecuta dentro de una transacción de base de datos.
 */
@Service
public class TiendaServicioImpl implements TiendaServicio {

    @Autowired
    private ProductoRepositorio productoRepositorio;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Autowired
    private EmpleadoRepositorio empleadoRepositorio;

    @Autowired
    private VentaRepositorio ventaRepositorio;

    @Autowired
    private DetalleVentaRepositorio detalleVentaRepositorio;

    // =================================================================
    // Productos
    // =================================================================

    @Override
    @Transactional
    public Producto registrarProducto(Producto producto) {
        return productoRepositorio.save(producto);
    }

    @Override
    @Transactional
    public Producto modificarProducto(int idProducto, String nombre, String descripcion,
                                      BigDecimal precio, Integer stock) {
        Producto producto = buscarProductoPorId(idProducto)
                .orElseThrow(() -> new NoEncontrado("El producto " + idProducto + " no existe."));
        producto.modificarDatos(nombre, descripcion, precio, stock);
        return productoRepositorio.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> buscarProductoPorId(int idProducto) {
        return productoRepositorio.findById(idProducto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> listarProductos(String buscar, boolean soloDisponibles) {
        String patron = patronDeBusqueda(buscar);
        List<Producto> productos = patron == null
                ? productoRepositorio.findAllByOrderByNombreAsc()
                : productoRepositorio.buscarPorNombreODescripcion(patron);

        if (!soloDisponibles) return productos;
        return productos.stream().filter(producto -> producto.getStock() > 0).toList();
    }

    @Override
    @Transactional
    public int actualizarStockProducto(int idProducto, int cantidad) {
        Producto producto = productoRepositorio.buscarParaActualizar(idProducto)
                .orElseThrow(() -> new NoEncontrado("El producto " + idProducto + " no existe."));
        return producto.actualizarStock(cantidad);
    }

    @Override
    @Transactional
    public void eliminarProducto(int idProducto) {
        Producto producto = buscarProductoPorId(idProducto)
                .orElseThrow(() -> new NoEncontrado("El producto " + idProducto + " no existe."));

        if (detalleVentaRepositorio.existsByProducto_IdProducto(idProducto)) {
            throw new ErrorValidacion("No se puede eliminar «" + producto.getNombre()
                    + "» porque figura en ventas registradas. "
                    + "Puede dejar su stock en 0 para que no se venda más.");
        }
        productoRepositorio.delete(producto);
    }

    // =================================================================
    // Clientes
    // =================================================================

    @Override
    @Transactional
    public Cliente registrarCliente(Cliente cliente) {
        cliente.validar();
        return clienteRepositorio.save(cliente);
    }

    @Override
    @Transactional
    public Cliente modificarCliente(int idCliente, String nombre, String apellido,
                                    String telefono, String correo) {
        Cliente cliente = buscarClientePorId(idCliente)
                .orElseThrow(() -> new NoEncontrado("El cliente " + idCliente + " no existe."));
        cliente.modificarDatos(nombre, apellido, telefono, correo);
        return clienteRepositorio.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarClientePorId(int idCliente) {
        return clienteRepositorio.findById(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> listarClientes(String buscar) {
        String patron = patronDeBusqueda(buscar);
        return patron == null
                ? clienteRepositorio.findAllByOrderByApellidoAscNombreAsc()
                : clienteRepositorio.buscar(patron);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> consultarComprasCliente(int idCliente) {
        buscarClientePorId(idCliente)
                .orElseThrow(() -> new NoEncontrado("El cliente " + idCliente + " no existe."));
        return ventaRepositorio.buscar(null, null, idCliente, null);
    }

    @Override
    @Transactional
    public void eliminarCliente(int idCliente) {
        Cliente cliente = buscarClientePorId(idCliente)
                .orElseThrow(() -> new NoEncontrado("El cliente " + idCliente + " no existe."));

        if (ventaRepositorio.existsByCliente_IdCliente(idCliente)) {
            throw new ErrorValidacion("No se puede eliminar un cliente que tiene ventas registradas.");
        }
        clienteRepositorio.delete(cliente);
    }

    // =================================================================
    // Empleados
    // =================================================================

    @Override
    @Transactional
    public Empleado registrarEmpleado(Empleado empleado) {
        empleado.validar();
        return empleadoRepositorio.save(empleado);
    }

    @Override
    @Transactional
    public Empleado modificarEmpleado(int idEmpleado, String nombre, String apellido, String cargo) {
        Empleado empleado = buscarEmpleadoPorId(idEmpleado)
                .orElseThrow(() -> new NoEncontrado("El empleado " + idEmpleado + " no existe."));
        empleado.modificarDatos(nombre, apellido, cargo);
        return empleadoRepositorio.save(empleado);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> buscarEmpleadoPorId(int idEmpleado) {
        return empleadoRepositorio.findById(idEmpleado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> listarEmpleados() {
        return empleadoRepositorio.findAllByOrderByApellidoAscNombreAsc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> consultarVentasEmpleado(int idEmpleado) {
        buscarEmpleadoPorId(idEmpleado)
                .orElseThrow(() -> new NoEncontrado("El empleado " + idEmpleado + " no existe."));
        return ventaRepositorio.buscar(null, null, null, idEmpleado);
    }

    @Override
    @Transactional
    public void eliminarEmpleado(int idEmpleado) {
        Empleado empleado = buscarEmpleadoPorId(idEmpleado)
                .orElseThrow(() -> new NoEncontrado("El empleado " + idEmpleado + " no existe."));

        if (ventaRepositorio.existsByEmpleado_IdEmpleado(idEmpleado)) {
            throw new ErrorValidacion("No se puede eliminar un empleado que tiene ventas registradas.");
        }
        empleadoRepositorio.delete(empleado);
    }

    // =================================================================
    // Ventas
    // =================================================================

    /**
     * Registra la venta en UNA SOLA TRANSACCIÓN:
     *  1) bloquea cada producto (SELECT ... FOR UPDATE) para que nadie más lo modifique,
     *  2) controla que haya stock y lo descuenta,
     *  3) guarda la cabecera de la venta y sus renglones.
     * Si algo falla se hace ROLLBACK y la base queda exactamente como estaba.
     * Los precios se toman SIEMPRE de la base de datos, nunca del navegador.
     */
    @Override
    @Transactional
    public Venta registrarVenta(Integer idCliente, Integer idEmpleado, List<ItemVenta> items) {
        if (idCliente == null) throw new ErrorValidacion("Debe seleccionar un cliente.");
        if (idEmpleado == null) throw new ErrorValidacion("Debe seleccionar un empleado.");
        if (items == null || items.isEmpty()) throw new ErrorValidacion("La venta debe tener al menos un producto.");
        if (items.size() > 200) throw new ErrorValidacion("Demasiados renglones en una sola venta.");

        Cliente cliente = buscarClientePorId(idCliente)
                .orElseThrow(() -> new ErrorValidacion("Debe seleccionar un cliente válido."));
        Empleado empleado = buscarEmpleadoPorId(idEmpleado)
                .orElseThrow(() -> new ErrorValidacion("Debe seleccionar un empleado válido."));

        // Une renglones repetidos y los ordena por producto para bloquear siempre en el mismo orden.
        TreeMap<Integer, Integer> cantidades = new TreeMap<>();
        for (ItemVenta item : items) {
            if (item == null || item.idProducto() == null) {
                throw new ErrorValidacion("Hay un renglón sin producto.");
            }
            if (item.cantidad() == null) {
                throw new ErrorValidacion("Hay un renglón sin cantidad.");
            }
            if (item.cantidad() <= 0) {
                throw new ErrorValidacion("La cantidad debe ser mayor a 0.");
            }
            cantidades.merge(item.idProducto(), item.cantidad(), Integer::sum);
        }

        Venta venta = new Venta(cliente, empleado);
        for (Map.Entry<Integer, Integer> renglon : cantidades.entrySet()) {
            Producto producto = productoRepositorio.buscarParaActualizar(renglon.getKey())
                    .orElseThrow(() -> new ErrorValidacion("El producto " + renglon.getKey() + " no existe."));
            int disponible = producto.getStock();
            if (renglon.getValue() > disponible) {
                throw new ErrorValidacion("Stock insuficiente para «" + producto.getNombre()
                        + "»: disponible " + disponible + ", solicitado " + renglon.getValue() + ".");
            }
            venta.agregarProducto(producto, renglon.getValue());
        }

        venta.validar();

        // Los productos ya están bloqueados por esta transacción: el descuento es seguro.
        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.getProducto().actualizarStock(-detalle.getCantidad());
        }

        return ventaRepositorio.save(venta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarVentas(LocalDate desde, LocalDate hasta, Integer idCliente, Integer idEmpleado) {
        LocalDateTime inicio = desde == null ? null : desde.atStartOfDay();
        LocalDateTime fin = hasta == null ? null : hasta.plusDays(1).atStartOfDay();
        return ventaRepositorio.buscar(inicio, fin, idCliente, idEmpleado);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Venta> buscarVentaPorId(int idVenta) {
        return ventaRepositorio.findById(idVenta);
    }

    /** Indicadores que muestra el panel principal. */
    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> resumen() {
        LocalDateTime inicioHoy = LocalDate.now().atStartOfDay();

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("ventasHoy", ventaRepositorio.countByFechaHoraGreaterThanEqual(inicioHoy));
        resumen.put("montoHoy", monto(ventaRepositorio.sumarTotalDesde(inicioHoy)));
        resumen.put("ventasTotal", ventaRepositorio.count());
        resumen.put("montoTotal", monto(ventaRepositorio.sumarTotal()));
        resumen.put("productos", productoRepositorio.count());
        resumen.put("unidadesStock", unidades(productoRepositorio.sumarStock()));
        resumen.put("clientes", clienteRepositorio.count());
        return resumen;
    }

    // =================================================================
    // Utilidades
    // =================================================================

    /** Arma el patrón LIKE (%texto%) o null si no hay texto para buscar. */
    private static String patronDeBusqueda(String buscar) {
        if (buscar == null || buscar.isBlank()) return null;
        return "%" + buscar.trim().toLowerCase() + "%";
    }

    private static BigDecimal monto(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO.setScale(2) : valor;
    }

    private static long unidades(Long valor) {
        return valor == null ? 0L : valor;
    }
}
