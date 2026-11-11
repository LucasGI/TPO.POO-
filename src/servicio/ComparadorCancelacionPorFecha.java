package servicio;

import modelo.Reserva;

import java.util.Comparator;

// ordena las reservas canceladas de la mas reciente a la mas antigua
public class ComparadorCancelacionPorFecha implements Comparator<Reserva> {

    @Override
    public int compare(Reserva a, Reserva b) {
        return b.getCancelacion().getFecha().compareTo(a.getCancelacion().getFecha());
    }
}
