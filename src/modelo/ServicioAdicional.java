package modelo;

import java.io.Serializable;

public class ServicioAdicional implements Serializable {
    private String codigo;
    private String nombre;
    private double precio;
    private TipoServicio tipo;
    private boolean obligatorio;

    public ServicioAdicional(String codigo, String nombre, double precio, TipoServicio tipo, boolean obligatorio) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo del servicio es obligatorio.");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio del servicio no puede ser negativo.");
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.precio = precio;
        this.tipo = tipo;
        this.obligatorio = obligatorio;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public TipoServicio getTipo() {
        return tipo;
    }

    public boolean isObligatorio() {
        return obligatorio;
    }

    @Override
    public String toString() {
        String texto = codigo + " - " + nombre + " $" + precio;
        if (obligatorio) {
            texto += " (obligatorio)";
        }
        return texto;
    }
}
