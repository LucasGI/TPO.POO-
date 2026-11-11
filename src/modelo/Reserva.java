package modelo;

import excepcion.EstadoInvalidoException;
import excepcion.PagoInvalidoException;
import excepcion.PasajeroInvalidoException;
import excepcion.ReglaNegocioException;
import servicio.PoliticaCancelacion;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Reserva implements Serializable {
    public static final double PORCENTAJE_SENIA = 0.30;
    public static final double RECARGO_CUOTAS = 0.07;
    public static final int DIAS_VENCIMIENTO_SALDO = 10;
    public static final int DIAS_LIMITE_REEMPLAZO = 3;

    private int numero;
    private LocalDate fecha;
    private EstadoReserva estado;
    private double total;
    private Cliente titular;
    private Vendedor vendedor;
    private Salida salida;
    private List<Pasajero> pasajeros;
    private List<Pago> pagos;
    private List<NotaAjuste> notas;
    private Cancelacion cancelacion;

    // la crea Salida.reservar(), por eso el constructor no es publico
    Reserva(int numero, LocalDate fecha, Cliente titular, Vendedor vendedor, Salida salida) {
        this.numero = numero;
        this.fecha = fecha;
        this.titular = titular;
        this.vendedor = vendedor;
        this.salida = salida;
        this.estado = EstadoReserva.PENDIENTE;
        this.total = 0;
        this.pasajeros = new ArrayList<>();
        this.pagos = new ArrayList<>();
        this.notas = new ArrayList<>();
    }

    // Creador: la reserva contiene a sus pasajeros
    public Pasajero agregarPasajero(DatosPasajero datos) throws PasajeroInvalidoException {
        if (buscarPasajero(datos.getDocumento()) != null) {
            throw new PasajeroInvalidoException("El pasajero con documento " + datos.getDocumento()
                    + " ya esta en la reserva.");
        }
        Pasajero pasajero = new Pasajero(datos);
        salida.getTour().validarPasajero(pasajero, salida.getFechaInicio());
        for (ServicioAdicional s : salida.getTour().serviciosObligatorios()) {
            pasajero.agregarAdicional(s);
        }
        pasajeros.add(pasajero);
        return pasajero;
    }

    public void reemplazarPasajero(String documento, DatosPasajero datos, LocalDate hoy) throws ReglaNegocioException {
        verificarActiva();
        if (salida.diasHasta(hoy) < DIAS_LIMITE_REEMPLAZO) {
            throw new ReglaNegocioException("Los reemplazos se permiten hasta " + DIAS_LIMITE_REEMPLAZO
                    + " dias antes de la salida.");
        }
        Pasajero anterior = buscarPasajero(documento);
        if (anterior == null) {
            throw new PasajeroInvalidoException("No hay un pasajero con documento " + documento + " en la reserva.");
        }
        Pasajero nuevo = new Pasajero(datos);
        salida.getTour().validarPasajero(nuevo, salida.getFechaInicio());
        // el reemplazante conserva los adicionales contratados
        for (ServicioAdicional s : anterior.getAdicionales()) {
            nuevo.agregarAdicional(s);
        }
        int posicion = pasajeros.indexOf(anterior);
        pasajeros.set(posicion, nuevo);
    }

    public void agregarAdicional(Pasajero pasajero, ServicioAdicional servicio) throws ReglaNegocioException {
        if (salida.getTour().buscarServicio(servicio.getCodigo()) == null) {
            throw new ReglaNegocioException("El servicio " + servicio.getCodigo() + " no pertenece al tour.");
        }
        if (!pasajeros.contains(pasajero)) {
            throw new PasajeroInvalidoException("El pasajero no pertenece a la reserva.");
        }
        pasajero.agregarAdicional(servicio);
    }

    // Creador: la reserva registra sus pagos y aplica el recargo por cuotas
    public Pago registrarPago(double importe, MedioPago medio, int cuotas, LocalDate hoy) throws ReglaNegocioException {
        verificarActiva();
        if (importe <= 0) {
            throw new PagoInvalidoException("El importe debe ser mayor a cero.");
        }
        if (cuotas < 1) {
            throw new PagoInvalidoException("La cantidad de cuotas debe ser al menos 1.");
        }
        if (medio != MedioPago.TARJETA && cuotas > 1) {
            throw new PagoInvalidoException("Solo se puede pagar en cuotas con tarjeta.");
        }
        double saldo = saldo();
        if (importe > saldo + 0.01) {
            throw new PagoInvalidoException("El importe supera el saldo pendiente de $" + saldo + ".");
        }
        if (salida.diasHasta(hoy) < DIAS_VENCIMIENTO_SALDO && importe < saldo - 0.01) {
            throw new PagoInvalidoException("Faltan menos de " + DIAS_VENCIMIENTO_SALDO
                    + " dias para la salida: se debe pagar el saldo completo ($" + saldo + ").");
        }
        double recargo = 0;
        if (medio == MedioPago.TARJETA && cuotas > 1) {
            recargo = redondear(importe * RECARGO_CUOTAS);
        }
        Pago pago = new Pago(hoy, importe, recargo, medio, cuotas);
        pagos.add(pago);
        if (estado == EstadoReserva.PENDIENTE && totalPagado() >= montoSenia() - 0.01) {
            estado = EstadoReserva.CONFIRMADA;
        }
        return pago;
    }

    public Cancelacion cancelar(String motivo, LocalDate hoy, PoliticaCancelacion politica) throws EstadoInvalidoException {
        if (!estaActiva()) {
            throw new EstadoInvalidoException("La reserva " + numero + " esta " + estado + " y no se puede cancelar.");
        }
        if (salida.getEstado() == EstadoSalida.EN_CURSO) {
            throw new EstadoInvalidoException("La salida ya esta en curso, la reserva no se puede cancelar.");
        }
        long dias = salida.diasHasta(hoy);
        double porcentaje = politica.porcentajeDevolucion(dias);
        double importe = redondear(totalPagado() * porcentaje);
        MedioPago medio = null;
        if (!pagos.isEmpty()) {
            // la devolucion se hace por el mismo medio del primer pago (S13)
            medio = pagos.get(0).getMedio();
        }
        cancelacion = new Cancelacion(hoy, motivo, politica.esCancelacionDeAgencia(), importe, medio);
        estado = EstadoReserva.ANULADA;
        salida.liberarLugares(this);
        return cancelacion;
    }

    // si ya hay pagos, la diferencia de precio queda registrada en una nota de ajuste
    public void actualizarTotal(double nuevoTotal, LocalDate hoy, String detalle) {
        double diferencia = redondear(nuevoTotal - total);
        if (!pagos.isEmpty() && diferencia != 0) {
            notas.add(new NotaAjuste(hoy, diferencia, detalle));
        }
        total = redondear(nuevoTotal);
        if (estado == EstadoReserva.CONFIRMADA && totalPagado() < montoSenia() - 0.01) {
            estado = EstadoReserva.PENDIENTE;
        }
    }

    public void finalizar() {
        if (estaActiva()) {
            estado = EstadoReserva.FINALIZADA;
        }
    }

    private void verificarActiva() throws EstadoInvalidoException {
        if (!estaActiva()) {
            throw new EstadoInvalidoException("La reserva " + numero + " esta " + estado + ".");
        }
    }

    public boolean estaActiva() {
        return estado == EstadoReserva.PENDIENTE || estado == EstadoReserva.CONFIRMADA;
    }

    // las reservas activas o finalizadas ocupan lugar en la salida
    public boolean ocupaLugar() {
        return estado != EstadoReserva.ANULADA;
    }

    public Pasajero buscarPasajero(String documento) {
        for (Pasajero p : pasajeros) {
            if (p.getDocumento().equals(documento)) {
                return p;
            }
        }
        return null;
    }

    public int cantidadLugares() {
        return pasajeros.size();
    }

    public double totalPagado() {
        double suma = 0;
        for (Pago p : pagos) {
            suma += p.getImporte();
        }
        return redondear(suma);
    }

    public double totalCobrado() {
        double suma = 0;
        for (Pago p : pagos) {
            suma += p.getTotalCobrado();
        }
        return redondear(suma);
    }

    public double saldo() {
        return redondear(total - totalPagado());
    }

    public double montoSenia() {
        return redondear(total * PORCENTAJE_SENIA);
    }

    public double totalAdicionales() {
        double suma = 0;
        for (Pasajero p : pasajeros) {
            suma += p.totalAdicionales();
        }
        return suma;
    }

    private double redondear(double valor) {
        return Math.round(valor * 100) / 100.0;
    }

    public int getNumero() {
        return numero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public double getTotal() {
        return total;
    }

    public Cliente getTitular() {
        return titular;
    }

    public Vendedor getVendedor() {
        return vendedor;
    }

    public Salida getSalida() {
        return salida;
    }

    public List<Pasajero> getPasajeros() {
        return new ArrayList<>(pasajeros);
    }

    public List<Pago> getPagos() {
        return new ArrayList<>(pagos);
    }

    public List<NotaAjuste> getNotas() {
        return new ArrayList<>(notas);
    }

    public Cancelacion getCancelacion() {
        return cancelacion;
    }

    @Override
    public String toString() {
        return "Reserva " + numero + " - " + estado + " - salida " + salida.getId() + " (" + salida.getTour().getNombre()
                + ") - titular " + titular.getNombreCompleto() + " - " + pasajeros.size() + " pasajeros - total $"
                + total + " - pagado $" + totalPagado() + " - saldo $" + saldo();
    }
}
