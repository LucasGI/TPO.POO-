package excepcion;

// Excepcion base para las reglas de negocio del sistema
public class ReglaNegocioException extends Exception {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
