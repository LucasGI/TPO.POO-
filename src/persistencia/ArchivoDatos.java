package persistencia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

// lee y escribe la base completa con serializacion de objetos
public class ArchivoDatos {
    private String ruta;

    public ArchivoDatos(String ruta) {
        this.ruta = ruta;
    }

    public boolean existe() {
        return new File(ruta).exists();
    }

    public void guardar(BaseDatos base) throws IOException {
        File archivo = new File(ruta);
        if (archivo.getParentFile() != null) {
            archivo.getParentFile().mkdirs();
        }
        // se guarda todo en un solo flujo para que las referencias entre objetos se mantengan
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(archivo))) {
            salida.writeObject(base);
        }
    }

    public BaseDatos cargar() throws IOException, ClassNotFoundException {
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(ruta))) {
            return (BaseDatos) entrada.readObject();
        }
    }

    public String getRuta() {
        return ruta;
    }
}
