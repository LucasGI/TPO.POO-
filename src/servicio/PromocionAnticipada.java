package servicio;

import modelo.Reserva;

// 15% si la reserva se hace con mas de 60 dias de anticipacion
public class PromocionAnticipada implements Promocion {
    private static final int DIAS_ANTICIPACION = 60;

    @Override
    public boolean aplica(Reserva reserva) {
        long dias = reserva.getSalida().diasHasta(reserva.getFecha());
        return dias > DIAS_ANTICIPACION;
    }

    @Override
    public double porcentaje() {
        return 0.15;
    }

    @Override
    public String getNombre() {
        return "Reserva anticipada";
    }
}
