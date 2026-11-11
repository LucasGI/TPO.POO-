package persistencia;

import modelo.Vehiculo;

public class VehiculoRepositorio extends RepositorioLista<Vehiculo, String> {

    @Override
    protected String obtenerId(Vehiculo elemento) {
        return elemento.getPatente();
    }
}
