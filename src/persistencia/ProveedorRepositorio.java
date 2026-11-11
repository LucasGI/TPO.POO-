package persistencia;

import modelo.Proveedor;

public class ProveedorRepositorio extends RepositorioLista<Proveedor, String> {

    @Override
    protected String obtenerId(Proveedor elemento) {
        return elemento.getCuit();
    }
}
