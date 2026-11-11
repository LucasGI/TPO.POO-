package servicio;

import modelo.Reserva;

// 10% para grupos de 6 pasajeros o mas
public class PromocionGrupo implements Promocion {
    private static final int MINIMO_PASAJEROS = 6;

    @Override
    public boolean aplica(Reserva reserva) {
        return reserva.cantidadLugares() >= MINIMO_PASAJEROS;
    }

    @Override
    public double porcentaje() {
        return 0.10;
    }

    @Override
    public String getNombre() {
        return "Grupo";
    }
}
