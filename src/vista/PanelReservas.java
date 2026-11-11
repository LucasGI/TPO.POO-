package vista;

import controlador.CatalogoController;
import controlador.ReservaController;
import controlador.SalidaController;
import excepcion.ReglaNegocioException;
import modelo.Cancelacion;
import modelo.Cliente;
import modelo.DatosPasajero;
import modelo.Empleado;
import modelo.MedioPago;
import modelo.NotaAjuste;
import modelo.Pago;
import modelo.Pasajero;
import modelo.Reserva;
import modelo.Salida;
import modelo.ServicioAdicional;
import modelo.Tour;
import modelo.Vendedor;
import persistencia.ArchivoDatos;
import persistencia.BaseDatos;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PanelReservas extends PanelBase {
    private ReservaController reservas;
    private SalidaController salidas;
    private CatalogoController catalogo;
    private PanelCatalogo panelCatalogo;

    private JButton btnVer;
    private JButton btnDetalle;
    private JButton btnCrear;
    private JButton btnPago;
    private JButton btnAgregarPasajero;
    private JButton btnReemplazar;
    private JButton btnAdicional;
    private JButton btnCancelar;

    public PanelReservas(JFrame ventana, ArchivoDatos archivo, BaseDatos base, ReservaController reservas,
                         SalidaController salidas, CatalogoController catalogo, PanelCatalogo panelCatalogo) {
        super(ventana, archivo, base);
        this.reservas = reservas;
        this.salidas = salidas;
        this.catalogo = catalogo;
        this.panelCatalogo = panelCatalogo;
        btnVer = agregarBoton("Ver reservas");
        btnDetalle = agregarBoton("Detalle de reserva");
        btnCrear = agregarBoton("Crear reserva");
        btnPago = agregarBoton("Registrar pago");
        btnAgregarPasajero = agregarBoton("Agregar pasajero");
        btnReemplazar = agregarBoton("Reemplazar pasajero");
        btnAdicional = agregarBoton("Contratar adicional");
        btnCancelar = agregarBoton("Cancelar reserva");
        verReservas();
    }

    @Override
    protected void procesar(Object fuente) throws ReglaNegocioException {
        if (fuente == btnVer) {
            verReservas();
        } else if (fuente == btnDetalle) {
            Reserva r = elegirReserva("Detalle de reserva", false);
            if (r != null) {
                mostrarDetalle(r);
            }
        } else if (fuente == btnCrear) {
            crearReserva();
        } else if (fuente == btnPago) {
            registrarPago();
        } else if (fuente == btnAgregarPasajero) {
            agregarPasajero();
        } else if (fuente == btnReemplazar) {
            reemplazarPasajero();
        } else if (fuente == btnAdicional) {
            contratarAdicional();
        } else if (fuente == btnCancelar) {
            cancelarReserva();
        }
    }

    public void verReservas() {
        List<String> lineas = new ArrayList<>();
        for (Reserva r : reservas.listarReservas()) {
            lineas.add(r.toString());
        }
        mostrar(lineas);
    }

    // CU05 Crear reserva
    private void crearReserva() throws ReglaNegocioException {
        List<Salida> disponibles = new ArrayList<>();
        for (Salida s : salidas.listarSalidas()) {
            if (s.getEstado().admiteReservas()) {
                disponibles.add(s);
            }
        }
        if (disponibles.isEmpty()) {
            mostrarError("No hay salidas que admitan reservas.");
            return;
        }
        String[] opcionesSalida = new String[disponibles.size()];
        for (int i = 0; i < disponibles.size(); i++) {
            Salida s = disponibles.get(i);
            opcionesSalida[i] = s.getId() + " - " + s.getTour().getNombre() + " - " + s.getFechaInicio()
                    + " (" + s.lugaresDisponibles() + " lugares)";
        }
        List<Vendedor> vendedores = new ArrayList<>();
        for (Empleado e : catalogo.listarEmpleados()) {
            if (e instanceof Vendedor) {
                vendedores.add((Vendedor) e);
            }
        }
        String[] opcionesVendedor = new String[vendedores.size()];
        for (int i = 0; i < vendedores.size(); i++) {
            opcionesVendedor[i] = vendedores.get(i).getLegajo() + " - " + vendedores.get(i).getNombreCompleto();
        }

        Formulario f = new Formulario(ventana, "Crear reserva");
        f.agregarOpciones("Salida", opcionesSalida);
        f.agregarTexto("Documento del titular");
        f.agregarOpciones("Vendedor", opcionesVendedor);
        f.agregarTexto("Cantidad de pasajeros", "1");
        if (!f.mostrar()) {
            return;
        }
        Salida salida = disponibles.get(f.opcion("Salida"));
        String documento = f.textoObligatorio("Documento del titular");
        int legajo = vendedores.get(f.opcion("Vendedor")).getLegajo();
        int cantidad = f.entero("Cantidad de pasajeros");
        // se avisa antes de pedir los datos de cada pasajero
        salida.verificarLugarPara(cantidad);

        if (catalogo.buscarCliente(documento) == null) {
            mostrarExito("El cliente " + documento + " no esta registrado. A continuacion se cargan sus datos.");
            Cliente nuevo = panelCatalogo.pedirCliente(documento);
            if (nuevo == null) {
                return;
            }
            catalogo.registrarCliente(nuevo);
            documento = nuevo.getDocumento();
        }

        List<DatosPasajero> datos = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            DatosPasajero d = pedirPasajero("Pasajero " + i + " de " + cantidad, salida.getTour());
            if (d == null) {
                return;
            }
            datos.add(d);
        }
        Reserva reserva = reservas.crearReserva(salida.getId(), documento, legajo, datos, LocalDate.now());
        mostrarDetalle(reserva);
        mostrarExito("Reserva " + reserva.getNumero() + " creada en estado " + reserva.getEstado() + ".\n"
                + reservas.detallePrecio(reserva.getNumero()) + "\nEstado de la salida: " + salida.getEstado());
    }

    // CU08 Registrar pago
    private void registrarPago() throws ReglaNegocioException {
        Reserva r = elegirReserva("Registrar pago", true);
        if (r == null) {
            return;
        }
        Formulario f = new Formulario(ventana, "Pago de la reserva " + r.getNumero());
        f.agregarTitulo("Saldo: $" + r.saldo() + " - sena minima: $" + r.montoSenia());
        f.agregarTexto("Importe", String.valueOf(r.saldo()));
        f.agregarOpciones("Medio de pago", new String[]{"Efectivo", "Transferencia", "Tarjeta"});
        f.agregarTexto("Cuotas (solo tarjeta)", "1");
        if (!f.mostrar()) {
            return;
        }
        MedioPago medio = MedioPago.EFECTIVO;
        if (f.opcion("Medio de pago") == 1) {
            medio = MedioPago.TRANSFERENCIA;
        } else if (f.opcion("Medio de pago") == 2) {
            medio = MedioPago.TARJETA;
        }
        Pago pago = reservas.registrarPago(r.getNumero(), f.decimal("Importe"), medio,
                f.entero("Cuotas (solo tarjeta)"), LocalDate.now());
        mostrarDetalle(r);
        mostrarExito("Pago registrado: " + pago + "\nTotal cobrado: $" + pago.getTotalCobrado()
                + "\nEstado de la reserva: " + r.getEstado() + " - saldo $" + r.saldo());
    }

    // CU09 Modificar reserva
    private void agregarPasajero() throws ReglaNegocioException {
        Reserva r = elegirReserva("Agregar pasajero", true);
        if (r == null) {
            return;
        }
        DatosPasajero d = pedirPasajero("Nuevo pasajero para la reserva " + r.getNumero(), r.getSalida().getTour());
        if (d != null) {
            reservas.agregarPasajero(r.getNumero(), d, LocalDate.now());
            mostrarDetalle(r);
            mostrarExito("Pasajero agregado.\n" + reservas.detallePrecio(r.getNumero()));
        }
    }

    private void reemplazarPasajero() throws ReglaNegocioException {
        Reserva r = elegirReserva("Reemplazar pasajero", true);
        if (r == null) {
            return;
        }
        List<Pasajero> pasajeros = r.getPasajeros();
        String[] opciones = new String[pasajeros.size()];
        for (int i = 0; i < pasajeros.size(); i++) {
            opciones[i] = pasajeros.get(i).toString();
        }
        Formulario f = new Formulario(ventana, "Pasajero que sale");
        f.agregarOpciones("Pasajero", opciones);
        if (!f.mostrar()) {
            return;
        }
        String docSale = pasajeros.get(f.opcion("Pasajero")).getDocumento();
        DatosPasajero nuevo = pedirPasajero("Pasajero que entra", null);
        if (nuevo == null) {
            return;
        }
        int notasAntes = r.getNotas().size();
        reservas.reemplazarPasajero(r.getNumero(), docSale, nuevo, LocalDate.now());
        String mensaje = "Pasajero reemplazado.\n" + reservas.detallePrecio(r.getNumero());
        if (r.getNotas().size() > notasAntes) {
            mensaje += "\nSe registro una nota de ajuste por la diferencia de precio.";
        }
        mostrarDetalle(r);
        mostrarExito(mensaje);
    }

    // CU07 Contratar servicio adicional
    private void contratarAdicional() throws ReglaNegocioException {
        Reserva r = elegirReserva("Contratar adicional", true);
        if (r == null) {
            return;
        }
        List<Pasajero> pasajeros = r.getPasajeros();
        String[] opcionesPasajero = new String[pasajeros.size()];
        for (int i = 0; i < pasajeros.size(); i++) {
            opcionesPasajero[i] = pasajeros.get(i).toString();
        }
        List<ServicioAdicional> servicios = r.getSalida().getTour().getServicios();
        if (servicios.isEmpty()) {
            mostrarError("El tour no ofrece servicios adicionales.");
            return;
        }
        String[] opcionesServicio = new String[servicios.size()];
        for (int i = 0; i < servicios.size(); i++) {
            opcionesServicio[i] = servicios.get(i).toString();
        }
        Formulario f = new Formulario(ventana, "Contratar adicional");
        f.agregarOpciones("Pasajero", opcionesPasajero);
        f.agregarOpciones("Servicio", opcionesServicio);
        if (!f.mostrar()) {
            return;
        }
        reservas.contratarAdicional(r.getNumero(), pasajeros.get(f.opcion("Pasajero")).getDocumento(),
                servicios.get(f.opcion("Servicio")).getCodigo(), LocalDate.now());
        mostrarDetalle(r);
        mostrarExito("Servicio contratado.\n" + reservas.detallePrecio(r.getNumero()));
    }

    // CU10 Cancelar reserva
    private void cancelarReserva() throws ReglaNegocioException {
        Formulario f = new Formulario(ventana, "Cancelar reserva");
        f.agregarTexto("Numero de reserva");
        f.agregarTexto("Motivo");
        f.agregarCasilla("La cancela la agencia");
        if (!f.mostrar()) {
            return;
        }
        int numero = f.entero("Numero de reserva");
        Cancelacion c = reservas.cancelarReserva(numero, f.textoObligatorio("Motivo"),
                f.marcado("La cancela la agencia"), LocalDate.now());
        String mensaje = "Reserva " + numero + " anulada.\nImporte a devolver: $" + c.getImporteDevuelto();
        if (c.getMedio() != null) {
            mensaje += " por " + c.getMedio();
        }
        verReservas();
        mostrarExito(mensaje);
    }

    // pide el numero de reserva; si soloActivas, avisa cuando esta anulada o finalizada
    private Reserva elegirReserva(String titulo, boolean soloActivas) throws ReglaNegocioException {
        Formulario f = new Formulario(ventana, titulo);
        f.agregarTexto("Numero de reserva");
        if (!f.mostrar()) {
            return null;
        }
        Reserva r = reservas.buscarReserva(f.entero("Numero de reserva"));
        if (soloActivas && !r.estaActiva()) {
            throw new ReglaNegocioException("La reserva " + r.getNumero() + " esta " + r.getEstado() + ".");
        }
        return r;
    }

    // si se pasa el tour, se ofrecen sus servicios opcionales como casillas
    private DatosPasajero pedirPasajero(String titulo, Tour tour) {
        Formulario f = new Formulario(ventana, titulo);
        f.agregarTexto("Nombre");
        f.agregarTexto("Apellido");
        f.agregarTexto("Documento");
        f.agregarTexto("Nacimiento (aaaa-mm-dd)");
        f.agregarCasilla("Jubilado");
        f.agregarTexto("Contacto de emergencia");
        f.agregarTexto("Restricciones");
        List<ServicioAdicional> opcionales = new ArrayList<>();
        if (tour != null) {
            for (ServicioAdicional s : tour.getServicios()) {
                if (!tour.serviciosObligatorios().contains(s)) {
                    opcionales.add(s);
                }
            }
            if (!opcionales.isEmpty()) {
                f.agregarTitulo("Servicios adicionales:");
            }
            for (ServicioAdicional s : opcionales) {
                f.agregarCasilla(s.getNombre() + " $" + s.getPrecio());
            }
        }
        if (!f.mostrar()) {
            return null;
        }
        DatosPasajero d = new DatosPasajero(f.textoObligatorio("Nombre"), f.textoObligatorio("Apellido"),
                f.textoObligatorio("Documento"), f.fecha("Nacimiento (aaaa-mm-dd)"), f.marcado("Jubilado"),
                f.texto("Contacto de emergencia"), f.texto("Restricciones"));
        for (ServicioAdicional s : opcionales) {
            if (f.marcado(s.getNombre() + " $" + s.getPrecio())) {
                d.agregarAdicional(s.getCodigo());
            }
        }
        return d;
    }

    private void mostrarDetalle(Reserva r) throws ReglaNegocioException {
        List<String> lineas = new ArrayList<>();
        lineas.add(r.toString());
        lineas.add("Fecha: " + r.getFecha() + " - vendedor: " + r.getVendedor().getNombreCompleto());
        lineas.add(reservas.detallePrecio(r.getNumero()));
        lineas.add("");
        lineas.add("Pasajeros:");
        LocalDate fechaSalida = r.getSalida().getFechaInicio();
        for (Pasajero p : r.getPasajeros()) {
            String linea = "  " + p + " - " + p.tipoEn(fechaSalida);
            if (p.isJubilado()) {
                linea += " (jubilado)";
            }
            lineas.add(linea);
            for (ServicioAdicional s : p.getAdicionales()) {
                lineas.add("      + " + s);
            }
        }
        lineas.add("");
        lineas.add("Pagos:");
        for (Pago p : r.getPagos()) {
            lineas.add("  " + p);
        }
        if (!r.getNotas().isEmpty()) {
            lineas.add("");
            lineas.add("Notas de ajuste:");
            for (NotaAjuste n : r.getNotas()) {
                lineas.add("  " + n);
            }
        }
        if (r.getCancelacion() != null) {
            lineas.add("");
            lineas.add("Cancelacion: " + r.getCancelacion());
        }
        mostrar(lineas);
    }
}
