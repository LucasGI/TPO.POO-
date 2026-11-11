package excepcion;

public class EstadoInvalidoException extends ReglaNegocioException {

    public EstadoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
