package controlador;

import excepcion.ReglaNegocioException;
import modelo.Cancelacion;
import modelo.Cliente;
import modelo.DatosPasajero;
import modelo.Empleado;
import modelo.MedioPago;
import modelo.Pago;
import modelo.Pasajero;
import modelo.Reserva;
import modelo.Salida;
import modelo.ServicioAdicional;
import modelo.Vendedor;
import persistencia.Repositorio;
import servicio.CalculadoraPrecio;
import servicio.PoliticaCancelacion;
import servicio.PoliticaCancelacionAgencia;
import servicio.PoliticaCancelacionCliente;

import java.time.LocalDate;
import java.util.List;

// recibe los pedidos de la vista sobre reservas y coordina el modelo
public class ReservaController {
    private Repositorio<Reserva, Integer> reservas;
    private Repositorio<Salida, Integer> salidas;
    private Repositorio<Cliente, String> clientes;
    private Repositorio<Empleado, Integer> empleados;
    private CalculadoraPrecio calculadora;

    public ReservaController(Repositorio<Reserva, Integer> reservas, Repositorio<Salida, Integer> salidas,
                             Repositorio<Cliente, String> clientes, Repositorio<Empleado, Integer> empleados,
                             CalculadoraPrecio calculadora) {
        this.reservas = reservas;
        this.salidas = salidas;
        this.clientes = clientes;
        this.empleados = empleados;
        this.calculadora = calculadora;
    }

    // CU05 Crear reserva
    public Reserva crearReserva(int idSalida, String docCliente, int legajoVendedor, List<DatosPasajero> datos,
                                LocalDate hoy) throws ReglaNegocioException {
        Salida salida = buscarSalida(idSalida);
        Cliente cliente = clientes.buscarPorId(docCliente);
        if (cliente == null) {
            throw new ReglaNegocioException("No existe un cliente con documento " + docCliente + ".");
        }
        Vendedor vendedor = buscarVendedor(legajoVendedor);
        if (salida.getTour().tarifaVigente(salida.getFechaInicio()) == null) {
            throw new ReglaNegocioException("La salida no tiene una tarifa vigente.");
        }
        // se validan los codigos antes de crear la reserva para no dejarla a medias
        for (DatosPasajero d : datos) {
            for (String codigo : d.getCodigosAdicionales()) {
                buscarServicio(salida, codigo);
            }
        }
        Reserva reserva = salida.reservar(reservas.cantidad() + 1, hoy, cliente, vendedor, datos);
        // adicionales opcionales elegidos para cada pasajero
        List<Pasajero> pasajeros = reserva.getPasajeros();
        for (int i = 0; i < datos.size(); i++) {
            for (String codigo : datos.get(i).getCodigosAdicionales()) {
                reserva.agregarAdicional(pasajeros.get(i), buscarServicio(salida, codigo));
            }
        }
        reserva.actualizarTotal(calculadora.calcularTotal(reserva), hoy, "Alta de reserva");
        reservas.guardar(reserva);
        salidas.guardar(salida);
        return reserva;
    }

    // CU08 Registrar pago
    public Pago registrarPago(int numero, double importe, MedioPago medio, int cuotas, LocalDate hoy)
            throws ReglaNegocioException {
        Reserva reserva = buscarReserva(numero);
        Pago pago = reserva.registrarPago(importe, medio, cuotas, hoy);
        reservas.guardar(reserva);
        return pago;
    }

    // CU09 Modificar reserva: agregar un pasajero
    public Reserva agregarPasajero(int numero, DatosPasajero datos, LocalDate hoy) throws ReglaNegocioException {
        Reserva reserva = buscarReserva(numero);
        if (!reserva.estaActiva()) {
            throw new ReglaNegocioException("La reserva " + numero + " esta " + reserva.getEstado() + ".");
        }
        Salida salida = reserva.getSalida();
        salida.verificarLugarPara(1);
        for (String codigo : datos.getCodigosAdicionales()) {
            buscarServicio(salida, codigo);
        }
        Pasajero nuevo = reserva.agregarPasajero(datos);
        for (String codigo : datos.getCodigosAdicionales()) {
            reserva.agregarAdicional(nuevo, buscarServicio(salida, codigo));
        }
        reserva.actualizarTotal(calculadora.calcularTotal(reserva), hoy, "Se agrega a " + nuevo.getNombreCompleto());
        salida.actualizarEstado();
        reservas.guardar(reserva);
        salidas.guardar(salida);
        return reserva;
    }

    // CU09 Modificar reserva: reemplazar un pasajero sin cargo
    public Reserva reemplazarPasajero(int numero, String docSale, DatosPasajero nuevo, LocalDate hoy)
            throws ReglaNegocioException {
        Reserva reserva = buscarReserva(numero);
        reserva.reemplazarPasajero(docSale, nuevo, hoy);
        reserva.actualizarTotal(calculadora.calcularTotal(reserva), hoy,
                "Reemplazo de " + docSale + " por " + nuevo.getDocumento());
        reservas.guardar(reserva);
        return reserva;
    }

    // CU07 Contratar servicio adicional
    public Reserva contratarAdicional(int numero, String docPasajero, String codigoServicio, LocalDate hoy)
            throws ReglaNegocioException {
        Reserva reserva = buscarReserva(numero);
        if (!reserva.estaActiva()) {
            throw new ReglaNegocioException("La reserva " + numero + " esta " + reserva.getEstado() + ".");
        }
        Pasajero pasajero = reserva.buscarPasajero(docPasajero);
        if (pasajero == null) {
            throw new ReglaNegocioException("No hay un pasajero con documento " + docPasajero + " en la reserva.");
        }
        ServicioAdicional servicio = buscarServicio(reserva.getSalida(), codigoServicio);
        reserva.agregarAdicional(pasajero, servicio);
        reserva.actualizarTotal(calculadora.calcularTotal(reserva), hoy, "Adicional " + servicio.getNombre());
        reservas.guardar(reserva);
        return reserva;
    }

    // CU10 Cancelar reserva
    public Cancelacion cancelarReserva(int numero, String motivo, boolean porAgencia, LocalDate hoy)
            throws ReglaNegocioException {
        Reserva reserva = buscarReserva(numero);
        PoliticaCancelacion politica;
        if (porAgencia) {
            politica = new PoliticaCancelacionAgencia();
        } else {
            politica = new PoliticaCancelacionCliente();
        }
        Cancelacion cancelacion = reserva.cancelar(motivo, hoy, politica);
        reservas.guardar(reserva);
        salidas.guardar(reserva.getSalida());
        return cancelacion;
    }

    public String detallePrecio(int numero) throws ReglaNegocioException {
        Reserva reserva = buscarReserva(numero);
        String promo = "sin promocion";
        if (calculadora.mejorPromocion(reserva) != null) {
            promo = calculadora.mejorPromocion(reserva).getNombre() + " "
                    + (int) (calculadora.mejorPromocion(reserva).porcentaje() * 100) + "%";
        }
        return "Total $" + reserva.getTotal() + " (" + promo + ", adicionales $" + reserva.totalAdicionales()
                + ") - sena minima $" + reserva.montoSenia();
    }

    public Reserva buscarReserva(int numero) throws ReglaNegocioException {
        Reserva reserva = reservas.buscarPorId(numero);
        if (reserva == null) {
            throw new ReglaNegocioException("No existe la reserva " + numero + ".");
        }
        return reserva;
    }

    public List<Reserva> listarReservas() {
        return reservas.listar();
    }

    private Salida buscarSalida(int id) throws ReglaNegocioException {
        Salida salida = salidas.buscarPorId(id);
        if (salida == null) {
            throw new ReglaNegocioException("No existe la salida " + id + ".");
        }
        return salida;
    }

    private Vendedor buscarVendedor(int legajo) throws ReglaNegocioException {
        Empleado empleado = empleados.buscarPorId(legajo);
        if (!(empleado instanceof Vendedor)) {
            throw new ReglaNegocioException("El legajo " + legajo + " no corresponde a un vendedor.");
        }
        return (Vendedor) empleado;
    }

    private ServicioAdicional buscarServicio(Salida salida, String codigo) throws ReglaNegocioException {
        ServicioAdicional servicio = salida.getTour().buscarServicio(codigo);
        if (servicio == null) {
            throw new ReglaNegocioException("El tour no ofrece el servicio " + codigo + ".");
        }
        return servicio;
    }
}
