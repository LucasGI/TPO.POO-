package controlador;

import excepcion.CupoInsuficienteException;
import excepcion.ReglaNegocioException;
import modelo.Cancelacion;
import modelo.Categoria;
import modelo.DatosPasajero;
import modelo.DatosTest;
import modelo.EstadoReserva;
import modelo.EstadoSalida;
import modelo.MedioPago;
import modelo.Reserva;
import modelo.Salida;
import org.junit.Before;
import org.junit.Test;
import persistencia.ArchivoTexto;
import persistencia.BaseDatos;
import servicio.CalculadoraPrecio;
import servicio.Promocion;
import servicio.PromocionAnticipada;
import servicio.PromocionGrupo;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

// prueba los controladores con los repositorios en memoria, sin tocar el archivo de datos
public class ControladoresTest {
    private BaseDatos base;
    private ReservaController reservas;
    private SalidaController salidas;
    private CatalogoController catalogo;

    @Before
    public void preparar() throws ReglaNegocioException {
        base = new BaseDatos();
        List<Promocion> promociones = new ArrayList<>();
        promociones.add(new PromocionAnticipada());
        promociones.add(new PromocionGrupo());
        catalogo = new CatalogoController(base.getTours(), base.getClientes(), base.getEmpleados(),
                base.getVehiculos(), base.getProveedores());
        reservas = new ReservaController(base.getReservas(), base.getSalidas(), base.getClientes(),
                base.getEmpleados(), new CalculadoraPrecio(promociones));
        salidas = new SalidaController(base.getSalidas(), base.getTours(), base.getEmpleados(), base.getVehiculos(),
                base.getReservas(), new ArchivoTexto("build/test-listas"));

        catalogo.altaTour(DatosTest.tourAventura());
        catalogo.registrarCliente(DatosTest.cliente());
        catalogo.registrarEmpleado(DatosTest.vendedor());
        catalogo.registrarEmpleado(DatosTest.guia(Categoria.AVENTURA));
        salidas.programarSalida("AV01", DatosTest.HOY.plusDays(30), 4, DatosTest.HOY);
    }

    @Test
    public void creaLaReservaComoEnElDiagramaDeSecuencia() throws ReglaNegocioException {
        List<DatosPasajero> datos = DatosTest.adultos(2);
        datos.get(0).agregarAdicional("RAF");
        Reserva r = reservas.crearReserva(1, "30111222", 101, datos, DatosTest.HOY);
        // 2 x 10000 + seguro obligatorio 2 x 1000 + rafting 5000
        assertEquals(27000, r.getTotal(), 0.01);
        assertEquals(EstadoReserva.PENDIENTE, r.getEstado());
        assertEquals(EstadoSalida.CONFIRMADA, r.getSalida().getEstado());
        assertEquals(r, base.getReservas().buscarPorId(1));
    }

    @Test
    public void unCodigoDeAdicionalInvalidoNoDejaNadaCargado() throws ReglaNegocioException {
        List<DatosPasajero> datos = DatosTest.adultos(1);
        datos.get(0).agregarAdicional("XXX");
        try {
            reservas.crearReserva(1, "30111222", 101, datos, DatosTest.HOY);
            fail("El servicio no existe");
        } catch (ReglaNegocioException e) {
            assertEquals(0, base.getReservas().cantidad());
            assertEquals(0, salidas.buscarSalida(1).lugaresOcupados());
        }
    }

    @Test(expected = CupoInsuficienteException.class)
    public void rechazaLaReservaSinCupo() throws ReglaNegocioException {
        reservas.crearReserva(1, "30111222", 101, DatosTest.adultos(5), DatosTest.HOY);
    }

    @Test(expected = ReglaNegocioException.class)
    public void elLegajoTieneQueSerDeUnVendedor() throws ReglaNegocioException {
        reservas.crearReserva(1, "30111222", 201, DatosTest.adultos(1), DatosTest.HOY);
    }

    @Test
    public void cancelaComoEnElDiagramaDeSecuencia() throws ReglaNegocioException {
        Reserva r = reservas.crearReserva(1, "30111222", 101, DatosTest.adultos(2), DatosTest.HOY);
        reservas.registrarPago(1, r.getTotal(), MedioPago.EFECTIVO, 1, DatosTest.HOY);
        // faltan 30 dias: devuelve el 90%
        Cancelacion c = reservas.cancelarReserva(1, "Cambio de planes", false, DatosTest.HOY);
        assertEquals(r.getTotal() * 0.9, c.getImporteDevuelto(), 0.01);
        assertEquals(0, salidas.buscarSalida(1).lugaresOcupados());
        assertEquals(EstadoSalida.PROGRAMADA, salidas.buscarSalida(1).getEstado());
    }

    @Test(expected = ReglaNegocioException.class)
    public void noCancelaUnaReservaInexistente() throws ReglaNegocioException {
        reservas.cancelarReserva(99, "x", false, DatosTest.HOY);
    }

    @Test
    public void controlaElCupoMinimoSieteDiasAntes() throws ReglaNegocioException {
        Reserva r = reservas.crearReserva(1, "30111222", 101, DatosTest.adultos(1), DatosTest.HOY);
        reservas.registrarPago(1, r.getTotal(), MedioPago.EFECTIVO, 1, DatosTest.HOY);
        // 8 dias antes todavia no se cancela
        assertTrue(salidas.controlarVencimientos(DatosTest.HOY.plusDays(22)).isEmpty());
        List<String> novedades = salidas.controlarVencimientos(DatosTest.HOY.plusDays(23));
        assertEquals(1, novedades.size());
        Salida s = salidas.buscarSalida(1);
        assertEquals(EstadoSalida.CANCELADA, s.getEstado());
        // cancela la agencia: se devuelve todo lo pagado
        assertEquals(r.totalPagado(), r.getCancelacion().getImporteDevuelto(), 0.01);
    }

    @Test
    public void anulaLaReservaConSaldoImpagoAlVencimiento() throws ReglaNegocioException {
        Reserva conSaldo = reservas.crearReserva(1, "30111222", 101, DatosTest.adultos(2), DatosTest.HOY);
        reservas.registrarPago(1, conSaldo.montoSenia(), MedioPago.EFECTIVO, 1, DatosTest.HOY);
        Reserva pagada = reservas.crearReserva(1, "30111222", 101, DatosTest.adultos(1), DatosTest.HOY);
        reservas.registrarPago(2, pagada.getTotal(), MedioPago.EFECTIVO, 1, DatosTest.HOY);
        salidas.controlarVencimientos(DatosTest.HOY.plusDays(20));
        assertEquals(EstadoReserva.ANULADA, conSaldo.getEstado());
        assertEquals(EstadoReserva.CONFIRMADA, pagada.getEstado());
    }

    @Test
    public void anulaLaReservaPendienteSinSeniaALas48Horas() throws ReglaNegocioException {
        Reserva r = reservas.crearReserva(1, "30111222", 101, DatosTest.adultos(1), DatosTest.HOY);
        salidas.controlarVencimientos(DatosTest.HOY.plusDays(1));
        assertEquals(EstadoReserva.PENDIENTE, r.getEstado());
        salidas.controlarVencimientos(DatosTest.HOY.plusDays(2));
        assertEquals(EstadoReserva.ANULADA, r.getEstado());
    }

    @Test(expected = ReglaNegocioException.class)
    public void noProgramaSalidasEnElPasado() throws ReglaNegocioException {
        salidas.programarSalida("AV01", DatosTest.HOY.minusDays(1), 4, DatosTest.HOY);
    }
}
