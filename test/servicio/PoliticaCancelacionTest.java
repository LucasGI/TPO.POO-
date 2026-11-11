package servicio;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PoliticaCancelacionTest {

    @Test
    public void escalaDelCliente() {
        PoliticaCancelacion politica = new PoliticaCancelacionCliente();
        assertEquals(0.90, politica.porcentajeDevolucion(45), 0.001);
        assertEquals(0.90, politica.porcentajeDevolucion(30), 0.001);
        assertEquals(0.50, politica.porcentajeDevolucion(29), 0.001);
        assertEquals(0.50, politica.porcentajeDevolucion(15), 0.001);
        assertEquals(0.20, politica.porcentajeDevolucion(14), 0.001);
        assertEquals(0.20, politica.porcentajeDevolucion(7), 0.001);
        assertEquals(0.0, politica.porcentajeDevolucion(6), 0.001);
        assertEquals(0.0, politica.porcentajeDevolucion(0), 0.001);
        assertFalse(politica.esCancelacionDeAgencia());
    }

    @Test
    public void laAgenciaDevuelveSiempreTodo() {
        PoliticaCancelacion politica = new PoliticaCancelacionAgencia();
        assertEquals(1.0, politica.porcentajeDevolucion(60), 0.001);
        assertEquals(1.0, politica.porcentajeDevolucion(1), 0.001);
        assertTrue(politica.esCancelacionDeAgencia());
    }
}
