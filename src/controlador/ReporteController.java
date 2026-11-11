package controlador;

import modelo.Empleado;
import modelo.Reserva;
import modelo.Salida;
import modelo.Tour;
import modelo.Vendedor;
import persistencia.ArchivoTexto;
import persistencia.Repositorio;
import servicio.ComparadorCancelacionPorFecha;
import servicio.ComparadorPorValor;
import servicio.LineaReporte;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// CU16 Emitir reportes
public class ReporteController {
    private Repositorio<Salida, Integer> salidas;
    private Repositorio<Reserva, Integer> reservas;
    private Repositorio<Empleado, Integer> empleados;
    private Repositorio<Tour, String> tours;
    private ArchivoTexto archivoReportes;

    public ReporteController(Repositorio<Salida, Integer> salidas, Repositorio<Reserva, Integer> reservas,
                             Repositorio<Empleado, Integer> empleados, Repositorio<Tour, String> tours,
                             ArchivoTexto archivoReportes) {
        this.salidas = salidas;
        this.reservas = reservas;
        this.empleados = empleados;
        this.tours = tours;
        this.archivoReportes = archivoReportes;
    }

    // usa el orden natural de Salida (por fecha)
    public List<String> ocupacionPorSalida() {
        List<Salida> lista = salidas.listar();
        Collections.sort(lista);
        List<String> lineas = new ArrayList<>();
        lineas.add("OCUPACION POR SALIDA");
        for (Salida s : lista) {
            int porcentaje = s.lugaresOcupados() * 100 / s.getCupoMaximo();
            lineas.add(s.getFechaInicio() + " | salida " + s.getId() + " | " + s.getTour().getNombre() + " | "
                    + s.getEstado() + " | " + s.lugaresOcupados() + "/" + s.getCupoMaximo() + " (" + porcentaje + "%)");
        }
        return lineas;
    }

    // ventas de reservas no anuladas, de mayor a menor (Comparator)
    public List<String> ventasPorVendedor() {
        List<LineaReporte> filas = new ArrayList<>();
        for (Empleado e : empleados.listar()) {
            if (e instanceof Vendedor) {
                Vendedor v = (Vendedor) e;
                double total = 0;
                int cantidad = 0;
                for (Reserva r : reservas.listar()) {
                    if (r.getVendedor() == v && r.ocupaLugar()) {
                        total += r.getTotal();
                        cantidad++;
                    }
                }
                double comision = Math.round(v.calcularComision(total) * 100) / 100.0;
                filas.add(new LineaReporte(v.getNombreCompleto() + " | " + cantidad + " reservas | $" + total
                        + " | comision $" + comision, total));
            }
        }
        Collections.sort(filas, new ComparadorPorValor());
        return armarLineas("VENTAS POR VENDEDOR", filas);
    }

    // ingresos cobrados menos devoluciones, por tour
    public List<String> ingresosPorTour() {
        List<LineaReporte> filas = new ArrayList<>();
        for (Tour t : tours.listar()) {
            double cobrado = 0;
            double devuelto = 0;
            for (Reserva r : reservas.listar()) {
                if (r.getSalida().getTour() == t) {
                    cobrado += r.totalCobrado();
                    if (r.getCancelacion() != null) {
                        devuelto += r.getCancelacion().getImporteDevuelto();
                    }
                }
            }
            double neto = Math.round((cobrado - devuelto) * 100) / 100.0;
            filas.add(new LineaReporte(t.getCodigo() + " " + t.getNombre() + " | cobrado $" + cobrado
                    + " | devuelto $" + devuelto + " | neto $" + neto, neto));
        }
        Collections.sort(filas, new ComparadorPorValor());
        return armarLineas("INGRESOS POR TOUR", filas);
    }

    public List<String> cancelaciones() {
        List<Reserva> canceladas = new ArrayList<>();
        for (Reserva r : reservas.listar()) {
            if (r.getCancelacion() != null) {
                canceladas.add(r);
            }
        }
        Collections.sort(canceladas, new ComparadorCancelacionPorFecha());
        List<String> lineas = new ArrayList<>();
        lineas.add("CANCELACIONES");
        for (Reserva r : canceladas) {
            lineas.add("Reserva " + r.getNumero() + " | " + r.getSalida().getTour().getNombre() + " | "
                    + r.getCancelacion());
        }
        if (canceladas.isEmpty()) {
            lineas.add("No hay cancelaciones registradas.");
        }
        return lineas;
    }

    public List<String> liquidaciones() {
        List<Salida> lista = salidas.listar();
        Collections.sort(lista);
        List<String> lineas = new ArrayList<>();
        lineas.add("LIQUIDACIONES DE GUIAS");
        for (Salida s : lista) {
            if (s.getLiquidacion() != null) {
                lineas.add("Salida " + s.getId() + " | " + s.getLiquidacion());
            }
        }
        if (lineas.size() == 1) {
            lineas.add("Todavia no hay salidas liquidadas.");
        }
        return lineas;
    }

    public String exportar(String nombreArchivo, List<String> lineas) throws IOException {
        return archivoReportes.escribir(nombreArchivo, lineas);
    }

    private List<String> armarLineas(String titulo, List<LineaReporte> filas) {
        List<String> lineas = new ArrayList<>();
        lineas.add(titulo);
        for (LineaReporte f : filas) {
            lineas.add(f.getTexto());
        }
        return lineas;
    }
}
