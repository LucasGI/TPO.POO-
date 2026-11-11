package modelo;

import excepcion.EstadoInvalidoException;
import excepcion.PagoInvalidoException;
import excepcion.ReglaNegocioException;
import org.junit.Test;
import servicio.PoliticaCancelacionAgencia;
import servicio.PoliticaCancelacionCliente;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ReservaTest {

    // reserva de 1 adulto con total fijo de 100000 para que las cuentas sean simples
    private Reserva reserva(int diasHastaSalida) throws ReglaNegocioException {
        Salida salida = DatosTest.tourCultural().crearSalida(1, DatosTest.HOY.plusDays(diasHastaSalida), 10);
        Reserva r = salida.reservar(1, DatosTest.HOY, DatosTest.cliente(), DatosTest.vendedor(), DatosTest.adultos(1));
        r.actualizarTotal(100000, DatosTest.HOY, "prueba");
        return r;
    }

    @Test
    public void conLaSeniaDelTreintaPorCientoQuedaConfirmada() throws ReglaNegocioException {
        Reserva r = reserva(40);
        r.registrarPago(29999, MedioPago.EFECTIVO, 1, DatosTest.HOY);
        assertEquals(EstadoReserva.PENDIENTE, r.getEstado());
        r.registrarPago(1, MedioPago.EFECTIVO, 1, DatosTest.HOY);
        assertEquals(EstadoReserva.CONFIRMADA, r.getEstado());
        assertEquals(70000, r.saldo(), 0.01);
    }

    @Test
    public void tarjetaEnCuotasTieneRecargoDelSietePorCiento() throws ReglaNegocioException {
        Reserva r = reserva(40);
        Pago pago = r.registrarPago(50000, MedioPago.TARJETA, 3, DatosTest.HOY);
        assertEquals(3500, pago.getRecargo(), 0.01);
        assertEquals(53500, pago.getTotalCobrado(), 0.01);
        // el recargo no descuenta saldo
        assertEquals(50000, r.saldo(), 0.01);
        Pago enUnPago = r.registrarPago(10000, MedioPago.TARJETA, 1, DatosTest.HOY);
        assertEquals(0, enUnPago.getRecargo(), 0.01);
    }

    @Test(expected = PagoInvalidoException.class)
    public void noSePuedePagarMasQueElSaldo() throws ReglaNegocioException {
        reserva(40).registrarPago(100001, MedioPago.EFECTIVO, 1, DatosTest.HOY);
    }

    @Test(expected = PagoInvalidoException.class)
    public void soloLaTarjetaAdmiteCuotas() throws ReglaNegocioException {
        reserva(40).registrarPago(1000, MedioPago.EFECTIVO, 3, DatosTest.HOY);
    }

    @Test(expected = PagoInvalidoException.class)
    public void aMenosDeDiezDiasHayQuePagarElSaldoCompleto() throws ReglaNegocioException {
        reserva(9).registrarPago(30000, MedioPago.EFECTIVO, 1, DatosTest.HOY);
    }

    @Test
    public void cancelacionDelClienteSegunAnticipacion() throws ReglaNegocioException {
        assertEquals(90000, cancelarConDias(30), 0.01);
        assertEquals(50000, cancelarConDias(29), 0.01);
        assertEquals(50000, cancelarConDias(15), 0.01);
        assertEquals(20000, cancelarConDias(14), 0.01);
        assertEquals(20000, cancelarConDias(7), 0.01);
        assertEquals(0, cancelarConDias(6), 0.01);
    }

    private double cancelarConDias(int dias) throws ReglaNegocioException {
        // se paga todo con anticipacion y se cancela "dias" antes de la salida
        Reserva r = reserva(60);
        r.registrarPago(100000, MedioPago.TRANSFERENCIA, 1, DatosTest.HOY);
        LocalDate fechaCancelacion = r.getSalida().getFechaInicio().minusDays(dias);
        Cancelacion c = r.cancelar("prueba", fechaCancelacion, new PoliticaCancelacionCliente());
        assertEquals(EstadoReserva.ANULADA, r.getEstado());
        assertEquals(MedioPago.TRANSFERENCIA, c.getMedio());
        return c.getImporteDevuelto();
    }

    @Test
    public void siCancelaLaAgenciaSeDevuelveTodo() throws ReglaNegocioException {
        Reserva r = reserva(3);
        r.registrarPago(100000, MedioPago.EFECTIVO, 1, DatosTest.HOY);
        Cancelacion c = r.cancelar("Clima", DatosTest.HOY, new PoliticaCancelacionAgencia());
        assertEquals(100000, c.getImporteDevuelto(), 0.01);
    }

    @Test
    public void sinPagosLaDevolucionEsCero() throws ReglaNegocioException {
        Cancelacion c = reserva(40).cancelar("prueba", DatosTest.HOY, new PoliticaCancelacionCliente());
        assertEquals(0, c.getImporteDevuelto(), 0.01);
        assertNull(c.getMedio());
    }

    @Test(expected = EstadoInvalidoException.class)
    public void noSeCancelaDosVeces() throws ReglaNegocioException {
        Reserva r = reserva(40);
        r.cancelar("uno", DatosTest.HOY, new PoliticaCancelacionCliente());
        r.cancelar("dos", DatosTest.HOY, new PoliticaCancelacionCliente());
    }

    @Test(expected = ReglaNegocioException.class)
    public void elReemplazoSeHaceHastaTresDiasAntes() throws ReglaNegocioException {
        Reserva r = reserva(2);
        r.reemplazarPasajero("1001", DatosTest.adulto("5000"), DatosTest.HOY);
    }

    @Test
    public void elReemplazoConservaLosAdicionales() throws ReglaNegocioException {
        Reserva r = reserva(20);
        r.agregarAdicional(r.getPasajeros().get(0), r.getSalida().getTour().buscarServicio("ALM"));
        r.reemplazarPasajero("1001", DatosTest.adulto("5000"), DatosTest.HOY);
        assertEquals("5000", r.getPasajeros().get(0).getDocumento());
        assertEquals(2000, r.totalAdicionales(), 0.01);
    }

    @Test
    public void conPagosLaDiferenciaQuedaEnUnaNotaDeAjuste() throws ReglaNegocioException {
        Reserva r = reserva(40);
        r.actualizarTotal(120000, DatosTest.HOY, "sin pagos no hay nota");
        assertEquals(0, r.getNotas().size());
        r.registrarPago(40000, MedioPago.EFECTIVO, 1, DatosTest.HOY);
        r.actualizarTotal(150000, DatosTest.HOY, "agrega pasajero");
        assertEquals(1, r.getNotas().size());
        assertEquals(30000, r.getNotas().get(0).getImporte(), 0.01);
    }
}
