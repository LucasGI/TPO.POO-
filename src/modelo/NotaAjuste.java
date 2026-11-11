package modelo;

import java.io.Serializable;
import java.time.LocalDate;

// las reservas con pagos no se borran, las correcciones quedan como notas (RN16)
public class NotaAjuste implements Serializable {
    private LocalDate fecha;
    private double importe;
    private String detalle;

    public NotaAjuste(LocalDate fecha, double importe, String detalle) {
        this.fecha = fecha;
        this.importe = importe;
        this.detalle = detalle;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public double getImporte() {
        return importe;
    }

    public String getDetalle() {
        return detalle;
    }

    @Override
    public String toString() {
        return fecha + " - " + detalle + " ($" + importe + ")";
    }
}
