package excepcion;

public class CupoInsuficienteException extends ReglaNegocioException {

    public CupoInsuficienteException(String mensaje) {
        super(mensaje);
    }
}
