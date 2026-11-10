package modelo;

public class Vendedor extends Empleado {
    // porcentaje de comision sobre las ventas, por ejemplo 0.03
    private double comision;

    public Vendedor(int legajo, String nombre, String apellido, String documento, String telefono,
                    double comision) {
        super(legajo, nombre, apellido, documento, telefono);
        if (comision < 0 || comision > 1) {
            throw new IllegalArgumentException("La comision debe estar entre 0 y 1.");
        }
        this.comision = comision;
    }

    public double getComision() {
        return comision;
    }

    public double calcularComision(double ventas) {
        return ventas * comision;
    }

    @Override
    public String getRol() {
        return "Vendedor";
    }
}
