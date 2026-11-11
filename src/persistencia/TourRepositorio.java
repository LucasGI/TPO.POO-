package persistencia;

import modelo.Tour;

public class TourRepositorio extends RepositorioLista<Tour, String> {

    @Override
    protected String obtenerId(Tour elemento) {
        return elemento.getCodigo();
    }
}
