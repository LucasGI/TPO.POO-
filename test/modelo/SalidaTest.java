package modelo;

import excepcion.AsignacionInvalidaException;
import excepcion.CupoInsuficienteException;
import excepcion.PasajeroInvalidoException;
import excepcion.ReglaNegocioException;
import org.junit.Before;
import org.junit.Test;
import servicio.PoliticaCancelacionCliente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SalidaTest {
    private Salida salida;
    private Cliente cliente;
    private Vendedor vendedor;

    @Before
    public void preparar() throws ReglaNegocioException {
        // cupo minimo 2 y maximo 4
        salida = DatosTest.tourAventura().crearSalida(1, DatosTest.HOY.plusDays(40), 4);
        cliente = DatosTest.cliente();
        vendedor = DatosTest.vendedor();
    }

    @Test
    public void quedaConfirmadaAlLlegarAlCupoMinimo() throws ReglaNegocioException {
        salida.reservar(1, DatosTest.HOY, cliente, vendedor, DatosTest.adultos(1));
        assertEquals(EstadoSalida.PROGRAMADA, salida.getEstado());
        List<DatosPasajero> otro = new ArrayList<>();
        otro.add(DatosTest.adulto("2001"));
        salida.reservar(2, DatosTest.HOY, cliente, vendedor, otro);
        assertEquals(EstadoSalida.CONFIRMADA, salida.getEstado());
    }

    @Test
    public void quedaCompletaYNoAdmiteMasReservas() throws ReglaNegocioException {
        salida.reservar(1, DatosTest.HOY, cliente, vendedor, DatosTest.adultos(4));
        assertEquals(EstadoSalida.COMPLETA, salida.getEstado());
        assertEquals(0, salida.lugaresDisponibles());
        try {
            salida.reservar(2, DatosTest.HOY, cliente, vendedor, DatosTest.adultos(1));
            fail("Deberia rechazar la reserva");
        } catch (CupoInsuficienteException e) {
            assertEquals(1, salida.getReservas().size());
        }
    }

    @Test(expected = CupoInsuficienteException.class)
    public void rechazaSiPidenMasLugaresQueLosDisponibles() throws ReglaNegocioException {
        salida.reservar(1, DatosTest.HOY, cliente, vendedor, DatosTest.adultos(5));
    }

    @Test
    public void unPasajeroInvalidoNoDejaLaReservaCargada() throws ReglaNegocioException {
        List<DatosPasajero> datos = DatosTest.adultos(1);
        datos.add(DatosTest.pasajero("9", LocalDate.of(2016, 1, 1), false));
        try {
            salida.reservar(1, DatosTest.HOY, cliente, vendedor, datos);
            fail("Deberia rechazar al menor");
        } catch (PasajeroInvalidoException e) {
            assertEquals(0, salida.getReservas().size());
            assertEquals(0, salida.lugaresOcupados());
        }
    }

    @Test
    public void alCancelarSeLiberanLosLugares() throws ReglaNegocioException {
        Reserva r = salida.reservar(1, DatosTest.HOY, cliente, vendedor, DatosTest.adultos(4));
        r.cancelar("Prueba", DatosTest.HOY, new PoliticaCancelacionCliente());
        assertEquals(0, salida.lugaresOcupados());
        assertEquals(EstadoSalida.PROGRAMADA, salida.getEstado());
    }

    @Test(expected = AsignacionInvalidaException.class)
    public void elGuiaDebeEstarHabilitadoEnLaCategoria() throws ReglaNegocioException {
        salida.asignarGuia(DatosTest.guia(Categoria.CULTURAL), new ArrayList<Salida>());
    }

    @Test
    public void elGuiaNoPuedeTenerSalidasSuperpuestas() throws ReglaNegocioException {
        Guia guia = DatosTest.guia(Categoria.AVENTURA);
        Salida otra = salida.getTour().crearSalida(2, salida.getFechaInicio().plusDays(1), 4);
        List<Salida> todas = new ArrayList<>();
        todas.add(salida);
        todas.add(otra);
        salida.asignarGuia(guia, todas);
        try {
            otra.asignarGuia(guia, todas);
            fail("Deberia rechazar la superposicion");
        } catch (AsignacionInvalidaException e) {
            assertTrue(e.getMessage().contains("otra salida"));
        }
        // una salida que empieza despues del fin de la primera si se puede
        Salida despues = salida.getTour().crearSalida(3, salida.getFechaFin().plusDays(1), 4);
        todas.add(despues);
        despues.asignarGuia(guia, todas);
        assertEquals(guia, despues.getGuia());
    }

    @Test(expected = AsignacionInvalidaException.class)
    public void elVehiculoDebeAlcanzarParaElCupo() throws ReglaNegocioException {
        salida.asignarVehiculo(new Vehiculo("AA111AA", 3, TipoVehiculo.COMBI));
    }

    @Test
    public void noSeIniciaSinGuiaNiLista() throws ReglaNegocioException {
        Reserva r = salida.reservar(1, DatosTest.HOY, cliente, vendedor, DatosTest.adultos(2));
        r.actualizarTotal(20000, DatosTest.HOY, "prueba");
        r.registrarPago(r.getTotal(), MedioPago.EFECTIVO, 1, DatosTest.HOY);
        try {
            salida.iniciar();
            fail("Falta el guia");
        } catch (ReglaNegocioException e) {
            assertTrue(e.getMessage().contains("guia"));
        }
        salida.asignarGuia(DatosTest.guia(Categoria.AVENTURA), new ArrayList<Salida>());
        try {
            salida.iniciar();
            fail("Falta la lista");
        } catch (ReglaNegocioException e) {
            assertTrue(e.getMessage().contains("lista"));
        }
        salida.emitirListaPasajeros();
        salida.iniciar();
        assertEquals(EstadoSalida.EN_CURSO, salida.getEstado());
    }

    @Test
    public void laListaSoloIncluyeReservasConfirmadasOrdenadas() throws ReglaNegocioException {
        List<DatosPasajero> datos = new ArrayList<>();
        datos.add(new DatosPasajero("Zoe", "Zapata", "1", LocalDate.of(1990, 1, 1), false, "", ""));
        datos.add(new DatosPasajero("Ana", "Acosta", "2", LocalDate.of(1990, 1, 1), false, "", ""));
        Reserva confirmada = salida.reservar(1, DatosTest.HOY, cliente, vendedor, datos);
        confirmada.actualizarTotal(20000, DatosTest.HOY, "prueba");
        confirmada.registrarPago(confirmada.montoSenia(), MedioPago.EFECTIVO, 1, DatosTest.HOY);
        salida.reservar(2, DatosTest.HOY, cliente, vendedor, DatosTest.adultos(1));
        List<Pasajero> lista = salida.emitirListaPasajeros();
        assertEquals(2, lista.size());
        assertEquals("Acosta", lista.get(0).getApellido());
    }

    @Test
    public void alFinalizarLiquidaAlGuiaConElCincoPorCientoDeAdicionales() throws ReglaNegocioException {
        List<DatosPasajero> datos = DatosTest.adultos(2);
        Reserva r = salida.reservar(1, DatosTest.HOY, cliente, vendedor, datos);
        r.agregarAdicional(r.getPasajeros().get(0), salida.getTour().buscarServicio("RAF"));
        r.actualizarTotal(30000, DatosTest.HOY, "prueba");
        r.registrarPago(r.getTotal(), MedioPago.EFECTIVO, 1, DatosTest.HOY);
        salida.asignarGuia(DatosTest.guia(Categoria.AVENTURA), new ArrayList<Salida>());
        salida.emitirListaPasajeros();
        salida.iniciar();
        Liquidacion liquidacion = salida.finalizar(DatosTest.HOY.plusDays(43));
        // 3 dias x 1000 + 5% de (seguro 1000 x 2 + rafting 5000)
        assertEquals(3000, liquidacion.getMontoFijo(), 0.01);
        assertEquals(350, liquidacion.getMontoAdicionales(), 0.01);
        assertEquals(3350, liquidacion.total(), 0.01);
        assertEquals(EstadoReserva.FINALIZADA, r.getEstado());
    }

    @Test
    public void noSeLiquidaDosVeces() throws ReglaNegocioException {
        Reserva r = salida.reservar(1, DatosTest.HOY, cliente, vendedor, DatosTest.adultos(2));
        r.actualizarTotal(1000, DatosTest.HOY, "prueba");
        r.registrarPago(1000, MedioPago.EFECTIVO, 1, DatosTest.HOY);
        salida.asignarGuia(DatosTest.guia(Categoria.AVENTURA), new ArrayList<Salida>());
        salida.emitirListaPasajeros();
        salida.iniciar();
        salida.finalizar(DatosTest.HOY);
        try {
            salida.finalizar(DatosTest.HOY);
            fail("Ya estaba finalizada");
        } catch (ReglaNegocioException e) {
            assertEquals(EstadoSalida.FINALIZADA, salida.getEstado());
        }
    }
}
