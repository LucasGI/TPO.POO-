package servicio;

import modelo.Reserva;

public interface Promocion {

    boolean aplica(Reserva reserva);

    double porcentaje();

    String getNombre();
}
