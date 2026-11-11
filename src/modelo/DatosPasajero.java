package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// datos que carga el vendedor para cada pasajero de una reserva
public class DatosPasajero {
    private String nombre;
    private String apellido;
    private String documento;
    private LocalDate fechaNacimiento;
    private boolean jubilado;
    private String contactoEmergencia;
    private String restricciones;
    private List<String> codigosAdicionales;

    public DatosPasajero(String nombre, String apellido, String documento, LocalDate fechaNacimiento,
                         boolean jubilado, String contactoEmergencia, String restricciones) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.fechaNacimiento = fechaNacimiento;
        this.jubilado = jubilado;
        this.contactoEmergencia = contactoEmergencia;
        this.restricciones = restricciones;
        this.codigosAdicionales = new ArrayList<>();
    }

    public void agregarAdicional(String codigo) {
        codigosAdicionales.add(codigo);
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

    public List<String> getCodigosAdicionales() {
        return new ArrayList<>(codigosAdicionales);
    }
}
