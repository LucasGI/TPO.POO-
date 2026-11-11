package modelo;

import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PasajeroTest {

    @Test
    public void calculaLaEdadSegunElCumpleanios() {
        Pasajero p = new Pasajero(DatosTest.pasajero("1", LocalDate.of(2000, 6, 15), false));
        assertEquals(25, p.edadEn(LocalDate.of(2026, 6, 14)));
        assertEquals(26, p.edadEn(LocalDate.of(2026, 6, 15)));
    }

    @Test
    public void elTipoDependeDeLaEdadALaFechaDeSalida() {
        LocalDate salida = LocalDate.of(2026, 6, 1);
        assertEquals(TipoPasajero.INFANTE, new Pasajero(DatosTest.pasajero("1", LocalDate.of(2024, 1, 1), false)).tipoEn(salida));
        assertEquals(TipoPasajero.MENOR, new Pasajero(DatosTest.pasajero("2", LocalDate.of(2023, 6, 1), false)).tipoEn(salida));
        assertEquals(TipoPasajero.MENOR, new Pasajero(DatosTest.pasajero("3", LocalDate.of(2014, 6, 2), false)).tipoEn(salida));
        assertEquals(TipoPasajero.ADULTO, new Pasajero(DatosTest.pasajero("4", LocalDate.of(2014, 6, 1), false)).tipoEn(salida));
    }

    @Test
    public void seOrdenanPorApellido() {
        List<Pasajero> lista = new ArrayList<>();
        lista.add(new Pasajero(new DatosPasajero("Ana", "Zapata", "1", LocalDate.of(1990, 1, 1), false, "", "")));
        lista.add(new Pasajero(new DatosPasajero("Luis", "Alvarez", "2", LocalDate.of(1990, 1, 1), false, "", "")));
        lista.add(new Pasajero(new DatosPasajero("Ana", "Alvarez", "3", LocalDate.of(1990, 1, 1), false, "", "")));
        Collections.sort(lista);
        assertEquals("3", lista.get(0).getDocumento());
        assertEquals("2", lista.get(1).getDocumento());
        assertEquals("1", lista.get(2).getDocumento());
    }

    @Test(expected = IllegalArgumentException.class)
    public void noSeCreaSinDocumento() {
        new Pasajero(DatosTest.pasajero("", LocalDate.of(1990, 1, 1), false));
    }
}
