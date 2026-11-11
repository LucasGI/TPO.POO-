package modelo;

import excepcion.AsignacionInvalidaException;
import excepcion.CupoInsuficienteException;
import excepcion.EstadoInvalidoException;
import excepcion.ReglaNegocioException;
import servicio.PoliticaCancelacion;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Salida implements Serializable, Comparable<Salida> {
    public static final double PORCENTAJE_GUIA_ADICIONALES = 0.05;
    public static final int DIAS_CONTROL_CUPO_MINIMO = 7;

    private int id;
    private Tour tour;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private int cupoMaximo;
    private EstadoSalida estado;
    private boolean listaEmitida;
    private Guia guia;
    private Vehiculo vehiculo;
    private List<Reserva> reservas;
    private Liquidacion liquidacion;

    // se crea desde Tour.crearSalida()
    Salida(int id, Tour tour, LocalDate fechaInicio, int cupoMaximo) {
        this.id = id;
        this.tour = tour;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaInicio.plusDays(tour.getDuracionDias() - 1);
        this.cupoMaximo = cupoMaximo;
        this.estado = EstadoSalida.PROGRAMADA;
        this.listaEmitida = false;
        this.reservas = new ArrayList<>();
    }

    // Creador: la salida contiene sus reservas y conoce el cupo
    public Reserva reservar(int numero, LocalDate hoy, Cliente titular, Vendedor vendedor, List<DatosPasajero> datos)
            throws ReglaNegocioException {
        if (!estado.admiteReservas()) {
            throw new CupoInsuficienteException("La salida " + id + " esta " + estado + " y no admite reservas.");
        }
        if (datos.isEmpty()) {
            throw new ReglaNegocioException("La reserva debe tener al menos un pasajero.");
        }
        if (lugaresDisponibles() < datos.size()) {
            throw new CupoInsuficienteException("No hay lugares suficientes: quedan " + lugaresDisponibles()
                    + " y se pidieron " + datos.size() + ".");
        }
        Reserva reserva = new Reserva(numero, hoy, titular, vendedor, this);
        for (DatosPasajero d : datos) {
            reserva.agregarPasajero(d);
        }
        // solo se agrega si todos los pasajeros fueron validos
        reservas.add(reserva);
        actualizarEstado();
        return reserva;
    }

    public void verificarLugarPara(int cantidad) throws ReglaNegocioException {
        if (!estado.admiteReservas()) {
            throw new CupoInsuficienteException("La salida " + id + " esta " + estado + " y no admite pasajeros nuevos.");
        }
        if (lugaresDisponibles() < cantidad) {
            throw new CupoInsuficienteException("No hay lugares suficientes en la salida " + id + ".");
        }
    }

    public int lugaresOcupados() {
        int ocupados = 0;
        for (Reserva r : reservas) {
            if (r.ocupaLugar()) {
                ocupados += r.cantidadLugares();
            }
        }
        return ocupados;
    }

    public int lugaresDisponibles() {
        return cupoMaximo - lugaresOcupados();
    }

    public void liberarLugares(Reserva reserva) {
        actualizarEstado();
    }

    // RN03 y RN04: confirmada al llegar al minimo, completa al llegar al maximo
    public void actualizarEstado() {
        if (estado != EstadoSalida.PROGRAMADA && estado != EstadoSalida.CONFIRMADA && estado != EstadoSalida.COMPLETA) {
            return;
        }
        int ocupados = lugaresOcupados();
        if (ocupados >= cupoMaximo) {
            estado = EstadoSalida.COMPLETA;
        } else if (ocupados >= tour.getCupoMinimo()) {
            estado = EstadoSalida.CONFIRMADA;
        } else {
            estado = EstadoSalida.PROGRAMADA;
        }
    }

    public long diasHasta(LocalDate hoy) {
        return fechaInicio.toEpochDay() - hoy.toEpochDay();
    }

    public boolean seSuperponeCon(LocalDate desde, LocalDate hasta) {
        return !fechaFin.isBefore(desde) && !hasta.isBefore(fechaInicio);
    }

    // una salida cancelada o finalizada ya no ocupa al guia
    public boolean estaVigente() {
        return estado != EstadoSalida.CANCELADA && estado != EstadoSalida.FINALIZADA;
    }

    public boolean faltaCupoMinimo() {
        return lugaresOcupados() < tour.getCupoMinimo();
    }

    public void asignarGuia(Guia nuevoGuia, List<Salida> todas) throws AsignacionInvalidaException {
        if (!estaVigente() || estado == EstadoSalida.EN_CURSO) {
            throw new AsignacionInvalidaException("No se puede asignar guia a una salida " + estado + ".");
        }
        if (!nuevoGuia.puedeGuiar(tour)) {
            throw new AsignacionInvalidaException(nuevoGuia.getNombreCompleto() + " no esta habilitado para tours de "
                    + tour.getCategoria() + ".");
        }
        List<Salida> otras = new ArrayList<>(todas);
        otras.remove(this);
        if (!nuevoGuia.estaDisponible(fechaInicio, fechaFin, otras)) {
            throw new AsignacionInvalidaException(nuevoGuia.getNombreCompleto() + " ya tiene otra salida en esas fechas.");
        }
        this.guia = nuevoGuia;
    }

    public void asignarVehiculo(Vehiculo nuevoVehiculo) throws AsignacionInvalidaException {
        if (!estaVigente() || estado == EstadoSalida.EN_CURSO) {
            throw new AsignacionInvalidaException("No se puede asignar vehiculo a una salida " + estado + ".");
        }
        if (nuevoVehiculo.getCapacidad() < cupoMaximo) {
            throw new AsignacionInvalidaException("El vehiculo tiene " + nuevoVehiculo.getCapacidad()
                    + " lugares y la salida tiene cupo " + cupoMaximo + ".");
        }
        this.vehiculo = nuevoVehiculo;
    }

    // lista ordenada por apellido de los pasajeros con reserva confirmada (RN14)
    public List<Pasajero> emitirListaPasajeros() throws ReglaNegocioException {
        if (!estaVigente()) {
            throw new EstadoInvalidoException("La salida " + id + " esta " + estado + ".");
        }
        List<Pasajero> lista = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.getEstado() == EstadoReserva.CONFIRMADA) {
                lista.addAll(r.getPasajeros());
            }
        }
        if (lista.isEmpty()) {
            throw new ReglaNegocioException("La salida " + id + " no tiene reservas confirmadas.");
        }
        Collections.sort(lista);
        listaEmitida = true;
        return lista;
    }

    public void iniciar() throws ReglaNegocioException {
        if (!estado.puedePasarA(EstadoSalida.EN_CURSO) || estado == EstadoSalida.PROGRAMADA) {
            throw new EstadoInvalidoException("Solo se inicia una salida confirmada o completa. Estado actual: " + estado);
        }
        if (guia == null) {
            throw new ReglaNegocioException("La salida " + id + " no tiene guia asignado.");
        }
        if (!listaEmitida) {
            throw new ReglaNegocioException("Primero hay que emitir la lista de pasajeros.");
        }
        estado = EstadoSalida.EN_CURSO;
    }

    // Creador: al finalizar, la salida genera la liquidacion del guia (RN15)
    public Liquidacion finalizar(LocalDate hoy) throws ReglaNegocioException {
        if (estado != EstadoSalida.EN_CURSO) {
            throw new EstadoInvalidoException("Solo se finaliza una salida en curso. Estado actual: " + estado);
        }
        if (liquidacion != null) {
            throw new ReglaNegocioException("La salida " + id + " ya fue liquidada.");
        }
        estado = EstadoSalida.FINALIZADA;
        for (Reserva r : reservas) {
            r.finalizar();
        }
        int dias = tour.getDuracionDias();
        double fijo = dias * guia.getHonorarioDiario();
        double variable = Math.round(ventasAdicionales() * PORCENTAJE_GUIA_ADICIONALES * 100) / 100.0;
        liquidacion = new Liquidacion(hoy, guia, dias, fijo, variable);
        return liquidacion;
    }

    // cancelacion por parte de la agencia: se devuelve segun la politica recibida
    public List<Cancelacion> cancelar(String motivo, LocalDate hoy, PoliticaCancelacion politica)
            throws EstadoInvalidoException {
        if (!estado.puedePasarA(EstadoSalida.CANCELADA)) {
            throw new EstadoInvalidoException("Una salida " + estado + " no se puede cancelar.");
        }
        List<Cancelacion> cancelaciones = new ArrayList<>();
        for (Reserva r : reservas) {
            if (r.estaActiva()) {
                cancelaciones.add(r.cancelar(motivo, hoy, politica));
            }
        }
        estado = EstadoSalida.CANCELADA;
        return cancelaciones;
    }

    public double ventasAdicionales() {
        double total = 0;
        for (Reserva r : reservas) {
            if (r.ocupaLugar()) {
                total += r.totalAdicionales();
            }
        }
        return total;
    }

    public int getId() {
        return id;
    }

    public Tour getTour() {
        return tour;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public EstadoSalida getEstado() {
        return estado;
    }

    public boolean isListaEmitida() {
        return listaEmitida;
    }

    public Guia getGuia() {
        return guia;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public List<Reserva> getReservas() {
        return new ArrayList<>(reservas);
    }

    public Liquidacion getLiquidacion() {
        return liquidacion;
    }

    // orden natural por fecha de inicio
    @Override
    public int compareTo(Salida otra) {
        return this.fechaInicio.compareTo(otra.fechaInicio);
    }

    @Override
    public String toString() {
        String texto = "Salida " + id + " - " + tour.getNombre() + " - " + fechaInicio + " al " + fechaFin + " - "
                + estado + " - " + lugaresOcupados() + "/" + cupoMaximo + " lugares";
        if (guia != null) {
            texto += " - guia " + guia.getNombreCompleto();
        }
        if (vehiculo != null) {
            texto += " - vehiculo " + vehiculo.getPatente();
        }
        return texto;
    }
}
