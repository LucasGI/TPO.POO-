package modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class Tarifa implements Serializable {
    private Temporada temporada;
    private LocalDate vigenciaDesde;
    private LocalDate vigenciaHasta;
    private double precioBase;

    public Tarifa(Temporada temporada, LocalDate vigenciaDesde, LocalDate vigenciaHasta, double precioBase) {
        if (vigenciaHasta.isBefore(vigenciaDesde)) {
            throw new IllegalArgumentException("La vigencia hasta no puede ser anterior a la vigencia desde.");
        }
        if (precioBase <= 0) {
            throw new IllegalArgumentException("El precio base debe ser mayor a cero.");
        }
        this.temporada = temporada;
        this.vigenciaDesde = vigenciaDesde;
        this.vigenciaHasta = vigenciaHasta;
        this.precioBase = precioBase;
    }

    public boolean vigenteEn(LocalDate fecha) {
        return !fecha.isBefore(vigenciaDesde) && !fecha.isAfter(vigenciaHasta);
    }

    public boolean seSuperponeCon(Tarifa otra) {
        return !this.vigenciaHasta.isBefore(otra.vigenciaDesde) && !otra.vigenciaHasta.isBefore(this.vigenciaDesde);
    }

    public Temporada getTemporada() {
        return temporada;
    }

    public LocalDate getVigenciaDesde() {
        return vigenciaDesde;
    }

    public LocalDate getVigenciaHasta() {
        return vigenciaHasta;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    @Override
    public String toString() {
        return temporada + " del " + vigenciaDesde + " al " + vigenciaHasta + ": $" + precioBase;
    }
}
