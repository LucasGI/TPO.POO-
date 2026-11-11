package modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Liquidacion implements Serializable {
    private LocalDate fecha;
    private Guia guia;
    private int diasTrabajados;
    private double montoFijo;
    private double montoAdicionales;

    public Liquidacion(LocalDate fecha, Guia guia, int diasTrabajados, double montoFijo, double montoAdicionales) {
        this.fecha = fecha;
        this.guia = guia;
        this.diasTrabajados = diasTrabajados;
        this.montoFijo = montoFijo;
        this.montoAdicionales = montoAdicionales;
    }

    public double total() {
        return montoFijo + montoAdicionales;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public Guia getGuia() {
        return guia;
    }

    public int getDiasTrabajados() {
        return diasTrabajados;
    }

    public double getMontoFijo() {
        return montoFijo;
    }

    public double getMontoAdicionales() {
        return montoAdicionales;
    }

    @Override
    public String toString() {
        return fecha + " - " + guia.getNombreCompleto() + ": " + diasTrabajados + " dias $" + montoFijo
                + " + 5% adicionales $" + montoAdicionales + " = $" + total();
    }
}
