package modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Pago implements Serializable {
    private LocalDate fecha;
    private double importe;
    private double recargo;
    private MedioPago medio;
    private int cuotas;

    public Pago(LocalDate fecha, double importe, double recargo, MedioPago medio, int cuotas) {
        this.fecha = fecha;
        this.importe = importe;
        this.recargo = recargo;
        this.medio = medio;
        this.cuotas = cuotas;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    // importe que se descuenta del saldo de la reserva
    public double getImporte() {
        return importe;
    }

    public double getRecargo() {
        return recargo;
    }

    public double getTotalCobrado() {
        return importe + recargo;
    }

    public MedioPago getMedio() {
        return medio;
    }

    public int getCuotas() {
        return cuotas;
    }

    @Override
    public String toString() {
        String texto = fecha + " - " + medio + " $" + importe;
        if (recargo > 0) {
            texto += " + recargo $" + recargo + " (" + cuotas + " cuotas)";
        }
        return texto;
    }
}
