# Rumbo Sur - Sistema de gestión para agencia de turismo

TPO de Programación Orientada a Objetos (UADE) - Propuesta B.

El sistema ordena la venta de tours de la agencia, la operación de cada salida y la liquidación a los guías.
Tiene interfaz gráfica hecha con Swing y guarda los datos en archivos para que no se pierdan entre ejecuciones.

## Integrantes

| Nombre | Legajo |
| --- | --- |
| Lucas Iacobellis | 1217175 |
| Facundo Fulop | 1216428 |

## Qué hace

- Catálogo de tours (aventura, cultural y naturaleza) con itinerario por día, tarifas por temporada y servicios adicionales.
- Programación de salidas con fecha y cupo, asignación de guía y vehículo, lista de pasajeros, inicio y cierre.
- Clientes y reservas con uno o más pasajeros: precio por tipo de pasajero, temporada y promociones; seña del 30 % y pagos del saldo.
- Modificación y cancelación de reservas con la política de devolución.
- Liquidación de honorarios del guía al cerrar la salida.
- Reportes: ocupación por salida, ventas por vendedor, ingresos por tour, cancelaciones y liquidaciones.
- Control de vencimientos al iniciar: cancela salidas sin cupo mínimo a 7 días, anula reservas con saldo impago a 10 días y reservas sin seña después de 48 horas.

## Requisitos

- JDK 17 o superior (el proyecto de IntelliJ está configurado con JDK 21).
- IntelliJ IDEA (recomendado) o una terminal con `javac` y `java`.
- Para las pruebas: JUnit 4.13.2 y Hamcrest 1.3, ya incluidos en la carpeta `lib/`.

## Instalación y ejecución

### Con IntelliJ IDEA

1. `File > Open` y elegir la carpeta del proyecto (la que tiene este README).
2. Si pide el JDK, elegir uno 17 o superior en `File > Project Structure > Project`.
3. Abrir `src/app/Main.java` y ejecutar con el botón verde. Se abre la ventana principal.

Para que compilen las pruebas, la carpeta `test` tiene que estar marcada como carpeta de pruebas y JUnit tiene que estar agregado. El archivo `RumboSur.iml` ya trae esa configuración. Si IntelliJ no la toma:

- Clic derecho sobre `test` > `Mark Directory as` > `Test Sources Root`.
- `File > Project Structure > Modules > Dependencies` > `+` > `JARs or Directories`, elegir los dos `.jar` de `lib/` y ponerles alcance `Test`.

La carpeta de trabajo tiene que ser la raíz del proyecto (es la opción por defecto de IntelliJ), porque los archivos de datos se buscan en `data/`.

### En Mac con doble clic

Abrir `ejecutar.command` desde Finder. Busca un JDK instalado, compila y abre la ventana.

### Desde la terminal (Mac o Linux)

```
javac -d out $(find src -name "*.java")
java -cp out app.Main
```

### Desde la terminal (Windows, PowerShell)

```
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out app.Main
```

## Datos de prueba

La primera vez que se ejecuta, si no existe `data/rumbosur.dat`, el programa carga datos de ejemplo. Las fechas se calculan a partir del día de ejecución, así que siempre sirven.

| Tipo | Datos |
| --- | --- |
| Tours | AV01 Trekking Cerro Catedral (aventura, edad minima 14, seguro obligatorio), CU01 Buenos Aires Historica (cultural), NA01 Glaciares y lagos (naturaleza) |
| Vendedores | 101 Martina Gomez, 102 Joaquin Ruiz |
| Guías | 201 Sofia Perez (aventura y naturaleza), 202 Diego Fernandez (cultural), 203 Lucia Torres (naturaleza y cultural) |
| Otros empleados | 301 Pablo Sosa (coordinador), 401 Laura Benitez (administradora) |
| Vehículos | AB123CD combi 15 lugares, AE456FG minibus 25, AF789HJ colectivo 45 |
| Clientes | 30111222 Ana Lopez, 28999888 Carlos Mendez, 35444555 Valentina Rios, 40111333 Tomas Herrera |
| Salida 1 | Trekking en 20 días, confirmada, con guía y vehículo, 2 reservas |
| Salida 2 | City tour en 5 días, confirmada, grupo de 6 con promoción, lista para emitir la lista de pasajeros, iniciar y cerrar |
| Salida 3 | Glaciares en 75 días, una reserva con promoción por anticipación |
| Salida 4 | Trekking en 6 días sin cupo mínimo: se cancela sola al iniciar el programa |
| Salida 5 | City tour en 40 días sin reservas, para probar altas |

Para volver a los datos de prueba, borrar la carpeta `data/` y ejecutar de nuevo.

## Uso

La ventana tiene cuatro pestañas: **Reservas y pagos**, **Salidas**, **Catálogo y personal** y **Reportes**. Cada pestaña tiene los botones de sus casos de uso a la izquierda y el resultado a la derecha. Los datos se cargan en formularios emergentes; los errores de reglas de negocio se muestran en un mensaje.

Las fechas se ingresan con el formato `aaaa-mm-dd` (por ejemplo 2026-12-20).

Para probar el ciclo completo de una salida con los datos de prueba: pestaña **Salidas** > "Emitir lista de pasajeros" (salida 2) > "Iniciar salida" (2) > "Finalizar y liquidar" (2).

## Archivos que genera

| Archivo | Contenido |
| --- | --- |
| `data/rumbosur.dat` | Todos los datos del sistema (serialización de objetos). Se guarda después de cada operación. |
| `data/listas/lista-salida-N.txt` | Lista de pasajeros de la salida N. |
| `data/reportes/*.txt` | Cada reporte que se consulta desde el menu. |

## Pruebas

### Pruebas unitarias

Están en `test/` (57 pruebas con JUnit 4) y cubren la lógica de negocio: precios y promociones, política de cancelación, cupo y estados de la salida, pagos y seña, asignaciones, liquidación y control de vencimientos.

- En IntelliJ: clic derecho sobre la carpeta `test` > `Run 'All Tests'`.
- Desde la terminal (Mac o Linux):

```
javac -d out $(find src -name "*.java")
javac -cp out:lib/junit-4.13.2.jar -d build/test $(find test -name "*.java")
java -cp out:build/test:lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar org.junit.runner.JUnitCore modelo.PasajeroTest modelo.TourTest modelo.SalidaTest modelo.ReservaTest servicio.CalculadoraPrecioTest servicio.PolíticaCancelacionTest controlador.ControladoresTest
```

En Windows se reemplazan los `:` del classpath por `;`.

### Pruebas funcionales

El plan con los casos, datos de entrada, resultado esperado y resultado obtenido está en [`docs/plan-de-pruebas.md`](docs/plan-de-pruebas.md).

## Estructura

```
src/
  app/           Main y carga de datos de prueba
  vista/         interfaz Swing (VentanaPrincipal, PanelBase, un panel por pestaña, Formulario)
  controlador/   ReservaController, SalidaController, CatálogoController, ReporteController
  servicio/      CalculadoraPrecio, promociones, políticas de cancelación y comparadores
  modelo/        clases del dominio y enumerados
  persistencia/  Repositorio, repositorios, BaseDatos, ArchivoDatos y ArchivoTexto
  excepcion/     ReglaNegocioException y sus subclases
test/            pruebas unitarias (JUnit 4)
lib/             JUnit y Hamcrest
docs/            plan de pruebas, informe final y diagramas UML (docs/uml)
```
