package modelo;

public class Administrador extends Empleado {

    public Administrador(int legajo, String nombre, String apellido, String documento, String telefono) {
        super(legajo, nombre, apellido, documento, telefono);
    }

    @Override
    public String getRol() {
        return "Administrador";
    }
}
