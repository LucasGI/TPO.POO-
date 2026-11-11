package servicio;

// una fila de reporte con su texto y el valor por el que se ordena
public class LineaReporte {
    private String texto;
    private double valor;

    public LineaReporte(String texto, double valor) {
        this.texto = texto;
        this.valor = valor;
    }

    public String getTexto() {
        return texto;
    }

    public double getValor() {
        return valor;
    }

    @Override
    public String toString() {
        return texto;
    }
}
