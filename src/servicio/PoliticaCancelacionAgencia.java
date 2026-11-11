package servicio;

// si cancela la agencia se devuelve todo lo pagado
public class PoliticaCancelacionAgencia implements PoliticaCancelacion {

    @Override
    public double porcentajeDevolucion(long diasAntes) {
        return 1.0;
    }

    @Override
    public boolean esCancelacionDeAgencia() {
        return true;
    }
}
