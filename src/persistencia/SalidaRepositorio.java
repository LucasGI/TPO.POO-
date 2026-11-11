package persistencia;

import modelo.Salida;

public class SalidaRepositorio extends RepositorioLista<Salida, Integer> {

    @Override
    protected Integer obtenerId(Salida elemento) {
        return elemento.getId();
    }
}
