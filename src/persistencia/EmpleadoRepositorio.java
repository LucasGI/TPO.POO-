package persistencia;

import modelo.Empleado;

public class EmpleadoRepositorio extends RepositorioLista<Empleado, Integer> {

    @Override
    protected Integer obtenerId(Empleado elemento) {
        return elemento.getLegajo();
    }
}
