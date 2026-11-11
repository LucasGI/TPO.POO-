package controlador;

import excepcion.ReglaNegocioException;
import modelo.Cliente;
import modelo.Empleado;
import modelo.Proveedor;
import modelo.Tarifa;
import modelo.Tour;
import modelo.Vehiculo;
import persistencia.Repositorio;

import java.util.List;

// altas del catalogo de tours, clientes, personal, vehiculos y proveedores
public class CatalogoController {
    private Repositorio<Tour, String> tours;
    private Repositorio<Cliente, String> clientes;
    private Repositorio<Empleado, Integer> empleados;
    private Repositorio<Vehiculo, String> vehiculos;
    private Repositorio<Proveedor, String> proveedores;

    public CatalogoController(Repositorio<Tour, String> tours, Repositorio<Cliente, String> clientes,
                              Repositorio<Empleado, Integer> empleados, Repositorio<Vehiculo, String> vehiculos,
                              Repositorio<Proveedor, String> proveedores) {
        this.tours = tours;
        this.clientes = clientes;
        this.empleados = empleados;
        this.vehiculos = vehiculos;
        this.proveedores = proveedores;
    }

    // CU01 Administrar catalogo: el tour llega armado desde la vista y se valida antes de guardarlo
    public void altaTour(Tour tour) throws ReglaNegocioException {
        if (tours.buscarPorId(tour.getCodigo()) != null) {
            throw new ReglaNegocioException("Ya existe un tour con codigo " + tour.getCodigo() + ".");
        }
        tour.validar();
        tours.guardar(tour);
    }

    // CU02 Gestionar tarifas por temporada
    public void agregarTarifa(String codigoTour, Tarifa tarifa) throws ReglaNegocioException {
        Tour tour = buscarTour(codigoTour);
        tour.agregarTarifa(tarifa);
        tours.guardar(tour);
    }

    // CU04 Registrar cliente
    public void registrarCliente(Cliente cliente) throws ReglaNegocioException {
        if (clientes.buscarPorId(cliente.getDocumento()) != null) {
            throw new ReglaNegocioException("Ya existe un cliente con documento " + cliente.getDocumento() + ".");
        }
        clientes.guardar(cliente);
    }

    public void registrarEmpleado(Empleado empleado) throws ReglaNegocioException {
        if (empleados.buscarPorId(empleado.getLegajo()) != null) {
            throw new ReglaNegocioException("Ya existe un empleado con legajo " + empleado.getLegajo() + ".");
        }
        empleados.guardar(empleado);
    }

    public void registrarVehiculo(Vehiculo vehiculo) throws ReglaNegocioException {
        if (vehiculos.buscarPorId(vehiculo.getPatente()) != null) {
            throw new ReglaNegocioException("Ya existe un vehiculo con patente " + vehiculo.getPatente() + ".");
        }
        vehiculos.guardar(vehiculo);
    }

    public void registrarProveedor(Proveedor proveedor) throws ReglaNegocioException {
        if (proveedores.buscarPorId(proveedor.getCuit()) != null) {
            throw new ReglaNegocioException("Ya existe un proveedor con CUIT " + proveedor.getCuit() + ".");
        }
        proveedores.guardar(proveedor);
    }

    public Tour buscarTour(String codigo) throws ReglaNegocioException {
        Tour tour = tours.buscarPorId(codigo.toUpperCase());
        if (tour == null) {
            throw new ReglaNegocioException("No existe el tour " + codigo + ".");
        }
        return tour;
    }

    public Cliente buscarCliente(String documento) {
        return clientes.buscarPorId(documento);
    }

    public Proveedor buscarProveedor(String cuit) {
        return proveedores.buscarPorId(cuit);
    }

    public List<Tour> listarTours() {
        return tours.listar();
    }

    public List<Cliente> listarClientes() {
        return clientes.listar();
    }

    public List<Empleado> listarEmpleados() {
        return empleados.listar();
    }

    public List<Vehiculo> listarVehiculos() {
        return vehiculos.listar();
    }

    public List<Proveedor> listarProveedores() {
        return proveedores.listar();
    }
}
