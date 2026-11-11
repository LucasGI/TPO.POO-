package modelo;

import java.io.Serializable;

public class Actividad implements Serializable, Comparable<Actividad> {
    private int dia;
    private int orden;
    private String horario;
    private String descripcion;
    private String lugar;
    private Proveedor proveedor;

    public Actividad(int dia, int orden, String horario, String descripcion, String lugar,
                     Proveedor proveedor) {
        if (dia <= 0 || orden <= 0) {
            throw new IllegalArgumentException("El dia y el orden de la actividad deben ser positivos.");
        }
        this.dia = dia;
        this.orden = orden;
        this.horario = horario;
        this.descripcion = descripcion;
        this.lugar = lugar;
        this.proveedor = proveedor;
    }

    public int getDia() {
        return dia;
    }

    public int getOrden() {
        return orden;
    }

    public String getHorario() {
        return horario;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getLugar() {
        return lugar;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    // orden natural del itinerario: primero por dia y despues por orden
    @Override
    public int compareTo(Actividad otra) {
        if (this.dia != otra.dia) {
            return this.dia - otra.dia;
        }
        return this.orden - otra.orden;
    }

    @Override
    public String toString() {
        String texto = "Dia " + dia + " - " + horario + " " + descripcion + " (" + lugar + ")";
        if (proveedor != null) {
            texto += " - proveedor: " + proveedor.getNombre();
        }
        return texto;
    }
}
