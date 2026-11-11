package vista;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// ventana emergente para cargar datos: se arma con etiquetas y campos y se lee al aceptar
public class Formulario extends JDialog {
    private List<String> etiquetas;
    private List<JComponent> campos;
    private JPanel pnlCampos;
    private boolean aceptado;

    public Formulario(JFrame duenio, String titulo) {
        super(duenio, titulo, true);
        etiquetas = new ArrayList<>();
        campos = new ArrayList<>();
        pnlCampos = new JPanel(new GridLayout(0, 2, 8, 6));
        setLayout(new BorderLayout(10, 10));
        add(pnlCampos, BorderLayout.CENTER);

        JButton btnAceptar = new JButton("Aceptar");
        JButton btnCancelar = new JButton("Cancelar");
        btnAceptar.addActionListener(e -> {
            aceptado = true;
            dispose();
        });
        btnCancelar.addActionListener(e -> dispose());
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBotones.add(btnCancelar);
        pnlBotones.add(btnAceptar);
        add(pnlBotones, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnAceptar);
    }

    public void agregarTexto(String etiqueta) {
        agregar(etiqueta, new JTextField(20));
    }

    public void agregarTexto(String etiqueta, String valorInicial) {
        agregar(etiqueta, new JTextField(valorInicial, 20));
    }

    public void agregarOpciones(String etiqueta, String[] opciones) {
        agregar(etiqueta, new JComboBox<String>(opciones));
    }

    public void agregarCasilla(String etiqueta) {
        agregar(etiqueta, new JCheckBox());
    }

    public void agregarTitulo(String texto) {
        pnlCampos.add(new JLabel(texto));
        pnlCampos.add(new JLabel(""));
    }

    private void agregar(String etiqueta, JComponent campo) {
        etiquetas.add(etiqueta);
        campos.add(campo);
        pnlCampos.add(new JLabel(etiqueta + ":"));
        pnlCampos.add(campo);
    }

    // muestra la ventana y espera; devuelve true si se apreto Aceptar
    public boolean mostrar() {
        aceptado = false;
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return aceptado;
    }

    private JComponent campo(String etiqueta) {
        int i = etiquetas.indexOf(etiqueta);
        if (i == -1) {
            throw new IllegalArgumentException("No existe el campo " + etiqueta);
        }
        return campos.get(i);
    }

    public String texto(String etiqueta) {
        return ((JTextField) campo(etiqueta)).getText().trim();
    }

    public String textoObligatorio(String etiqueta) {
        String texto = texto(etiqueta);
        if (texto.isEmpty()) {
            throw new IllegalArgumentException("El campo " + etiqueta + " es obligatorio.");
        }
        return texto;
    }

    public int entero(String etiqueta) {
        try {
            return Integer.parseInt(texto(etiqueta));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo " + etiqueta + " tiene que ser un numero entero.");
        }
    }

    public double decimal(String etiqueta) {
        try {
            return Double.parseDouble(texto(etiqueta).replace(",", "."));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo " + etiqueta + " tiene que ser un numero.");
        }
    }

    // formato aaaa-mm-dd
    public LocalDate fecha(String etiqueta) {
        String[] partes = texto(etiqueta).split("-");
        if (partes.length != 3) {
            throw new IllegalArgumentException("El campo " + etiqueta + " tiene que tener el formato aaaa-mm-dd.");
        }
        try {
            return LocalDate.of(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]), Integer.parseInt(partes[2]));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El campo " + etiqueta + " tiene que tener el formato aaaa-mm-dd.");
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("La fecha de " + etiqueta + " no existe.");
        }
    }

    // posicion elegida en una lista de opciones, empieza en 0
    public int opcion(String etiqueta) {
        return ((JComboBox<?>) campo(etiqueta)).getSelectedIndex();
    }

    public boolean marcado(String etiqueta) {
        return ((JCheckBox) campo(etiqueta)).isSelected();
    }
}
