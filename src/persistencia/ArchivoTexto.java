package persistencia;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

// exporta reportes y listas de pasajeros a archivos .txt
public class ArchivoTexto {
    private String carpeta;

    public ArchivoTexto(String carpeta) {
        this.carpeta = carpeta;
    }

    public String escribir(String nombreArchivo, List<String> lineas) throws IOException {
        File directorio = new File(carpeta);
        directorio.mkdirs();
        File archivo = new File(directorio, nombreArchivo);
        try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo))) {
            for (String linea : lineas) {
                escritor.write(linea);
                escritor.newLine();
            }
        }
        return archivo.getPath();
    }
}
