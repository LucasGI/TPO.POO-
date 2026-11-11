package vista;

import controlador.CatalogoController;
import excepcion.ReglaNegocioException;
import modelo.Actividad;
import modelo.Categoria;
import modelo.Cliente;
import modelo.Empleado;
import modelo.Guia;
import modelo.Proveedor;
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
import persistencia.ArchivoDatos;
import persistencia.BaseDatos;

import javax.swing.JButton;
import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.List;

public class PanelCatalogo extends PanelBase {
    private static final String[] TIPOS_SERVICIO = {"Excursion", "Traslado", "Seguro", "Alojamiento", "Comida"};

    private CatalogoController controlador;
    private JButton btnVerTours;
    private JButton btnAltaTour;
    private JButton btnAgregarTarifa;
    private JButton btnVerClientes;
    private JButton btnRegistrarCliente;
    private JButton btnVerPersonal;
    private JButton btnAltaGuia;
    private JButton btnAltaVendedor;
    private JButton btnVerVehiculos;
    private JButton btnAltaVehiculo;
    private JButton btnVerProveedores;
    private JButton btnAltaProveedor;

    public PanelCatalogo(JFrame ventana, ArchivoDatos archivo, BaseDatos base, CatalogoController controlador) {
        super(ventana, archivo, base);
        this.controlador = controlador;
        btnVerTours = agregarBoton("Ver tours");
        btnAltaTour = agregarBoton("Alta de tour");
        btnAgregarTarifa = agregarBoton("Agregar tarifa");
        btnVerClientes = agregarBoton("Ver clientes");
        btnRegistrarCliente = agregarBoton("Registrar cliente");
        btnVerPersonal = agregarBoton("Ver personal");
        btnAltaGuia = agregarBoton("Alta de guia");
        btnAltaVendedor = agregarBoton("Alta de vendedor");
        btnVerVehiculos = agregarBoton("Ver vehiculos");
        btnAltaVehiculo = agregarBoton("Alta de vehiculo");
        btnVerProveedores = agregarBoton("Ver proveedores");
        btnAltaProveedor = agregarBoton("Alta de proveedor");
        verTours();
    }

    @Override
    protected void procesar(Object fuente) throws ReglaNegocioException {
        if (fuente == btnVerTours) {
            verTours();
        } else if (fuente == btnAltaTour) {
            altaTour();
        } else if (fuente == btnAgregarTarifa) {
            agregarTarifa();
        } else if (fuente == btnVerClientes) {
            verClientes();
        } else if (fuente == btnRegistrarCliente) {
            registrarCliente();
        } else if (fuente == btnVerPersonal) {
            verPersonal();
        } else if (fuente == btnAltaGuia) {
            altaGuia();
        } else if (fuente == btnAltaVendedor) {
            altaVendedor();
        } else if (fuente == btnVerVehiculos) {
            verVehiculos();
        } else if (fuente == btnAltaVehiculo) {
            altaVehiculo();
        } else if (fuente == btnVerProveedores) {
            verProveedores();
        } else if (fuente == btnAltaProveedor) {
            altaProveedor();
        }
    }

    private void verTours() {
        List<String> lineas = new ArrayList<>();
        for (Tour t : controlador.listarTours()) {
            lineas.add(t.toString());
            lineas.add("  " + t.getDescripcion());
            lineas.add("  Itinerario:");
            for (Actividad a : t.getItinerario()) {
                lineas.add("    " + a);
            }
            lineas.add("  Tarifas:");
            for (Tarifa tarifa : t.getTarifas()) {
                lineas.add("    " + tarifa);
            }
            lineas.add("  Servicios adicionales:");
            for (ServicioAdicional s : t.getServicios()) {
                lineas.add("    " + s);
            }
            lineas.add("");
        }
        mostrar(lineas);
    }

    private void verClientes() {
        List<String> lineas = new ArrayList<>();
        for (Cliente c : controlador.listarClientes()) {
            lineas.add(c + " - " + c.getEmail() + " - " + c.getPaisResidencia());
        }
        mostrar(lineas);
    }

    private void verPersonal() {
        List<String> lineas = new ArrayList<>();
        for (Empleado e : controlador.listarEmpleados()) {
            lineas.add(e.toString());
        }
        mostrar(lineas);
    }

    private void verVehiculos() {
        List<String> lineas = new ArrayList<>();
        for (Vehiculo v : controlador.listarVehiculos()) {
            lineas.add(v.toString());
        }
        mostrar(lineas);
    }

    private void verProveedores() {
        List<String> lineas = new ArrayList<>();
        for (Proveedor p : controlador.listarProveedores()) {
            lineas.add(p + " - " + p.getContacto());
        }
        mostrar(lineas);
    }

    // CU01: datos del tour, actividades, servicios y tarifas
    private void altaTour() throws ReglaNegocioException {
        Formulario f = new Formulario(ventana, "Alta de tour");
        f.agregarOpciones("Categoria", new String[]{"Aventura", "Cultural", "Naturaleza"});
        f.agregarTexto("Codigo");
        f.agregarTexto("Nombre");
        f.agregarTexto("Descripcion");
        f.agregarTexto("Duracion en dias");
        f.agregarTexto("Cupo minimo");
        f.agregarTexto("Cupo maximo");
        f.agregarTitulo("Solo para aventura:");
        f.agregarTexto("Nivel de dificultad");
        f.agregarTexto("Edad minima");
        if (!f.mostrar()) {
            return;
        }
        String codigo = f.textoObligatorio("Codigo");
        String nombre = f.textoObligatorio("Nombre");
        String descripcion = f.texto("Descripcion");
        int duracion = f.entero("Duracion en dias");
        int minimo = f.entero("Cupo minimo");
        int maximo = f.entero("Cupo maximo");
        Tour tour;
        if (f.opcion("Categoria") == 0) {
            tour = new TourAventura(codigo, nombre, descripcion, duracion, minimo, maximo,
                    f.textoObligatorio("Nivel de dificultad"), f.entero("Edad minima"));
        } else if (f.opcion("Categoria") == 1) {
            tour = new TourCultural(codigo, nombre, descripcion, duracion, minimo, maximo);
        } else {
            tour = new TourNaturaleza(codigo, nombre, descripcion, duracion, minimo, maximo);
        }

        // al menos una actividad
        boolean seguir = true;
        while (seguir) {
            Formulario fa = new Formulario(ventana, "Actividad del itinerario");
            fa.agregarTexto("Dia");
            fa.agregarTexto("Orden en el dia");
            fa.agregarTexto("Horario (hh:mm)");
            fa.agregarTexto("Descripcion");
            fa.agregarTexto("Lugar");
            fa.agregarTexto("CUIT del proveedor (opcional)");
            if (fa.mostrar()) {
                try {
                    Proveedor proveedor = null;
                    String cuit = fa.texto("CUIT del proveedor (opcional)");
                    if (!cuit.isEmpty()) {
                        proveedor = controlador.buscarProveedor(cuit);
                        if (proveedor == null) {
                            mostrarError("No existe ese proveedor, la actividad queda sin proveedor.");
                        }
                    }
                    tour.agregarActividad(fa.entero("Dia"), fa.entero("Orden en el dia"), fa.texto("Horario (hh:mm)"),
                            fa.textoObligatorio("Descripcion"), fa.texto("Lugar"), proveedor);
                } catch (IllegalArgumentException e) {
                    mostrarError(e.getMessage());
                }
            }
            if (tour.getItinerario().isEmpty()) {
                if (!confirmar("El tour necesita al menos una actividad. Cargar una?")) {
                    return;
                }
            } else {
                seguir = confirmar("Agregar otra actividad?");
            }
        }

        while (confirmar("Agregar un servicio adicional?")) {
            try {
                ServicioAdicional servicio = leerServicio();
                if (servicio != null) {
                    tour.agregarServicio(servicio);
                }
            } catch (IllegalArgumentException e) {
                mostrarError(e.getMessage());
            }
        }
        if (tour.getCategoria() == Categoria.AVENTURA && !tieneSeguro(tour)) {
            Formulario fs = new Formulario(ventana, "Seguro obligatorio");
            fs.agregarTitulo("Los tours de aventura llevan seguro de viaje.");
            fs.agregarTexto("Precio del seguro por pasajero");
            if (!fs.mostrar()) {
                return;
            }
            tour.agregarServicio(new ServicioAdicional("SEG", "Seguro de viaje",
                    fs.decimal("Precio del seguro por pasajero"), TipoServicio.SEGURO, true));
        }

        // al menos una tarifa
        seguir = true;
        while (seguir) {
            try {
                Tarifa tarifa = leerTarifa("Tarifa del tour " + tour.getCodigo());
                if (tarifa != null) {
                    tour.agregarTarifa(tarifa);
                }
            } catch (ReglaNegocioException e) {
                mostrarError(e.getMessage());
            } catch (IllegalArgumentException e) {
                mostrarError(e.getMessage());
            }
            if (tour.getTarifas().isEmpty()) {
                if (!confirmar("El tour necesita al menos una tarifa. Cargar una?")) {
                    return;
                }
            } else {
                seguir = confirmar("Agregar otra tarifa?");
            }
        }

        controlador.altaTour(tour);
        verTours();
        mostrarExito("Tour dado de alta: " + tour.getCodigo() + " - " + tour.getNombre());
    }

    // CU02
    private void agregarTarifa() throws ReglaNegocioException {
        List<Tour> tours = controlador.listarTours();
        String[] opciones = new String[tours.size()];
        for (int i = 0; i < tours.size(); i++) {
            opciones[i] = tours.get(i).getCodigo() + " - " + tours.get(i).getNombre();
        }
        Formulario f = new Formulario(ventana, "Elegir tour");
        f.agregarOpciones("Tour", opciones);
        if (!f.mostrar() || tours.isEmpty()) {
            return;
        }
        Tour tour = tours.get(f.opcion("Tour"));
        Tarifa tarifa = leerTarifa("Nueva tarifa para " + tour.getCodigo());
        if (tarifa != null) {
            controlador.agregarTarifa(tour.getCodigo(), tarifa);
            verTours();
            mostrarExito("Tarifa agregada.");
        }
    }

    // CU04
    private void registrarCliente() throws ReglaNegocioException {
        Cliente cliente = pedirCliente("");
        if (cliente != null) {
            controlador.registrarCliente(cliente);
            verClientes();
            mostrarExito("Cliente registrado: " + cliente);
        }
    }

    // tambien lo usa la pestania de reservas cuando el titular no esta registrado
    public Cliente pedirCliente(String documento) {
        Formulario f = new Formulario(ventana, "Registrar cliente");
        f.agregarTexto("Documento", documento);
        f.agregarTexto("Nombre");
        f.agregarTexto("Apellido");
        f.agregarTexto("Telefono");
        f.agregarTexto("Correo");
        f.agregarTexto("Pais de residencia");
        if (!f.mostrar()) {
            return null;
        }
        return new Cliente(f.textoObligatorio("Nombre"), f.textoObligatorio("Apellido"), f.textoObligatorio("Documento"),
                f.texto("Telefono"), f.texto("Correo"), f.texto("Pais de residencia"));
    }

    private void altaGuia() throws ReglaNegocioException {
        Formulario f = new Formulario(ventana, "Alta de guia");
        f.agregarTexto("Legajo");
        f.agregarTexto("Nombre");
        f.agregarTexto("Apellido");
        f.agregarTexto("Documento");
        f.agregarTexto("Telefono");
        f.agregarTexto("Honorario por dia");
        f.agregarCasilla("Habilitado para aventura");
        f.agregarCasilla("Habilitado para cultural");
        f.agregarCasilla("Habilitado para naturaleza");
        f.agregarTexto("Idiomas (separados por coma)");
        if (!f.mostrar()) {
            return;
        }
        Guia guia = new Guia(f.entero("Legajo"), f.textoObligatorio("Nombre"), f.textoObligatorio("Apellido"),
                f.textoObligatorio("Documento"), f.texto("Telefono"), f.decimal("Honorario por dia"));
        if (f.marcado("Habilitado para aventura")) {
            guia.habilitarCategoria(Categoria.AVENTURA);
        }
        if (f.marcado("Habilitado para cultural")) {
            guia.habilitarCategoria(Categoria.CULTURAL);
        }
        if (f.marcado("Habilitado para naturaleza")) {
            guia.habilitarCategoria(Categoria.NATURALEZA);
        }
        for (String idioma : f.texto("Idiomas (separados por coma)").split(",")) {
            if (!idioma.trim().isEmpty()) {
                guia.agregarIdioma(idioma.trim());
            }
        }
        controlador.registrarEmpleado(guia);
        verPersonal();
        mostrarExito("Guia registrado: " + guia.getNombreCompleto());
    }

    private void altaVendedor() throws ReglaNegocioException {
        Formulario f = new Formulario(ventana, "Alta de vendedor");
        f.agregarTexto("Legajo");
        f.agregarTexto("Nombre");
        f.agregarTexto("Apellido");
        f.agregarTexto("Documento");
        f.agregarTexto("Telefono");
        f.agregarTexto("Comision en % (ej. 3)");
        if (!f.mostrar()) {
            return;
        }
        Vendedor vendedor = new Vendedor(f.entero("Legajo"), f.textoObligatorio("Nombre"), f.textoObligatorio("Apellido"),
                f.textoObligatorio("Documento"), f.texto("Telefono"), f.decimal("Comision en % (ej. 3)") / 100);
        controlador.registrarEmpleado(vendedor);
        verPersonal();
        mostrarExito("Vendedor registrado: " + vendedor.getNombreCompleto());
    }

    private void altaVehiculo() throws ReglaNegocioException {
        Formulario f = new Formulario(ventana, "Alta de vehiculo");
        f.agregarTexto("Patente");
        f.agregarTexto("Capacidad");
        f.agregarOpciones("Tipo", new String[]{"Combi", "Minibus", "Colectivo"});
        if (!f.mostrar()) {
            return;
        }
        TipoVehiculo tipo = TipoVehiculo.COMBI;
        if (f.opcion("Tipo") == 1) {
            tipo = TipoVehiculo.MINIBUS;
        } else if (f.opcion("Tipo") == 2) {
            tipo = TipoVehiculo.COLECTIVO;
        }
        Vehiculo vehiculo = new Vehiculo(f.textoObligatorio("Patente"), f.entero("Capacidad"), tipo);
        controlador.registrarVehiculo(vehiculo);
        verVehiculos();
        mostrarExito("Vehiculo registrado: " + vehiculo);
    }

    private void altaProveedor() throws ReglaNegocioException {
        Formulario f = new Formulario(ventana, "Alta de proveedor");
        f.agregarTexto("Nombre");
        f.agregarTexto("CUIT");
        f.agregarTexto("Contacto");
        f.agregarOpciones("Servicio", TIPOS_SERVICIO);
        if (!f.mostrar()) {
            return;
        }
        Proveedor proveedor = new Proveedor(f.textoObligatorio("Nombre"), f.textoObligatorio("CUIT"),
                f.texto("Contacto"), tipoServicio(f.opcion("Servicio")));
        controlador.registrarProveedor(proveedor);
        verProveedores();
        mostrarExito("Proveedor registrado: " + proveedor);
    }

    private ServicioAdicional leerServicio() {
        Formulario f = new Formulario(ventana, "Servicio adicional");
        f.agregarTexto("Codigo");
        f.agregarTexto("Nombre");
        f.agregarTexto("Precio por pasajero");
        f.agregarOpciones("Tipo", TIPOS_SERVICIO);
        f.agregarCasilla("Obligatorio");
        if (!f.mostrar()) {
            return null;
        }
        return new ServicioAdicional(f.textoObligatorio("Codigo").toUpperCase(), f.textoObligatorio("Nombre"),
                f.decimal("Precio por pasajero"), tipoServicio(f.opcion("Tipo")), f.marcado("Obligatorio"));
    }

    private Tarifa leerTarifa(String titulo) {
        Formulario f = new Formulario(ventana, titulo);
        f.agregarOpciones("Temporada", new String[]{"Baja", "Media", "Alta"});
        f.agregarTexto("Vigente desde (aaaa-mm-dd)");
        f.agregarTexto("Vigente hasta (aaaa-mm-dd)");
        f.agregarTexto("Precio base por pasajero");
        if (!f.mostrar()) {
            return null;
        }
        Temporada temporada = Temporada.BAJA;
        if (f.opcion("Temporada") == 1) {
            temporada = Temporada.MEDIA;
        } else if (f.opcion("Temporada") == 2) {
            temporada = Temporada.ALTA;
        }
        return new Tarifa(temporada, f.fecha("Vigente desde (aaaa-mm-dd)"), f.fecha("Vigente hasta (aaaa-mm-dd)"),
                f.decimal("Precio base por pasajero"));
    }

    private TipoServicio tipoServicio(int opcion) {
        switch (opcion) {
            case 0:
                return TipoServicio.EXCURSION;
            case 1:
                return TipoServicio.TRASLADO;
            case 2:
                return TipoServicio.SEGURO;
            case 3:
                return TipoServicio.ALOJAMIENTO;
            default:
                return TipoServicio.COMIDA;
        }
    }

    private boolean tieneSeguro(Tour tour) {
        for (ServicioAdicional s : tour.getServicios()) {
            if (s.getTipo() == TipoServicio.SEGURO) {
                return true;
            }
        }
        return false;
    }
}
