package vista;

import controlador.ReporteController;
import persistencia.ArchivoDatos;
import persistencia.BaseDatos;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.io.IOException;
import java.util.List;

public class PanelReportes extends PanelBase {
    private ReporteController reportes;

    private JButton btnOcupacion;
    private JButton btnVentas;
    private JButton btnIngresos;
    private JButton btnCancelaciones;
    private JButton btnLiquidaciones;

    public PanelReportes(JFrame ventana, ArchivoDatos archivo, BaseDatos base, ReporteController reportes) {
        super(ventana, archivo, base);
        this.reportes = reportes;
        btnOcupacion = agregarBoton("Ocupacion por salida");
        btnVentas = agregarBoton("Ventas por vendedor");
        btnIngresos = agregarBoton("Ingresos por tour");
        btnCancelaciones = agregarBoton("Cancelaciones");
        btnLiquidaciones = agregarBoton("Liquidaciones de guias");
        mostrarTexto("Elija un reporte. Cada reporte tambien se guarda en data/reportes.");
    }

    @Override
    protected void procesar(Object fuente) throws IOException {
        if (fuente == btnOcupacion) {
            mostrarYExportar(reportes.ocupacionPorSalida(), "ocupacion.txt");
        } else if (fuente == btnVentas) {
            mostrarYExportar(reportes.ventasPorVendedor(), "ventas-por-vendedor.txt");
        } else if (fuente == btnIngresos) {
            mostrarYExportar(reportes.ingresosPorTour(), "ingresos-por-tour.txt");
        } else if (fuente == btnCancelaciones) {
            mostrarYExportar(reportes.cancelaciones(), "cancelaciones.txt");
        } else if (fuente == btnLiquidaciones) {
            mostrarYExportar(reportes.liquidaciones(), "liquidaciones.txt");
        }
    }

    private void mostrarYExportar(List<String> lineas, String archivo) throws IOException {
        String ruta = reportes.exportar(archivo, lineas);
        lineas.add("");
        lineas.add("Reporte guardado en " + ruta);
        mostrar(lineas);
    }
}
