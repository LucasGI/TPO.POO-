package modelo;

import java.io.Serializable;

public class Vehiculo implements Serializable {
    private String patente;
    private int capacidad;
    private TipoVehiculo tipo;

    public Vehiculo(String patente, int capacidad, TipoVehiculo tipo) {
        if (patente == null || patente.isBlank()) {
            throw new IllegalArgumentException("La patente es obligatoria.");
        }
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a cero.");
        }
        this.patente = patente.toUpperCase();
        this.capacidad = capacidad;
        this.tipo = tipo;
    }

    public String getPatente() {
        return patente;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }

    @Override
    public String toString() {
        return patente + " - " + tipo + " (" + capacidad + " lugares)";
    }
}
