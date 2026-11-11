package servicio;

import excepcion.ReglaNegocioException;
import modelo.DatosPasajero;
import modelo.DatosTest;
import modelo.Reserva;
import modelo.Salida;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CalculadoraPrecioTest {
    private CalculadoraPrecio calculadora;

    @Before
    public void preparar() {
        List<Promocion> promociones = new ArrayList<>();
        promociones.add(new PromocionAnticipada());
        promociones.add(new PromocionGrupo());
        calculadora = new CalculadoraPrecio(promociones);
    }

    // tarifa base 10000 en el tour cultural de prueba
    private Reserva reservar(int diasHastaSalida, List<DatosPasajero> datos) throws ReglaNegocioException {
        Salida salida = DatosTest.tourCultural().crearSalida(1, DatosTest.HOY.plusDays(diasHastaSalida), 10);
        return salida.reservar(1, DatosTest.HOY, DatosTest.cliente(), DatosTest.vendedor(), datos);
    }

    @Test
    public void aplicaElFactorSegunElTipoDePasajero() throws ReglaNegocioException {
        LocalDate salida = DatosTest.HOY.plusDays(30);
        List<DatosPasajero> datos = new ArrayList<>();
        datos.add(DatosTest.pasajero("1", LocalDate.of(1990, 1, 1), false));
        datos.add(DatosTest.pasajero("2", salida.minusYears(8), false));
        datos.add(DatosTest.pasajero("3", salida.minusYears(1), false));
        // 10000 + 6000 + 0
        assertEquals(16000, calculadora.calcularTotal(reservar(30, datos)), 0.01);
    }

    @Test
    public void elJubiladoTieneDiezPorCientoDeDescuento() throws ReglaNegocioException {
        List<DatosPasajero> datos = new ArrayList<>();
        datos.add(DatosTest.pasajero("1", LocalDate.of(1955, 1, 1), true));
        assertEquals(9000, calculadora.calcularTotal(reservar(30, datos)), 0.01);
    }

    @Test
    public void reservaAnticipadaTieneQuincePorCiento() throws ReglaNegocioException {
        assertEquals(8500, calculadora.calcularTotal(reservar(61, DatosTest.adultos(1))), 0.01);
        assertEquals(10000, calculadora.calcularTotal(reservar(60, DatosTest.adultos(1))), 0.01);
    }

    @Test
    public void grupoDeSeisTieneDiezPorCiento() throws ReglaNegocioException {
        assertEquals(54000, calculadora.calcularTotal(reservar(30, DatosTest.adultos(6))), 0.01);
        assertEquals(50000, calculadora.calcularTotal(reservar(30, DatosTest.adultos(5))), 0.01);
    }

    @Test
    public void lasPromocionesNoSeAcumulan() throws ReglaNegocioException {
        Reserva r = reservar(90, DatosTest.adultos(6));
        assertEquals(0.15, calculadora.mejorDescuento(r), 0.001);
        assertEquals(51000, calculadora.calcularTotal(r), 0.01);
    }

    @Test
    public void losAdicionalesSeSumanSinDescuento() throws ReglaNegocioException {
        Reserva r = reservar(90, DatosTest.adultos(1));
        r.agregarAdicional(r.getPasajeros().get(0), r.getSalida().getTour().buscarServicio("ALM"));
        // 10000 con 15% + almuerzo 2000 completo
        assertEquals(10500, calculadora.calcularTotal(r), 0.01);
    }

    @Test
    public void sinPromocionesNoHayDescuento() throws ReglaNegocioException {
        CalculadoraPrecio sinPromos = new CalculadoraPrecio(new ArrayList<Promocion>());
        Reserva r = reservar(90, DatosTest.adultos(6));
        assertEquals(60000, sinPromos.calcularTotal(r), 0.01);
        assertNull(sinPromos.mejorPromocion(r));
    }
}
