package modelo;

import excepcion.PasajeroInvalidoException;
import excepcion.ReglaNegocioException;

import java.time.LocalDate;
import java.util.List;

public class TourAventura extends Tour {
    private String nivelDificultad;
    private int edadMinima;

    public TourAventura(String codigo, String nombre, String descripcion, int duracionDias, int cupoMinimo,
                        int cupoMaximo, String nivelDificultad, int edadMinima) {
        super(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
        if (edadMinima < 0) {
            throw new IllegalArgumentException("La edad minima no puede ser negativa.");
        }
        this.nivelDificultad = nivelDificultad;
        this.edadMinima = edadMinima;
    }

    @Override
    public Categoria getCategoria() {
        return Categoria.AVENTURA;
    }

    @Override
    public void validarPasajero(Pasajero pasajero, LocalDate fechaSalida) throws PasajeroInvalidoException {
        super.validarPasajero(pasajero, fechaSalida);
        int edad = pasajero.edadEn(fechaSalida);
        if (edad < edadMinima) {
            throw new PasajeroInvalidoException(pasajero.getNombreCompleto() + " tendra " + edad
                    + " de edad y el tour exige un minimo de " + edadMinima + ".");
        }
    }

    // en aventura el seguro de viaje siempre es obligatorio (RN09)
    @Override
    public List<ServicioAdicional> serviciosObligatorios() {
        List<ServicioAdicional> obligatorios = super.serviciosObligatorios();
        for (ServicioAdicional s : getServicios()) {
            if (s.getTipo() == TipoServicio.SEGURO && !obligatorios.contains(s)) {
                obligatorios.add(s);
            }
        }
        return obligatorios;
    }

    @Override
    public void validar() throws ReglaNegocioException {
        super.validar();
        boolean tieneSeguro = false;
        for (ServicioAdicional s : getServicios()) {
            if (s.getTipo() == TipoServicio.SEGURO) {
                tieneSeguro = true;
            }
        }
        if (!tieneSeguro) {
            throw new ReglaNegocioException("Un tour de aventura debe incluir el seguro de viaje.");
        }
    }

    public String getNivelDificultad() {
        return nivelDificultad;
    }

    public int getEdadMinima() {
        return edadMinima;
    }

    @Override
    public String toString() {
        return super.toString() + " dificultad " + nivelDificultad + ", edad minima " + edadMinima;
    }
}
