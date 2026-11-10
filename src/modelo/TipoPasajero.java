package modelo;

public enum TipoPasajero {
    INFANTE(0.0),
    MENOR(0.6),
    ADULTO(1.0);

    // porcentaje de la tarifa base que paga cada tipo
    private final double factor;

    TipoPasajero(double factor) {
        this.factor = factor;
    }

    public double getFactor() {
        return factor;
    }
}
