package modelo;

public class TourCultural extends Tour {

    public TourCultural(String codigo, String nombre, String descripcion, int duracionDias, int cupoMinimo,
                        int cupoMaximo) {
        super(codigo, nombre, descripcion, duracionDias, cupoMinimo, cupoMaximo);
    }

    @Override
    public Categoria getCategoria() {
        return Categoria.CULTURAL;
    }
}
