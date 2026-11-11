package modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Cancelacion implements Serializable {
    private LocalDate fecha;
    private String motivo;
    private boolean porAgencia;
    private double importeDevuelto;
    private MedioPago medio;

    public Cancelacion(LocalDate fecha, String motivo, boolean porAgencia, double importeDevuelto, MedioPago medio) {
        this.fecha = fecha;
        this.motivo = motivo;
        this.porAgencia = porAgencia;
        this.importeDevuelto = importeDevuelto;
        this.medio = medio;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public boolean isPorAgencia() {
        return porAgencia;
    }

    public double getImporteDevuelto() {
        return importeDevuelto;
    }

    public MedioPago getMedio() {
        return medio;
    }

    @Override
    public String toString() {
        String quien = porAgencia ? "agencia" : "cliente";
        String texto = fecha + " - cancela " + quien + " - " + motivo + " - devolucion $" + importeDevuelto;
        if (medio != null) {
            texto += " por " + medio;
        }
        return texto;
    }
}
