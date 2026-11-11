package persistencia;

import modelo.Reserva;

public class ReservaRepositorio extends RepositorioLista<Reserva, Integer> {

    @Override
    protected Integer obtenerId(Reserva elemento) {
        return elemento.getNumero();
    }
}
