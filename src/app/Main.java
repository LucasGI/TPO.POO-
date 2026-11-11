package app;

import controlador.CatalogoController;
import controlador.ReporteController;
import controlador.ReservaController;
import controlador.SalidaController;
import excepcion.ReglaNegocioException;
import persistencia.ArchivoDatos;
import persistencia.ArchivoTexto;
import persistencia.BaseDatos;
import servicio.CalculadoraPrecio;
import servicio.Promocion;
import servicio.PromocionAnticipada;
import servicio.PromocionGrupo;
import vista.VentanaPrincipal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// arma todos los objetos y los conecta; las rutas son relativas a la carpeta del proyecto
public class Main {

    public static void main(String[] args) {
        // mensajes que se muestran cuando abre la ventana
        List<String> avisos = new ArrayList<>();
        ArchivoDatos archivo = new ArchivoDatos("data/rumbosur.dat");
        LocalDate hoy = LocalDate.now();

        BaseDatos base = null;
        boolean primeraVez = false;
        if (archivo.existe()) {
            try {
                base = archivo.cargar();
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "No se pudo leer " + archivo.getRuta() + ": " + e.getMessage()
                        + "\nBorre el archivo para empezar con los datos de prueba.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            base = new BaseDatos();
            primeraVez = true;
        }

        // las promociones se pueden ampliar agregando clases nuevas
        List<Promocion> promociones = new ArrayList<>();
        promociones.add(new PromocionAnticipada());
        promociones.add(new PromocionGrupo());
        CalculadoraPrecio calculadora = new CalculadoraPrecio(promociones);

        CatalogoController catalogo = new CatalogoController(base.getTours(), base.getClientes(),
                base.getEmpleados(), base.getVehiculos(), base.getProveedores());
        ReservaController reservas = new ReservaController(base.getReservas(), base.getSalidas(),
                base.getClientes(), base.getEmpleados(), calculadora);
        SalidaController salidas = new SalidaController(base.getSalidas(), base.getTours(), base.getEmpleados(),
                base.getVehiculos(), base.getReservas(), new ArchivoTexto("data/listas"));
        ReporteController reportes = new ReporteController(base.getSalidas(), base.getReservas(),
                base.getEmpleados(), base.getTours(), new ArchivoTexto("data/reportes"));

        if (primeraVez) {
            try {
                new DatosDePrueba(catalogo, salidas, reservas).cargar(hoy);
                avisos.add("Primera ejecucion: se cargaron los datos de prueba.");
            } catch (ReglaNegocioException e) {
                avisos.add("Fallo la carga de datos de prueba: " + e.getMessage());
            }
        }

        // al iniciar se procesan los vencimientos (S11)
        avisos.addAll(salidas.controlarVencimientos(hoy));

        try {
            archivo.guardar(base);
        } catch (IOException e) {
            avisos.add("No se pudieron guardar los datos: " + e.getMessage());
        }

        // la interfaz se crea en el hilo de eventos de Swing
        final BaseDatos datos = base;
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(archivo, datos, catalogo, reservas, salidas, reportes);
            ventana.setVisible(true);
            if (!avisos.isEmpty()) {
                String texto = "";
                for (String aviso : avisos) {
                    texto += aviso + "\n";
                }
                JOptionPane.showMessageDialog(ventana, texto, "Avisos", JOptionPane.INFORMATION_MESSAGE);
            }
        });
    }
}
