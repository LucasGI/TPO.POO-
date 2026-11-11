package vista;

import excepcion.ReglaNegocioException;
import persistencia.ArchivoDatos;
import persistencia.BaseDatos;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

// estructura comun de las pestanias: botones a la izquierda y resultados a la derecha
public abstract class PanelBase extends JPanel implements ActionListener {
    protected JFrame ventana;
    protected JTextArea areaResultado;
    private JPanel pnlBotones;
    private ArchivoDatos archivo;
    private BaseDatos base;

    public PanelBase(JFrame ventana, ArchivoDatos archivo, BaseDatos base) {
        this.ventana = ventana;
        this.archivo = archivo;
        this.base = base;
        setLayout(new BorderLayout(10, 10));
        pnlBotones = new JPanel(new GridLayout(0, 1, 4, 4));
        JPanel pnlIzquierda = new JPanel(new BorderLayout());
        pnlIzquierda.add(pnlBotones, BorderLayout.NORTH);
        add(pnlIzquierda, BorderLayout.WEST);
        areaResultado = new JTextArea();
        areaResultado.setEditable(false);
        areaResultado.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        add(new JScrollPane(areaResultado), BorderLayout.CENTER);
    }

    protected JButton agregarBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.addActionListener(this);
        pnlBotones.add(boton);
        return boton;
    }

    // cada pestania decide que hacer segun el boton que se apreto
    protected abstract void procesar(Object fuente) throws ReglaNegocioException, IOException;

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            procesar(e.getSource());
        } catch (ReglaNegocioException ex) {
            mostrarError(ex.getMessage());
        } catch (IllegalArgumentException ex) {
            mostrarError("Dato invalido: " + ex.getMessage());
        } catch (IOException ex) {
            mostrarError("No se pudo escribir el archivo: " + ex.getMessage());
        } finally {
            // se guarda despues de cada operacion, salga bien o mal
            guardar();
        }
    }

    protected void mostrar(List<String> lineas) {
        areaResultado.setText("");
        for (String linea : lineas) {
            areaResultado.append(linea + "\n");
        }
        areaResultado.setCaretPosition(0);
    }

    protected void mostrarTexto(String texto) {
        areaResultado.setText(texto);
        areaResultado.setCaretPosition(0);
    }

    protected void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(ventana, mensaje, "Operacion realizada", JOptionPane.INFORMATION_MESSAGE);
    }

    protected void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(ventana, mensaje, "No se pudo realizar", JOptionPane.ERROR_MESSAGE);
    }

    protected boolean confirmar(String pregunta) {
        return JOptionPane.showConfirmDialog(ventana, pregunta, "Confirmar", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION;
    }

    protected void guardar() {
        try {
            archivo.guardar(base);
        } catch (IOException e) {
            mostrarError("No se pudieron guardar los datos: " + e.getMessage());
        }
    }
}
