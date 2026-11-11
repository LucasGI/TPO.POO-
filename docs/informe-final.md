# Informe final - Rumbo Sur (Etapa 2)

TPO de Programación Orientada a Objetos - UADE - Propuesta B: Agencia de Turismo.

## 1. Cambios respecto del diseño original

El código respeta la estructura del diagrama de clases final de la Etapa 1: los mismos paquetes, las jerarquías de `Persona` y `Tour`, los cuatro controladores, `CalculadoraPrecio`, las interfaces `Promocion` y `PoliticaCancelacion` y la interfaz `Repositorio`. Los cambios que surgieron al programar se reflejan en los diagramas actualizados de `docs/uml` (`clases-final-modelo`, `clases-final-capas` y los dos de secuencia).

| # | Cambio | Motivo |
| --- | --- | --- |
| 1 | La persistencia pasó de un archivo CSV por entidad a **serialización de objetos** en un único archivo `data/rumbosur.dat`. `RepositorioArchivo` se reemplazó por `RepositorioLista` (guarda en una lista), `BaseDatos` (agrupa los repositorios) y `ArchivoDatos` (lee y escribe con `ObjectOutputStream` y `ObjectInputStream`). | Una reserva apunta a su salida, a su cliente y a su vendedor. Con un CSV por entidad había que guardar códigos y volver a armar esas relaciones al leer. Guardando todo junto en un solo archivo las relaciones se mantienen. Es la forma de guardar objetos que vimos en la clase de archivos. |
| 2 | Se agregó `ArchivoTexto`, que escribe con `BufferedWriter` la lista de pasajeros y los reportes en `.txt`. | La lista de pasajeros se le entrega al guía y los reportes se consultan fuera del programa, así que conviene que sean archivos legibles. |
| 3 | `buscarPorId` devuelve el objeto o `null`, en lugar de `Optional`. | Es la forma que usamos en clase (por ejemplo en el repositorio de usuarios). El controlador revisa el `null` y lanza `ReglaNegocioException`. |
| 4 | `Tour.crearSalida(id, fecha, cupo)` crea la salida y valida el cupo y la tarifa vigente. El controlador solo la guarda. | Patrón Creador: el tour tiene la información para armar una salida válida. |
| 5 | `Salida.reservar` recibe también el número de reserva y la fecha del día. Se agregaron `verificarLugarPara`, `estaVigente`, `seSuperponeCon`, `faltaCupoMinimo` y `cancelar` (cancelación de toda la salida). | El número lo conoce el repositorio, no la salida. Los demás métodos salieron al programar el control de vencimientos y la disponibilidad del guía (Experto). |
| 6 | La clase `DatosPasajero` trae también los códigos de adicionales elegidos. Reemplaza el parámetro `Map` que estaba en `crearReserva`. | Es más simple de cargar desde el menú y evita usar un mapa de listas. |
| 7 | `setTotal` se reemplazó por `Reserva.actualizarTotal(total, hoy, detalle)`. | Así la reserva decide sola si tiene que registrar una nota de ajuste (RN16) cuando cambia el precio y ya tiene pagos. |
| 8 | Se agregó `esCancelacionDeAgencia()` a `PoliticaCancelacion`. | La cancelación guarda quién canceló sin volver al parámetro `boolean` que el diseño quería eliminar. |
| 9 | La interfaz es gráfica con **Swing** en lugar de un único `MenuPrincipal`: `VentanaPrincipal` (un `JFrame` con `JTabbedPane`), una clase abstracta `PanelBase` y un panel por pestaña (reservas, salidas, catálogo y reportes), y un `Formulario` (`JDialog`) que se reutiliza para cargar datos. | El enunciado valora la interfaz gráfica. `PanelBase` implementa `ActionListener` y tiene el `try / catch / finally` común a todos los botones: muestra el error en un `JOptionPane` y guarda los datos en el `finally`. Al cerrar la ventana se guarda con un `WindowAdapter`. |
| 10 | Nuevo paquete `app` con `Main` y `DatosDePrueba`. | `Main` arma los objetos y los conecta (los controladores reciben los repositorios por constructor). `DatosDePrueba` carga ejemplos la primera vez, con fechas calculadas desde el día de ejecución. |
| 11 | Para los reportes se agregaron `LineaReporte`, `ComparadorPorValor` y `ComparadorCancelacionPorFecha`. | Ordenamiento personalizado con `Comparator`. El orden natural (`Comparable`) está en `Salida` (por fecha), `Pasajero` (por apellido) y `Actividad` (por día y orden). |
| 12 | `Actividad.horario` es `String` y `Guia.categorias` es `List`. Se agregaron los enumerados `TipoServicio` y `TipoVehiculo`. | `LocalTime` y `Set` no hacían falta para lo que se resuelve. Los enumerados evitan textos inválidos. |
| 13 | Las operaciones reciben la fecha del día (`hoy`) por parámetro. | Las pruebas pueden usar una fecha fija y verificar los plazos de 7, 10 y 3 días sin depender del reloj. |

## 2. Dificultades encontradas

- **Guardar objetos relacionados.** El diseño original tenía un archivo por repositorio. Al leerlos por separado, la salida de una reserva y la salida guardada en su propio archivo quedarían como dos objetos distintos. Lo resolvimos guardando todos los repositorios juntos en un único archivo.
- **No dejar datos a medias.** Si el tercer pasajero de una reserva no cumplía la edad mínima, la reserva quedaba agregada a la salida con dos pasajeros. Se cambió el orden: la salida valida a todos los pasajeros y recién después agrega la reserva. Lo mismo con los códigos de adicionales: el controlador los revisa antes de crear la reserva.
- **Cálculo de días entre fechas.** Para la política de cancelación y los vencimientos usamos `toEpochDay()` de `LocalDate` y restamos. Hubo que acordar si los días se cuentan corridos (supuesto S09) y probar los límites (30, 29, 15, 14, 7 y 6 días).
- **Estados de la salida.** Una salida completa tiene que volver a confirmada si alguien cancela, y una confirmada a programada si baja del mínimo. Se concentró todo en `actualizarEstado()`, que se llama después de reservar y de liberar lugares.
- **Vencimientos sin un programa corriendo siempre.** El sistema no está abierto todo el día, así que el control de cupo mínimo, saldo impago y seña se ejecuta al iniciar y también desde el menú (supuesto S11).
- **Redondeo de importes.** Con `double` pueden aparecer valores como 0.30000000000000004. Los totales, recargos y devoluciones se redondean a dos decimales.
- **Datos de prueba que no venzan.** Con fechas fijas, la demo dejaba de funcionar a los pocos días. Las fechas se calculan a partir del día en que se ejecuta.

## 3. Distribución de tareas

| Integrante | Tareas principales |
| --- | --- |
| Lucas Iacobellis (1217175) | Estructura del proyecto y README. Paquete `modelo`: jerarquía de personas, tours y sus subclases, actividades, tarifas, servicios, proveedores, vehículos, `Pasajero` y enumerados. Pruebas `PasajeroTest` y `TourTest`. Diagramas de casos de uso y de secuencia. |
| Facundo Fulop (1216428) | Excepciones. `Salida`, `Reserva`, pagos, cancelaciones, notas de ajuste y liquidación. Paquete `servicio`: precios, promociones, políticas de cancelación y comparadores. Pruebas `SalidaTest`, `ReservaTest`, `CalculadoraPrecioTest` y `PoliticaCancelacionTest`. Diagramas de clases. |

## 4. Reflexión del grupo

> Borrador para revisar entre todos y ajustar con la experiencia real del grupo.

Lo más útil fue haber hecho bien el diseño en la Etapa 1. Con el diagrama final y los dos de secuencia ya sabíamos qué clase hacía cada cosa, y la codificación fue bastante directa: los mensajes de "Crear reserva" y "Cancelar reserva" están en el código en el mismo orden que en el diagrama.

Los principios y patrones dejaron de ser teoría cuando tuvimos que cambiar cosas. Cambiar la persistencia de CSV a serialización no tocó ningún controlador, porque dependen de la interfaz `Repositorio` (DIP). Agregar una promoción o una política nueva es crear una clase, sin modificar `CalculadoraPrecio` ni `Reserva` (OCP). Tener la lógica en el modelo y no en el menú (Experto y Controlador) nos permitió probar casi todo con pruebas unitarias, sin abrir la ventana. Y cuando pasamos de la consola a Swing, el modelo y los controladores no cambiaron.

Lo que haríamos distinto: empezar antes las pruebas unitarias. Varios errores (la reserva que quedaba a medias, el estado de la salida al cancelar) los encontramos al escribir las pruebas y hubiera sido mejor detectarlos mientras programábamos. También dividiríamos el trabajo por casos de uso completos y no por capas, para que cada uno pueda explicar un flujo de punta a punta en la defensa.
