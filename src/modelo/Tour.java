package modelo;

import excepcion.PasajeroInvalidoException;
import excepcion.ReglaNegocioException;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Tour implements Serializable {
    private String codigo;
    private String nombre;
    private String descripcion;
    private int duracionDias;
    private int cupoMinimo;
    private int cupoMaximo;
    private List<Actividad> itinerario;
    private List<Tarifa> tarifas;
    private List<ServicioAdicional> servicios;

    public Tour(String codigo, String nombre, String descripcion, int duracionDias, int cupoMinimo, int cupoMaximo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo del tour es obligatorio.");
        }
        if (duracionDias <= 0) {
            throw new IllegalArgumentException("La duracion debe ser de al menos un dia.");
        }
        if (cupoMinimo <= 0 || cupoMinimo > cupoMaximo) {
            throw new IllegalArgumentException("El cupo minimo debe ser positivo y no superar al maximo.");
        }
        this.codigo = codigo.toUpperCase();
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionDias = duracionDias;
        this.cupoMinimo = cupoMinimo;
        this.cupoMaximo = cupoMaximo;
        this.itinerario = new ArrayList<>();
        this.tarifas = new ArrayList<>();
        this.servicios = new ArrayList<>();
    }

    public abstract Categoria getCategoria();

    // las subclases agregan sus propias condiciones
    public void validarPasajero(Pasajero pasajero, LocalDate fechaSalida) throws PasajeroInvalidoException {
        if (pasajero.getFechaNacimiento().isAfter(fechaSalida)) {
            throw new PasajeroInvalidoException("La fecha de nacimiento de " + pasajero.getNombreCompleto()
                    + " es posterior a la salida.");
        }
    }

    // controla que el tour este completo antes de darlo de alta
    public void validar() throws ReglaNegocioException {
        if (itinerario.isEmpty()) {
            throw new ReglaNegocioException("El tour " + codigo + " debe tener al menos una actividad.");
        }
        if (tarifas.isEmpty()) {
            throw new ReglaNegocioException("El tour " + codigo + " debe tener al menos una tarifa.");
        }
    }

    public void agregarActividad(int dia, int orden, String horario, String descripcion, String lugar,
                                 Proveedor proveedor) {
        if (dia > duracionDias) {
            throw new IllegalArgumentException("El dia " + dia + " supera la duracion del tour.");
        }
        itinerario.add(new Actividad(dia, orden, horario, descripcion, lugar, proveedor));
        Collections.sort(itinerario);
    }

    public void agregarTarifa(Tarifa nueva) throws ReglaNegocioException {
        for (Tarifa t : tarifas) {
            if (t.seSuperponeCon(nueva)) {
                throw new ReglaNegocioException("La tarifa se superpone con otra vigente: " + t);
            }
        }
        tarifas.add(nueva);
    }

    public void agregarServicio(ServicioAdicional servicio) {
        if (buscarServicio(servicio.getCodigo()) != null) {
            throw new IllegalArgumentException("Ya existe un servicio con codigo " + servicio.getCodigo());
        }
        servicios.add(servicio);
    }

    public ServicioAdicional buscarServicio(String codigoServicio) {
        for (ServicioAdicional s : servicios) {
            if (s.getCodigo().equalsIgnoreCase(codigoServicio)) {
                return s;
            }
        }
        return null;
    }

    public List<ServicioAdicional> serviciosObligatorios() {
        List<ServicioAdicional> obligatorios = new ArrayList<>();
        for (ServicioAdicional s : servicios) {
            if (s.isObligatorio()) {
                obligatorios.add(s);
            }
        }
        return obligatorios;
    }

    public Tarifa tarifaVigente(LocalDate fecha) {
        for (Tarifa t : tarifas) {
            if (t.vigenteEn(fecha)) {
                return t;
            }
        }
        return null;
    }

    // Creador: el tour tiene los datos para armar una salida valida
    public Salida crearSalida(int id, LocalDate fechaInicio, int cupo) throws ReglaNegocioException {
        if (cupo < cupoMinimo || cupo > cupoMaximo) {
            throw new ReglaNegocioException("El cupo debe estar entre " + cupoMinimo + " y " + cupoMaximo + ".");
        }
        if (tarifaVigente(fechaInicio) == null) {
            throw new ReglaNegocioException("El tour " + codigo + " no tiene tarifa vigente para el " + fechaInicio + ".");
        }
        return new Salida(id, this, fechaInicio, cupo);
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getDuracionDias() {
        return duracionDias;
    }

    public int getCupoMinimo() {
        return cupoMinimo;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public List<Actividad> getItinerario() {
        return new ArrayList<>(itinerario);
    }

    public List<Tarifa> getTarifas() {
        return new ArrayList<>(tarifas);
    }

    public List<ServicioAdicional> getServicios() {
        return new ArrayList<>(servicios);
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " [" + getCategoria() + ", " + duracionDias + " dias, cupo "
                + cupoMinimo + "-" + cupoMaximo + "]";
    }
}
