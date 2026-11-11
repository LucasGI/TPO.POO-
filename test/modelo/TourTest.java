package modelo;

import excepcion.PasajeroInvalidoException;
import excepcion.ReglaNegocioException;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class TourTest {

    @Test(expected = PasajeroInvalidoException.class)
    public void aventuraRechazaMenoresDeLaEdadMinima() throws Exception {
        Tour tour = DatosTest.tourAventura();
        Pasajero chico = new Pasajero(DatosTest.pasajero("1", LocalDate.of(2015, 1, 1), false));
        tour.validarPasajero(chico, DatosTest.HOY);
    }

    @Test
    public void culturalAceptaMenores() throws Exception {
        Tour tour = DatosTest.tourCultural();
        Pasajero chico = new Pasajero(DatosTest.pasajero("1", LocalDate.of(2020, 1, 1), false));
        tour.validarPasajero(chico, DatosTest.HOY);
    }

    @Test
    public void enAventuraElSeguroSiempreEsObligatorio() throws Exception {
        Tour tour = DatosTest.tourAventura();
        assertEquals(1, tour.serviciosObligatorios().size());
        assertEquals("SEG", tour.serviciosObligatorios().get(0).getCodigo());
        assertTrue(DatosTest.tourCultural().serviciosObligatorios().isEmpty());
    }

    @Test(expected = ReglaNegocioException.class)
    public void aventuraSinSeguroNoEsValido() throws Exception {
        TourAventura tour = new TourAventura("AV02", "Sin seguro", "", 2, 2, 5, "Alta", 18);
        tour.agregarActividad(1, 1, "08:00", "Salida", "Base", null);
        tour.agregarTarifa(new Tarifa(Temporada.ALTA, DatosTest.HOY, DatosTest.HOY.plusDays(30), 5000));
        tour.validar();
    }

    @Test(expected = ReglaNegocioException.class)
    public void noAdmiteTarifasSuperpuestas() throws Exception {
        Tour tour = DatosTest.tourCultural();
        tour.agregarTarifa(new Tarifa(Temporada.ALTA, DatosTest.HOY.plusDays(100), DatosTest.HOY.plusDays(400), 20000));
    }

    @Test
    public void buscaLaTarifaVigentePorFecha() throws Exception {
        Tour tour = DatosTest.tourCultural();
        assertNotNull(tour.tarifaVigente(DatosTest.HOY.plusDays(30)));
        assertNull(tour.tarifaVigente(DatosTest.HOY.plusDays(500)));
    }

    @Test(expected = ReglaNegocioException.class)
    public void noCreaSalidaConCupoFueraDeRango() throws Exception {
        DatosTest.tourCultural().crearSalida(1, DatosTest.HOY.plusDays(20), 50);
    }

    @Test(expected = ReglaNegocioException.class)
    public void noCreaSalidaSinTarifaVigente() throws Exception {
        DatosTest.tourCultural().crearSalida(1, DatosTest.HOY.plusDays(500), 5);
    }

    @Test
    public void elItinerarioQuedaOrdenadoPorDiaYOrden() throws Exception {
        Tour tour = new TourNaturaleza("NA01", "Lagos", "", 3, 2, 10);
        tour.agregarActividad(2, 1, "09:00", "B", "", null);
        tour.agregarActividad(1, 2, "12:00", "A2", "", null);
        tour.agregarActividad(1, 1, "08:00", "A1", "", null);
        assertEquals("A1", tour.getItinerario().get(0).getDescripcion());
        assertEquals("A2", tour.getItinerario().get(1).getDescripcion());
        assertEquals("B", tour.getItinerario().get(2).getDescripcion());
    }

    @Test
    public void laFechaDeFinSeCalculaConLaDuracion() throws Exception {
        Salida salida = DatosTest.tourAventura().crearSalida(1, LocalDate.of(2026, 7, 10), 4);
        assertEquals(LocalDate.of(2026, 7, 12), salida.getFechaFin());
    }
}
