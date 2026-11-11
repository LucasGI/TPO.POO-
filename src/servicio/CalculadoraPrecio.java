package servicio;

import excepcion.ReglaNegocioException;
import modelo.Pasajero;
import modelo.Reserva;
import modelo.Tarifa;
import modelo.TipoPasajero;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// separa el calculo del precio de la reserva (Fabricacion pura)
public class CalculadoraPrecio {
    public static final double DESCUENTO_JUBILADO = 0.10;

    private List<Promocion> promociones;

    public CalculadoraPrecio(List<Promocion> promociones) {
        this.promociones = new ArrayList<>(promociones);
    }

    public double calcularTotal(Reserva reserva) throws ReglaNegocioException {
        LocalDate fechaSalida = reserva.getSalida().getFechaInicio();
        Tarifa tarifa = reserva.getSalida().getTour().tarifaVigente(fechaSalida);
        if (tarifa == null) {
            throw new ReglaNegocioException("No hay tarifa vigente para el " + fechaSalida + ".");
        }
        double subtotal = 0;
        for (Pasajero p : reserva.getPasajeros()) {
            subtotal += precioPasajero(p, tarifa.getPrecioBase(), fechaSalida);
        }
        // la promocion se aplica sobre la tarifa, no sobre los adicionales (S05)
        double conDescuento = subtotal * (1 - mejorDescuento(reserva));
        double total = conDescuento + reserva.totalAdicionales();
        return Math.round(total * 100) / 100.0;
    }

    public double precioPasajero(Pasajero pasajero, double precioBase, LocalDate fechaSalida) {
        TipoPasajero tipo = pasajero.tipoEn(fechaSalida);
        double precio = precioBase * tipo.getFactor();
        if (tipo == TipoPasajero.ADULTO && pasajero.isJubilado()) {
            precio = precio * (1 - DESCUENTO_JUBILADO);
        }
        return precio;
    }

    // las promociones no se acumulan: se queda con la de mayor porcentaje (RN08)
    public double mejorDescuento(Reserva reserva) {
        double mejor = 0;
        for (Promocion p : promociones) {
            if (p.aplica(reserva) && p.porcentaje() > mejor) {
                mejor = p.porcentaje();
            }
        }
        return mejor;
    }

    public Promocion mejorPromocion(Reserva reserva) {
        Promocion mejor = null;
        for (Promocion p : promociones) {
            if (p.aplica(reserva) && (mejor == null || p.porcentaje() > mejor.porcentaje())) {
                mejor = p;
            }
        }
        return mejor;
    }
}
