package persistencia;

import modelo.Cliente;

public class ClienteRepositorio extends RepositorioLista<Cliente, String> {

    @Override
    protected String obtenerId(Cliente elemento) {
        return elemento.getDocumento();
    }
}
