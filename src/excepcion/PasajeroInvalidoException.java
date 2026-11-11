package excepcion;

public class PasajeroInvalidoException extends ReglaNegocioException {

    public PasajeroInvalidoException(String mensaje) {
        super(mensaje);
    }
}
