package app;

import controlador.CatalogoController;
import controlador.ReservaController;
import controlador.SalidaController;
import excepcion.ReglaNegocioException;
import modelo.Administrador;
import modelo.Categoria;
import modelo.Cliente;
import modelo.Coordinador;
import modelo.DatosPasajero;
import modelo.Guia;
import modelo.MedioPago;
import modelo.Proveedor;
import modelo.Reserva;
import modelo.ServicioAdicional;
import modelo.Tarifa;
import modelo.Temporada;
import modelo.TipoServicio;
import modelo.TipoVehiculo;
import modelo.Tour;
import modelo.TourAventura;
import modelo.TourCultural;
import modelo.TourNaturaleza;
import modelo.Vehiculo;
import modelo.Vendedor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// carga datos de ejemplo la primera vez que se ejecuta el programa
// las fechas se calculan a partir de hoy para que siempre sirvan
public class DatosDePrueba {
    private CatalogoController catalogo;
    private SalidaController salidas;
    private ReservaController reservas;

    public DatosDePrueba(CatalogoController catalogo, SalidaController salidas, ReservaController reservas) {
        this.catalogo = catalogo;
        this.salidas = salidas;
        this.reservas = reservas;
    }

    public void cargar(LocalDate hoy) throws ReglaNegocioException {
        cargarPersonal();
        cargarTours(hoy);
        cargarClientes();
        cargarSalidasYReservas(hoy);
    }

    private void cargarPersonal() throws ReglaNegocioException {
        catalogo.registrarEmpleado(new Vendedor(101, "Martina", "Gomez", "33111222", "1155550101", 0.03));
        catalogo.registrarEmpleado(new Vendedor(102, "Joaquin", "Ruiz", "34222333", "1155550102", 0.025));

        Guia sofia = new Guia(201, "Sofia", "Perez", "30555666", "1155550201", 45000);
        sofia.habilitarCategoria(Categoria.AVENTURA);
        sofia.habilitarCategoria(Categoria.NATURALEZA);
        sofia.agregarIdioma("Espanol");
        sofia.agregarIdioma("Ingles");
        catalogo.registrarEmpleado(sofia);

        Guia diego = new Guia(202, "Diego", "Fernandez", "29444555", "1155550202", 38000);
        diego.habilitarCategoria(Categoria.CULTURAL);
        diego.agregarIdioma("Espanol");
        diego.agregarIdioma("Portugues");
        catalogo.registrarEmpleado(diego);

        Guia lucia = new Guia(203, "Lucia", "Torres", "31777888", "1155550203", 40000);
        lucia.habilitarCategoria(Categoria.NATURALEZA);
        lucia.habilitarCategoria(Categoria.CULTURAL);
        lucia.agregarIdioma("Espanol");
        catalogo.registrarEmpleado(lucia);

        catalogo.registrarEmpleado(new Coordinador(301, "Pablo", "Sosa", "27888999", "1155550301"));
        catalogo.registrarEmpleado(new Administrador(401, "Laura", "Benitez", "26999000", "1155550401"));

        catalogo.registrarVehiculo(new Vehiculo("AB123CD", 15, TipoVehiculo.COMBI));
        catalogo.registrarVehiculo(new Vehiculo("AE456FG", 25, TipoVehiculo.MINIBUS));
        catalogo.registrarVehiculo(new Vehiculo("AF789HJ", 45, TipoVehiculo.COLECTIVO));

        catalogo.registrarProveedor(new Proveedor("Hosteria El Mirador", "30-71111111-5", "mirador@mail.com",
                TipoServicio.ALOJAMIENTO));
        catalogo.registrarProveedor(new Proveedor("Transportes del Sur", "30-72222222-6", "tdsur@mail.com",
                TipoServicio.TRASLADO));
        catalogo.registrarProveedor(new Proveedor("Navegaciones Lago Azul", "30-73333333-7", "lagoazul@mail.com",
                TipoServicio.EXCURSION));
    }

    private void cargarTours(LocalDate hoy) throws ReglaNegocioException {
        Proveedor hosteria = catalogo.buscarProveedor("30-71111111-5");
        Proveedor transporte = catalogo.buscarProveedor("30-72222222-6");
        Proveedor navegacion = catalogo.buscarProveedor("30-73333333-7");

        Tour trekking = new TourAventura("AV01", "Trekking Cerro Catedral", "Ascenso guiado con noche en refugio",
                3, 4, 12, "Media", 14);
        trekking.agregarActividad(1, 1, "08:00", "Traslado a la base del cerro", "Bariloche", transporte);
        trekking.agregarActividad(1, 2, "10:00", "Ascenso al refugio Frey", "Cerro Catedral", null);
        trekking.agregarActividad(2, 1, "09:00", "Travesia a laguna Tonchek", "Cerro Catedral", null);
        trekking.agregarActividad(3, 1, "09:00", "Descenso y regreso", "Bariloche", transporte);
        trekking.agregarServicio(new ServicioAdicional("SEG", "Seguro de viaje", 12000, TipoServicio.SEGURO, true));
        trekking.agregarServicio(new ServicioAdicional("RAF", "Rafting rio Manso", 35000, TipoServicio.EXCURSION, false));
        trekking.agregarServicio(new ServicioAdicional("TRA", "Traslado aeropuerto", 8000, TipoServicio.TRASLADO, false));
        trekking.agregarTarifa(new Tarifa(Temporada.MEDIA, hoy.minusDays(30), hoy.plusDays(90), 180000));
        trekking.agregarTarifa(new Tarifa(Temporada.ALTA, hoy.plusDays(91), hoy.plusDays(365), 230000));
        catalogo.altaTour(trekking);

        Tour ciudad = new TourCultural("CU01", "Buenos Aires Historica", "San Telmo, Plaza de Mayo y La Boca",
                1, 5, 20);
        ciudad.agregarActividad(1, 1, "09:30", "Recorrido por San Telmo", "San Telmo", null);
        ciudad.agregarActividad(1, 2, "12:30", "Almuerzo en bodegon", "La Boca", null);
        ciudad.agregarActividad(1, 3, "15:00", "Visita a Plaza de Mayo y Cabildo", "Monserrat", null);
        ciudad.agregarServicio(new ServicioAdicional("ALM", "Almuerzo tipico", 15000, TipoServicio.COMIDA, false));
        ciudad.agregarServicio(new ServicioAdicional("TRA", "Traslado desde hotel", 6000, TipoServicio.TRASLADO, false));
        ciudad.agregarTarifa(new Tarifa(Temporada.BAJA, hoy.minusDays(30), hoy.plusDays(90), 45000));
        ciudad.agregarTarifa(new Tarifa(Temporada.ALTA, hoy.plusDays(91), hoy.plusDays(365), 55000));
        catalogo.altaTour(ciudad);

        Tour glaciares = new TourNaturaleza("NA01", "Glaciares y lagos", "Perito Moreno y navegacion por el lago",
                4, 6, 25);
        glaciares.agregarActividad(1, 1, "14:00", "Llegada y alojamiento", "El Calafate", hosteria);
        glaciares.agregarActividad(2, 1, "08:00", "Pasarelas del glaciar Perito Moreno", "Parque Nacional", null);
        glaciares.agregarActividad(3, 1, "09:00", "Navegacion por el lago Argentino", "Puerto Bandera", navegacion);
        glaciares.agregarActividad(4, 1, "10:00", "Regreso", "El Calafate", transporte);
        glaciares.agregarServicio(new ServicioAdicional("NAV", "Minitrekking sobre hielo", 40000, TipoServicio.EXCURSION, false));
        glaciares.agregarServicio(new ServicioAdicional("SEG", "Seguro de viaje", 12000, TipoServicio.SEGURO, false));
        glaciares.agregarTarifa(new Tarifa(Temporada.MEDIA, hoy.minusDays(30), hoy.plusDays(60), 320000));
        glaciares.agregarTarifa(new Tarifa(Temporada.ALTA, hoy.plusDays(61), hoy.plusDays(365), 380000));
        catalogo.altaTour(glaciares);
    }

    private void cargarClientes() throws ReglaNegocioException {
        catalogo.registrarCliente(new Cliente("Ana", "Lopez", "30111222", "1166660001", "ana@mail.com", "Argentina"));
        catalogo.registrarCliente(new Cliente("Carlos", "Mendez", "28999888", "1166660002", "carlos@mail.com", "Argentina"));
        catalogo.registrarCliente(new Cliente("Valentina", "Rios", "35444555", "1166660003", "vale@mail.com", "Argentina"));
        catalogo.registrarCliente(new Cliente("Tomas", "Herrera", "40111333", "59899000111", "tomas@mail.com", "Uruguay"));
    }

    private void cargarSalidasYReservas(LocalDate hoy) throws ReglaNegocioException {
        // salida 1: trekking en 20 dias, queda confirmada con 4 pasajeros
        salidas.programarSalida("AV01", hoy.plusDays(20), 10, hoy);
        salidas.asignarGuia(1, 201);
        salidas.asignarVehiculo(1, "AB123CD");
        List<DatosPasajero> grupo1 = new ArrayList<>();
        grupo1.add(pasajero("Ana", "Lopez", "30111222", 1984, 5, 10, false, "Juan Lopez 1166661111", ""));
        DatosPasajero conRafting = pasajero("Martin", "Lopez", "29333444", 1982, 11, 2, false, "Juan Lopez 1166661111", "");
        conRafting.agregarAdicional("RAF");
        grupo1.add(conRafting);
        Reserva r1 = reservas.crearReserva(1, "30111222", 101, grupo1, hoy);
        reservas.registrarPago(r1.getNumero(), r1.montoSenia(), MedioPago.TRANSFERENCIA, 1, hoy);

        List<DatosPasajero> grupo2 = new ArrayList<>();
        grupo2.add(pasajero("Carlos", "Mendez", "28999888", 1980, 3, 15, false, "Elena Mendez 1166662222", "Celiaco"));
        grupo2.add(pasajero("Bruno", "Mendez", "47111222", 2009, 1, 20, false, "Elena Mendez 1166662222", ""));
        Reserva r2 = reservas.crearReserva(1, "28999888", 102, grupo2, hoy);
        reservas.registrarPago(r2.getNumero(), r2.getTotal(), MedioPago.TARJETA, 3, hoy);

        // salida 2: city tour en 5 dias, grupo de 6 con promocion y lista lista para emitir
        salidas.programarSalida("CU01", hoy.plusDays(5), 15, hoy);
        salidas.asignarGuia(2, 202);
        salidas.asignarVehiculo(2, "AB123CD");
        List<DatosPasajero> familia = new ArrayList<>();
        familia.add(pasajero("Valentina", "Rios", "35444555", 1990, 7, 1, false, "Hugo Rios 1166663333", ""));
        familia.add(pasajero("Federico", "Alvarez", "34888777", 1989, 2, 14, false, "Hugo Rios 1166663333", ""));
        familia.add(pasajero("Mia", "Alvarez", "58111222", hoy.getYear() - 8, 4, 4, false, "Hugo Rios 1166663333", ""));
        familia.add(pasajero("Leo", "Alvarez", "60222333", hoy.getYear() - 1, 1, 1, false, "Hugo Rios 1166663333", ""));
        familia.add(pasajero("Hugo", "Rios", "10222333", 1955, 9, 9, true, "Valentina Rios 1166660003", "Movilidad reducida"));
        familia.add(pasajero("Marta", "Diaz", "11333444", 1957, 6, 6, true, "Valentina Rios 1166660003", "Vegetariana"));
        Reserva r3 = reservas.crearReserva(2, "35444555", 101, familia, hoy);
        reservas.registrarPago(r3.getNumero(), r3.getTotal(), MedioPago.EFECTIVO, 1, hoy);

        // salida 3: glaciares con mas de 60 dias de anticipacion
        salidas.programarSalida("NA01", hoy.plusDays(75), 20, hoy);
        List<DatosPasajero> pareja = new ArrayList<>();
        pareja.add(pasajero("Tomas", "Herrera", "40111333", 1997, 12, 3, false, "Rosa Herrera 59899000222", ""));
        pareja.add(pasajero("Julia", "Castro", "41222444", 1998, 8, 21, false, "Rosa Herrera 59899000222", ""));
        Reserva r4 = reservas.crearReserva(3, "40111333", 102, pareja, hoy);
        reservas.registrarPago(r4.getNumero(), r4.montoSenia(), MedioPago.TARJETA, 1, hoy);

        // salida 4: en 6 dias y sin cupo minimo, se cancela sola al iniciar el sistema
        salidas.programarSalida("AV01", hoy.plusDays(6), 8, hoy);
        List<DatosPasajero> solo = new ArrayList<>();
        solo.add(pasajero("Carlos", "Mendez", "28999888", 1980, 3, 15, false, "Elena Mendez 1166662222", "Celiaco"));
        reservas.crearReserva(4, "28999888", 101, solo, hoy);

        // salida 5: city tour sin reservas, para probar altas
        salidas.programarSalida("CU01", hoy.plusDays(40), 20, hoy);
    }

    private DatosPasajero pasajero(String nombre, String apellido, String doc, int anio, int mes, int dia,
                                   boolean jubilado, String emergencia, String restricciones) {
        return new DatosPasajero(nombre, apellido, doc, LocalDate.of(anio, mes, dia), jubilado, emergencia, restricciones);
    }
}
