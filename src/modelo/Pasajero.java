package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Pasajero implements Serializable, Comparable<Pasajero> {
    private String nombre;
    private String apellido;
    private String documento;
    private LocalDate fechaNacimiento;
    private boolean jubilado;
    private String contactoEmergencia;
    private String restricciones;
    private List<ServicioAdicional> adicionales;

    public Pasajero(DatosPasajero datos) {
        if (datos.getNombre() == null || datos.getNombre().isBlank()
                || datos.getApellido() == null || datos.getApellido().isBlank()) {
            throw new IllegalArgumentException("El nombre y el apellido del pasajero son obligatorios.");
        }
        if (datos.getDocumento() == null || datos.getDocumento().isBlank()) {
            throw new IllegalArgumentException("El documento del pasajero es obligatorio.");
        }
        if (datos.getFechaNacimiento() == null) {
            throw new IllegalArgumentException("La fecha de nacimiento es obligatoria.");
        }
        this.nombre = datos.getNombre();
        this.apellido = datos.getApellido();
        this.documento = datos.getDocumento();
        this.fechaNacimiento = datos.getFechaNacimiento();
        this.jubilado = datos.isJubilado();
        this.contactoEmergencia = datos.getContactoEmergencia();
        this.restricciones = datos.getRestricciones();
        this.adicionales = new ArrayList<>();
    }

    public int edadEn(LocalDate fecha) {
        int edad = fecha.getYear() - fechaNacimiento.getYear();
        // si todavia no cumplio anios en ese anio, resto uno
        if (fecha.getMonthValue() < fechaNacimiento.getMonthValue()
                || (fecha.getMonthValue() == fechaNacimiento.getMonthValue()
                && fecha.getDayOfMonth() < fechaNacimiento.getDayOfMonth())) {
            edad--;
        }
        return edad;
    }

    // el tipo se calcula con la edad a la fecha de la salida (S03)
    public TipoPasajero tipoEn(LocalDate fecha) {
        int edad = edadEn(fecha);
        if (edad < 3) {
            return TipoPasajero.INFANTE;
        }
        if (edad < 12) {
            return TipoPasajero.MENOR;
        }
        return TipoPasajero.ADULTO;
    }

    public void agregarAdicional(ServicioAdicional servicio) {
        if (!adicionales.contains(servicio)) {
            adicionales.add(servicio);
        }
    }

    public double totalAdicionales() {
        double total = 0;
        for (ServicioAdicional s : adicionales) {
            total += s.getPrecio();
        }
        return total;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDocumento() {
        return documento;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public boolean isJubilado() {
        return jubilado;
    }

    public String getContactoEmergencia() {
        return contactoEmergencia;
    }

    public String getRestricciones() {
        return restricciones;
    }

    public List<ServicioAdicional> getAdicionales() {
        return new ArrayList<>(adicionales);
    }

    // orden natural por apellido y nombre, se usa en la lista de pasajeros
    @Override
    public int compareTo(Pasajero otro) {
        int comparacion = this.apellido.compareToIgnoreCase(otro.apellido);
        if (comparacion != 0) {
            return comparacion;
        }
        return this.nombre.compareToIgnoreCase(otro.nombre);
    }

    @Override
    public String toString() {
        return apellido + ", " + nombre + " - doc. " + documento;
    }
}
