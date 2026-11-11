package persistencia;

import java.util.List;

// los controladores dependen de esta interfaz y no de como se guardan los datos
public interface Repositorio<T, ID> {

    void guardar(T elemento);

    // devuelve null si no lo encuentra
    T buscarPorId(ID id);

    List<T> listar();

    int cantidad();
}
