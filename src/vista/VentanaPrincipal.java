package vista;

import controlador.CatalogoController;
import controlador.ReporteController;
import controlador.ReservaController;
import controlador.SalidaController;
import persistencia.ArchivoDatos;
import persistencia.BaseDatos;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class VentanaPrincipal extends JFrame {
    private PanelReservas pnlReservas;

    public VentanaPrincipal(ArchivoDatos archivo, BaseDatos base, CatalogoController catalogo,
                            ReservaController reservas, SalidaController salidas, ReporteController reportes) {
        super("Rumbo Sur - Agencia de turismo");
        PanelCatalogo pnlCatalogo = new PanelCatalogo(this, archivo, base, catalogo);
        pnlReservas = new PanelReservas(this, archivo, base, reservas, salidas, catalogo, pnlCatalogo);
        PanelSalidas pnlSalidas = new PanelSalidas(this, archivo, base, salidas, catalogo);
        PanelReportes pnlReportes = new PanelReportes(this, archivo, base, reportes);

        JTabbedPane pestanias = new JTabbedPane();
        pestanias.addTab("Reservas y pagos", pnlReservas);
        pestanias.addTab("Salidas", pnlSalidas);
        pestanias.addTab("Catalogo y personal", pnlCatalogo);
        pestanias.addTab("Reportes", pnlReportes);

        setLayout(new BorderLayout());
        add(pestanias, BorderLayout.CENTER);
        add(new JLabel(" Datos en " + archivo.getRuta()), BorderLayout.SOUTH);

        setSize(1150, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // al cerrar la ventana se guarda por ultima vez
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    archivo.guardar(base);
                } catch (IOException ex) {
                    System.out.println("No se pudieron guardar los datos: " + ex.getMessage());
                }
            }
        });
    }
}
