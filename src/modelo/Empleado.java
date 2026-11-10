package modelo;

public abstract class Empleado extends Persona {
    private int legajo;

    public Empleado(int legajo, String nombre, String apellido, String documento, String telefono) {
        super(nombre, apellido, documento, telefono);
        if (legajo <= 0) {
            throw new IllegalArgumentException("El legajo debe ser positivo.");
        }
        this.legajo = legajo;
    }

    public int getLegajo() {
        return legajo;
    }

    public abstract String getRol();

    @Override
    public String toString() {
        return "[" + legajo + "] " + getNombreCompleto() + " - " + getRol();
    }
}
