package modelo;

public class Coordinador extends Empleado {

    public Coordinador(int legajo, String nombre, String apellido, String documento, String telefono) {
        super(legajo, nombre, apellido, documento, telefono);
    }

    @Override
    public String getRol() {
        return "Coordinador de operaciones";
    }
}
