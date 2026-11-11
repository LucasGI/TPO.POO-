package modelo;

import excepcion.ReglaNegocioException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// objetos de ejemplo que usan todas las pruebas
public class DatosTest {
    public static final LocalDate HOY = LocalDate.of(2026, 6, 1);

    public static TourAventura tourAventura() throws ReglaNegocioException {
        TourAventura tour = new TourAventura("AV01", "Trekking", "Prueba", 3, 2, 4, "Media", 14);
        tour.agregarActividad(1, 1, "08:00", "Salida", "Base", null);
        tour.agregarServicio(new ServicioAdicional("SEG", "Seguro", 1000, TipoServicio.SEGURO, false));
        tour.agregarServicio(new ServicioAdicional("RAF", "Rafting", 5000, TipoServicio.EXCURSION, false));
        tour.agregarTarifa(new Tarifa(Temporada.MEDIA, HOY.minusDays(10), HOY.plusDays(365), 10000));
        return tour;
    }

    public static TourCultural tourCultural() throws ReglaNegocioException {
        TourCultural tour = new TourCultural("CU01", "City tour", "Prueba", 1, 2, 10);
        tour.agregarActividad(1, 1, "09:00", "Recorrido", "Centro", null);
        tour.agregarServicio(new ServicioAdicional("ALM", "Almuerzo", 2000, TipoServicio.COMIDA, false));
        tour.agregarTarifa(new Tarifa(Temporada.BAJA, HOY.minusDays(10), HOY.plusDays(365), 10000));
        return tour;
    }

    public static Cliente cliente() {
        return new Cliente("Ana", "Lopez", "30111222", "11", "ana@mail.com", "Argentina");
    }

    public static Vendedor vendedor() {
        return new Vendedor(101, "Martina", "Gomez", "33111222", "11", 0.03);
    }

    public static Guia guia(Categoria categoria) {
        Guia guia = new Guia(201, "Sofia", "Perez", "30555666", "11", 1000);
        guia.habilitarCategoria(categoria);
        return guia;
    }

    public static DatosPasajero adulto(String documento) {
        return new DatosPasajero("Adulto", "Ape" + documento, documento, LocalDate.of(1990, 1, 1), false, "Contacto", "");
    }

    public static DatosPasajero pasajero(String documento, LocalDate nacimiento, boolean jubilado) {
        return new DatosPasajero("Nombre", "Ape" + documento, documento, nacimiento, jubilado, "Contacto", "");
    }

    public static List<DatosPasajero> adultos(int cantidad) {
        List<DatosPasajero> lista = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            lista.add(adulto("100" + i));
        }
        return lista;
    }
}
