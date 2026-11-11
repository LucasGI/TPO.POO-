package vista;

import controlador.CatalogoController;
import controlador.SalidaController;
import excepcion.ReglaNegocioException;
import modelo.Cancelacion;
import modelo.Empleado;
import modelo.Guia;
import modelo.Liquidacion;
import modelo.Salida;
import modelo.Tour;
import modelo.Vehiculo;
import persistencia.ArchivoDatos;
import persistencia.BaseDatos;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PanelSalidas extends PanelBase {
    private SalidaController salidas;
    private CatalogoController catalogo;

    private JButton btnVer;
    private JButton btnProgramar;
    private JButton btnGuia;
    private JButton btnVehiculo;
    private JButton btnLista;
    private JButton btnIniciar;
    private JButton btnFinalizar;
    private JButton btnCancelar;
    private JButton btnVencimientos;

    public PanelSalidas(JFrame ventana, ArchivoDatos archivo, BaseDatos base, SalidaController salidas,
                        CatalogoController catalogo) {
        super(ventana, archivo, base);
        this.salidas = salidas;
        this.catalogo = catalogo;
        btnVer = agregarBoton("Ver salidas");
        btnProgramar = agregarBoton("Programar salida");
        btnGuia = agregarBoton("Asignar guia");
        btnVehiculo = agregarBoton("Asignar vehiculo");
        btnLista = agregarBoton("Emitir lista de pasajeros");
        btnIniciar = agregarBoton("Iniciar salida");
        btnFinalizar = agregarBoton("Finalizar y liquidar");
        btnCancelar = agregarBoton("Cancelar salida");
        btnVencimientos = agregarBoton("Controlar vencimientos");
        verSalidas();
    }

    @Override
    protected void procesar(Object fuente) throws ReglaNegocioException, IOException {
        if (fuente == btnVer) {
            verSalidas();
        } else if (fuente == btnProgramar) {
            programarSalida();
        } else if (fuente == btnGuia) {
            asignarGuia();
        } else if (fuente == btnVehiculo) {
            asignarVehiculo();
        } else if (fuente == btnLista) {
            Salida s = elegirSalida("Emitir lista de pasajeros");
            if (s != null) {
                mostrar(salidas.emitirListaPasajeros(s.getId()));
            }
        } else if (fuente == btnIniciar) {
            Salida s = elegirSalida("Iniciar salida");
            if (s != null) {
                salidas.iniciarSalida(s.getId());
                verSalidas();
                mostrarExito("Salida " + s.getId() + " en curso.");
            }
        } else if (fuente == btnFinalizar) {
            Salida s = elegirSalida("Finalizar salida");
            if (s != null) {
                Liquidacion l = salidas.cerrarSalida(s.getId(), LocalDate.now());
                verSalidas();
                mostrarExito("Salida finalizada.\nLiquidacion: " + l);
            }
        } else if (fuente == btnCancelar) {
            cancelarSalida();
        } else if (fuente == btnVencimientos) {
            List<String> novedades = salidas.controlarVencimientos(LocalDate.now());
            if (novedades.isEmpty()) {
                novedades.add("No hay vencimientos para procesar.");
            }
            mostrar(novedades);
        }
    }

    private void verSalidas() {
        List<String> lineas = new ArrayList<>();
        for (Salida s : salidas.listarSalidas()) {
            lineas.add(s.toString());
        }
        mostrar(lineas);
    }

    // CU03 Programar salida
    private void programarSalida() throws ReglaNegocioException {
        List<Tour> tours = catalogo.listarTours();
        String[] opciones = new String[tours.size()];
        for (int i = 0; i < tours.size(); i++) {
            Tour t = tours.get(i);
            opciones[i] = t.getCodigo() + " - " + t.getNombre() + " (cupo " + t.getCupoMinimo() + "-" + t.getCupoMaximo() + ")";
        }
        Formulario f = new Formulario(ventana, "Programar salida");
        f.agregarOpciones("Tour", opciones);
        f.agregarTexto("Fecha de inicio (aaaa-mm-dd)");
        f.agregarTexto("Cupo maximo");
        if (!f.mostrar() || tours.isEmpty()) {
            return;
        }
        Salida s = salidas.programarSalida(tours.get(f.opcion("Tour")).getCodigo(),
                f.fecha("Fecha de inicio (aaaa-mm-dd)"), f.entero("Cupo maximo"), LocalDate.now());
        verSalidas();
        mostrarExito("Programada: " + s);
    }

    // CU11 Asignar guia y vehiculo
    private void asignarGuia() throws ReglaNegocioException {
        List<Guia> guias = new ArrayList<>();
        for (Empleado e : catalogo.listarEmpleados()) {
            if (e instanceof Guia) {
                guias.add((Guia) e);
            }
        }
        String[] opciones = new String[guias.size()];
        for (int i = 0; i < guias.size(); i++) {
            opciones[i] = guias.get(i).getLegajo() + " - " + guias.get(i).getNombreCompleto() + " " + guias.get(i).getCategorias();
        }
        Formulario f = new Formulario(ventana, "Asignar guia");
        f.agregarTexto("Numero de salida");
        f.agregarOpciones("Guia", opciones);
        if (!f.mostrar() || guias.isEmpty()) {
            return;
        }
        Salida s = salidas.asignarGuia(f.entero("Numero de salida"), guias.get(f.opcion("Guia")).getLegajo());
        verSalidas();
        mostrarExito("Guia asignado: " + s.getGuia().getNombreCompleto());
    }

    private void asignarVehiculo() throws ReglaNegocioException {
        List<Vehiculo> vehiculos = catalogo.listarVehiculos();
        String[] opciones = new String[vehiculos.size()];
        for (int i = 0; i < vehiculos.size(); i++) {
            opciones[i] = vehiculos.get(i).toString();
        }
        Formulario f = new Formulario(ventana, "Asignar vehiculo");
        f.agregarTexto("Numero de salida");
        f.agregarOpciones("Vehiculo", opciones);
        if (!f.mostrar() || vehiculos.isEmpty()) {
            return;
        }
        Salida s = salidas.asignarVehiculo(f.entero("Numero de salida"), vehiculos.get(f.opcion("Vehiculo")).getPatente());
        verSalidas();
        mostrarExito("Vehiculo asignado: " + s.getVehiculo());
    }

    private void cancelarSalida() throws ReglaNegocioException {
        Formulario f = new Formulario(ventana, "Cancelar salida");
        f.agregarTexto("Numero de salida");
        f.agregarTexto("Motivo");
        if (!f.mostrar()) {
            return;
        }
        int id = f.entero("Numero de salida");
        String motivo = f.textoObligatorio("Motivo");
        if (!confirmar("Se anulan todas las reservas de la salida " + id + " con devolucion del 100%. Continuar?")) {
            return;
        }
        List<Cancelacion> cancelaciones = salidas.cancelarSalida(id, motivo, LocalDate.now());
        List<String> lineas = new ArrayList<>();
        lineas.add("Salida " + id + " cancelada. Reservas anuladas: " + cancelaciones.size());
        for (Cancelacion c : cancelaciones) {
            lineas.add("  " + c);
        }
        mostrar(lineas);
    }

    private Salida elegirSalida(String titulo) throws ReglaNegocioException {
        Formulario f = new Formulario(ventana, titulo);
        f.agregarTexto("Numero de salida");
        if (!f.mostrar()) {
            return null;
        }
        return salidas.buscarSalida(f.entero("Numero de salida"));
    }
}
