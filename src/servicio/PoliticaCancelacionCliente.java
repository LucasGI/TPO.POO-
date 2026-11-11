package servicio;

// escala de devolucion cuando cancela el cliente (RN12)
public class PoliticaCancelacionCliente implements PoliticaCancelacion {

    @Override
    public double porcentajeDevolucion(long diasAntes) {
        if (diasAntes >= 30) {
            return 0.90;
        }
        if (diasAntes >= 15) {
            return 0.50;
        }
        if (diasAntes >= 7) {
            return 0.20;
        }
        return 0.0;
    }

    @Override
    public boolean esCancelacionDeAgencia() {
        return false;
    }
}
