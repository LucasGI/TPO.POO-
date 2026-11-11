package servicio;

import java.io.Serializable;

public interface PoliticaCancelacion extends Serializable {

    // porcentaje del monto pagado que se devuelve, entre 0 y 1
    double porcentajeDevolucion(long diasAntes);

    boolean esCancelacionDeAgencia();
}
