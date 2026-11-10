package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Guia extends Empleado {
    private List<String> idiomas;
    private List<Categoria> categorias;
    private double honorarioDiario;

    public Guia(int legajo, String nombre, String apellido, String documento, String telefono,
                double honorarioDiario) {
        super(legajo, nombre, apellido, documento, telefono);
        if (honorarioDiario <= 0) {
            throw new IllegalArgumentException("El honorario diario debe ser mayor a cero.");
        }
        this.honorarioDiario = honorarioDiario;
        this.idiomas = new ArrayList<>();
        this.categorias = new ArrayList<>();
    }

    public void agregarIdioma(String idioma) {
        if (!idiomas.contains(idioma)) {
            idiomas.add(idioma);
        }
    }

    public void habilitarCategoria(Categoria categoria) {
        if (!categorias.contains(categoria)) {
            categorias.add(categoria);
        }
    }

    public boolean puedeGuiar(Tour tour) {
        return categorias.contains(tour.getCategoria());
    }

    // un guia no puede tener dos salidas que se superpongan (RN05)
    public boolean estaDisponible(LocalDate desde, LocalDate hasta, List<Salida> salidas) {
        for (Salida s : salidas) {
            if (s.getGuia() == this && s.estaVigente() && s.seSuperponeCon(desde, hasta)) {
                return false;
            }
        }
        return true;
    }

    public List<String> getIdiomas() {
        return new ArrayList<>(idiomas);
    }

    public List<Categoria> getCategorias() {
        return new ArrayList<>(categorias);
    }

    public double getHonorarioDiario() {
        return honorarioDiario;
    }

    @Override
    public String getRol() {
        return "Guia";
    }

    @Override
    public String toString() {
        return super.toString() + " " + categorias + " " + idiomas;
    }
}
