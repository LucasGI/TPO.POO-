package persistencia;

import java.io.Serializable;

// agrupa todos los repositorios para guardarlos juntos en un solo archivo
public class BaseDatos implements Serializable {
    private TourRepositorio tours;
    private SalidaRepositorio salidas;
    private ClienteRepositorio clientes;
    private ReservaRepositorio reservas;
    private EmpleadoRepositorio empleados;
    private VehiculoRepositorio vehiculos;
    private ProveedorRepositorio proveedores;

    public BaseDatos() {
        tours = new TourRepositorio();
        salidas = new SalidaRepositorio();
        clientes = new ClienteRepositorio();
        reservas = new ReservaRepositorio();
        empleados = new EmpleadoRepositorio();
        vehiculos = new VehiculoRepositorio();
        proveedores = new ProveedorRepositorio();
    }

    public TourRepositorio getTours() {
        return tours;
    }

    public SalidaRepositorio getSalidas() {
        return salidas;
    }

    public ClienteRepositorio getClientes() {
        return clientes;
    }

    public ReservaRepositorio getReservas() {
        return reservas;
    }

    public EmpleadoRepositorio getEmpleados() {
        return empleados;
    }

    public VehiculoRepositorio getVehiculos() {
        return vehiculos;
    }

    public ProveedorRepositorio getProveedores() {
        return proveedores;
    }
}
