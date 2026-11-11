package persistencia;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// guarda los elementos en una lista; ArchivoDatos se encarga de escribirla en disco
public abstract class RepositorioLista<T, ID> implements Repositorio<T, ID>, Serializable {
    protected List<T> elementos;

    public RepositorioLista() {
        elementos = new ArrayList<>();
    }

    protected abstract ID obtenerId(T elemento);

    @Override
    public void guardar(T elemento) {
        ID id = obtenerId(elemento);
        for (int i = 0; i < elementos.size(); i++) {
            if (obtenerId(elementos.get(i)).equals(id)) {
                elementos.set(i, elemento);
                return;
            }
        }
        elementos.add(elemento);
    }

    @Override
    public T buscarPorId(ID id) {
        for (T elemento : elementos) {
            if (obtenerId(elemento).equals(id)) {
                return elemento;
            }
        }
        return null;
    }

    @Override
    public List<T> listar() {
        return new ArrayList<>(elementos);
    }

    @Override
    public int cantidad() {
        return elementos.size();
    }
}
