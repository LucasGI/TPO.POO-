package modelo;

import java.io.Serializable;

public class Proveedor implements Serializable {
    private String nombre;
    private String cuit;
    private String contacto;
    private TipoServicio servicio;

    public Proveedor(String nombre, String cuit, String contacto, TipoServicio servicio) {
        if (cuit == null || cuit.isBlank()) {
            throw new IllegalArgumentException("El CUIT del proveedor es obligatorio.");
        }
        this.nombre = nombre;
        this.cuit = cuit;
        this.contacto = contacto;
        this.servicio = servicio;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCuit() {
        return cuit;
    }

    public String getContacto() {
        return contacto;
    }

    public TipoServicio getServicio() {
        return servicio;
    }

    @Override
    public String toString() {
        return nombre + " (" + servicio + ", CUIT " + cuit + ")";
    }
}
