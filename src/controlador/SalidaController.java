package controlador;

import excepcion.ReglaNegocioException;
import modelo.Cancelacion;
import modelo.Empleado;
import modelo.EstadoReserva;
import modelo.EstadoSalida;
import modelo.Guia;
import modelo.Liquidacion;
import modelo.Pasajero;
import modelo.Reserva;
import modelo.Salida;
import modelo.Tour;
import modelo.Vehiculo;
import persistencia.ArchivoTexto;
import persistencia.Repositorio;
import servicio.PoliticaCancelacionAgencia;
import servicio.PoliticaCancelacionCliente;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// coordina la operacion de las salidas: programacion, asignaciones, inicio y cierre
public class SalidaController {
    private Repositorio<Salida, Integer> salidas;
    private Repositorio<Tour, String> tours;
    private Repositorio<Empleado, Integer> empleados;
    private Repositorio<Vehiculo, String> vehiculos;
    private Repositorio<Reserva, Integer> reservas;
    private ArchivoTexto archivoListas;

    public SalidaController(Repositorio<Salida, Integer> salidas, Repositorio<Tour, String> tours,
                            Repositorio<Empleado, Integer> empleados, Repositorio<Vehiculo, String> vehiculos,
                            Repositorio<Reserva, Integer> reservas, ArchivoTexto archivoListas) {
        this.salidas = salidas;
        this.tours = tours;
        this.empleados = empleados;
        this.vehiculos = vehiculos;
        this.reservas = reservas;
        this.archivoListas = archivoListas;
    }

    // CU03 Programar salida
    public Salida programarSalida(String codigoTour, LocalDate fechaInicio, int cupo, LocalDate hoy)
            throws ReglaNegocioException {
        Tour tour = tours.buscarPorId(codigoTour.toUpperCase());
        if (tour == null) {
            throw new ReglaNegocioException("No existe el tour " + codigoTour + ".");
        }
        if (!fechaInicio.isAfter(hoy)) {
            throw new ReglaNegocioException("La fecha de la salida tiene que ser posterior a hoy.");
        }
        Salida salida = tour.crearSalida(salidas.cantidad() + 1, fechaInicio, cupo);
        salidas.guardar(salida);
        return salida;
    }

    // CU11 Asignar guia
    public Salida asignarGuia(int idSalida, int legajo) throws ReglaNegocioException {
        Salida salida = buscarSalida(idSalida);
        Empleado empleado = empleados.buscarPorId(legajo);
        if (!(empleado instanceof Guia)) {
            throw new ReglaNegocioException("El legajo " + legajo + " no corresponde a un guia.");
        }
        salida.asignarGuia((Guia) empleado, salidas.listar());
        salidas.guardar(salida);
        return salida;
    }

    // CU11 Asignar vehiculo
    public Salida asignarVehiculo(int idSalida, String patente) throws ReglaNegocioException {
        Salida salida = buscarSalida(idSalida);
        Vehiculo vehiculo = vehiculos.buscarPorId(patente.toUpperCase());
        if (vehiculo == null) {
            throw new ReglaNegocioException("No existe el vehiculo " + patente + ".");
        }
        salida.asignarVehiculo(vehiculo);
        salidas.guardar(salida);
        return salida;
    }

    // CU12 Emitir lista de pasajeros, tambien se exporta a un archivo de texto
    public List<String> emitirListaPasajeros(int idSalida) throws ReglaNegocioException, IOException {
        Salida salida = buscarSalida(idSalida);
        List<Pasajero> lista = salida.emitirListaPasajeros();
        List<String> lineas = new ArrayList<>();
        lineas.add("Lista de pasajeros - " + salida.getTour().getNombre() + " - salida " + salida.getId()
                + " del " + salida.getFechaInicio());
        if (salida.getGuia() != null) {
            lineas.add("Guia: " + salida.getGuia().getNombreCompleto());
        }
        lineas.add("");
        int i = 1;
        for (Pasajero p : lista) {
            String linea = i + ". " + p.getApellido() + ", " + p.getNombre() + " - doc. " + p.getDocumento()
                    + " - emergencia: " + p.getContactoEmergencia();
            if (p.getRestricciones() != null && !p.getRestricciones().isBlank()) {
                linea += " - restricciones: " + p.getRestricciones();
            }
            lineas.add(linea);
            i++;
        }
        lineas.add("");
        lineas.add("Total: " + lista.size() + " pasajeros");
        salidas.guardar(salida);
        String ruta = archivoListas.escribir("lista-salida-" + salida.getId() + ".txt", lineas);
        lineas.add("Archivo generado: " + ruta);
        return lineas;
    }

    // CU13 Iniciar salida
    public Salida iniciarSalida(int idSalida) throws ReglaNegocioException {
        Salida salida = buscarSalida(idSalida);
        salida.iniciar();
        salidas.guardar(salida);
        return salida;
    }

    // CU14 y CU15 Finalizar la salida y liquidar al guia
    public Liquidacion cerrarSalida(int idSalida, LocalDate hoy) throws ReglaNegocioException {
        Salida salida = buscarSalida(idSalida);
        Liquidacion liquidacion = salida.finalizar(hoy);
        salidas.guardar(salida);
        for (Reserva r : salida.getReservas()) {
            reservas.guardar(r);
        }
        return liquidacion;
    }

    public List<Cancelacion> cancelarSalida(int idSalida, String motivo, LocalDate hoy) throws ReglaNegocioException {
        Salida salida = buscarSalida(idSalida);
        List<Cancelacion> cancelaciones = salida.cancelar(motivo, hoy, new PoliticaCancelacionAgencia());
        guardarConReservas(salida);
        return cancelaciones;
    }

    // CU17 Controlar vencimientos: cupo minimo, saldo impago y senas no pagadas
    public List<String> controlarVencimientos(LocalDate hoy) {
        List<String> novedades = new ArrayList<>();
        for (Salida s : salidas.listar()) {
            if (s.getEstado() == EstadoSalida.PROGRAMADA && s.diasHasta(hoy) <= Salida.DIAS_CONTROL_CUPO_MINIMO
                    && s.faltaCupoMinimo()) {
                try {
                    s.cancelar("No se alcanzo el cupo minimo", hoy, new PoliticaCancelacionAgencia());
                    guardarConReservas(s);
                    novedades.add("Salida " + s.getId() + " cancelada por no alcanzar el cupo minimo.");
                } catch (ReglaNegocioException e) {
                    novedades.add("No se pudo cancelar la salida " + s.getId() + ": " + e.getMessage());
                }
            }
        }
        for (Reserva r : reservas.listar()) {
            Salida s = r.getSalida();
            if (!r.estaActiva() || !s.estaVigente() || s.getEstado() == EstadoSalida.EN_CURSO) {
                continue;
            }
            String motivo = null;
            long diasSalida = s.diasHasta(hoy);
            long diasDesdeAlta = hoy.toEpochDay() - r.getFecha().toEpochDay();
            if (r.saldo() > 0 && diasSalida <= Reserva.DIAS_VENCIMIENTO_SALDO) {
                motivo = "Saldo impago al vencimiento";
            } else if (r.getEstado() == EstadoReserva.PENDIENTE && diasDesdeAlta >= 2) {
                motivo = "Sena no pagada en 48 horas";
            }
            if (motivo != null) {
                try {
                    r.cancelar(motivo, hoy, new PoliticaCancelacionCliente());
                    reservas.guardar(r);
                    salidas.guardar(s);
                    novedades.add("Reserva " + r.getNumero() + " anulada: " + motivo + ".");
                } catch (ReglaNegocioException e) {
                    novedades.add("No se pudo anular la reserva " + r.getNumero() + ": " + e.getMessage());
                }
            }
        }
        return novedades;
    }

    public List<Salida> listarSalidas() {
        List<Salida> lista = salidas.listar();
        Collections.sort(lista);
        return lista;
    }

    public Salida buscarSalida(int id) throws ReglaNegocioException {
        Salida salida = salidas.buscarPorId(id);
        if (salida == null) {
            throw new ReglaNegocioException("No existe la salida " + id + ".");
        }
        return salida;
    }

    private void guardarConReservas(Salida salida) {
        salidas.guardar(salida);
        for (Reserva r : salida.getReservas()) {
            reservas.guardar(r);
        }
    }
}
