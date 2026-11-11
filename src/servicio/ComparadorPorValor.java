package servicio;

import java.util.Comparator;

// ordena las lineas de mayor a menor valor
public class ComparadorPorValor implements Comparator<LineaReporte> {

    @Override
    public int compare(LineaReporte a, LineaReporte b) {
        return Double.compare(b.getValor(), a.getValor());
    }
}
