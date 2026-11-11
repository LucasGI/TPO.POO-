# Plan de pruebas - Rumbo Sur

## 1. Pruebas funcionales

Las pruebas se hicieron sobre la interfaz Swing, desde cero (sin `data/`, con los datos de prueba), el 10/11/2026. Los mensajes de éxito y de error aparecen en ventanas emergentes ("Operacion realizada" / "No se pudo realizar"); los listados, en el área de texto de cada pestaña. Las fechas de las salidas dependen del día de ejecución, por eso se indican como "en N días".

Resultado: **26 de 26 casos OK**.

| ID | Caso de uso | Datos de entrada | Resultado esperado | Resultado obtenido | Estado |
| --- | --- | --- | --- | --- | --- |
| PF01 | CU17 Controlar vencimientos | Primera ejecución. Salida 4 (trekking en 6 días) con 1 pasajero y cupo mínimo 4 | Al iniciar, la salida 4 se cancela y su reserva se anula con devolución del 100 % | "Aviso: Salida 4 cancelada por no alcanzar el cupo minimo." Reserva 5 ANULADA, cancela agencia | OK |
| PF02 | CU05 Crear reserva | Salida 5, cliente 30111222, vendedor 101, 2 adultos, el primero con almuerzo (ALM) | Reserva PENDIENTE, total 2 x 45000 + 15000 = 105000, seña 31500, salida PROGRAMADA (2 < 5) | "Reserva 6 creada en estado PENDIENTE", "Total $105000.0 ... sena minima $31500.0", salida PROGRAMADA | OK |
| PF03 | CU05 Crear reserva (cupo) | Salida 2 (6 de 15 lugares ocupados), pedido de 10 pasajeros | Se rechaza antes de cargar los pasajeros | "No hay lugares suficientes en la salida 2." | OK |
| PF04 | CU05 Crear reserva (edad mínima) | Salida 1 (aventura, edad mínima 14), pasajero nacido el 2018-01-01 | Se rechaza la reserva y no se ocupa cupo | "Nene Lopez tendra 8 de edad y el tour exige un minimo de 14." La salida 1 sigue con 4/10 | OK |
| PF05 | CU04 Registrar cliente (desde CU05) | Documento 99887766 no registrado | Avisa que no está registrado, abre el formulario "Registrar cliente" y continúa con la reserva | "El cliente 99887766 no esta registrado. A continuacion se cargan sus datos." Se registró y la reserva 7 quedó creada | OK |
| PF06 | CU06 Precio (promoción grupo) | Salida 5, cliente 99887766, 6 adultos | 6 x 45000 = 270000 con 10 % = 243000 | "Total $243000.0 (Grupo 10%, adicionales $0.0)" | OK |
| PF07 | CU06 Precio (reserva anticipada) | Salida 3 (en 75 días, temporada alta 380000), 1 adulto | 380000 con 15 % = 323000 | "Total $323000.0 (Reserva anticipada 15%, ...)" | OK |
| PF08 | CU06 Precio (tipos de pasajero) | Reserva 3 de los datos de prueba: 2 adultos, 1 menor, 1 infante, 2 jubilados, tarifa 45000 | 45000 + 45000 + 27000 + 0 + 40500 + 40500 = 198000, con 10 % por grupo = 178200 | "total $178200.0", tipos ADULTO, MENOR, INFANTE y "(jubilado)" en el detalle | OK |
| PF09 | CU08 Registrar pago (seña) | Reserva 6, 31500 por transferencia | La reserva pasa a CONFIRMADA, saldo 73500 | "Estado de la reserva: CONFIRMADA - saldo $73500.0" | OK |
| PF10 | CU08 Registrar pago (cuotas) | Reserva 7, 10000 con tarjeta en 3 cuotas | Recargo 7 % = 700, total cobrado 10700, sigue PENDIENTE (no llega al 30 %) | "TARJETA $10000.0 + recargo $700.0 (3 cuotas)", "Total cobrado en este pago: $10700.0", PENDIENTE | OK |
| PF11 | CU08 Registrar pago (importe inválido) | Reserva 6, importe 999999 | Se rechaza el pago | "El importe supera el saldo pendiente de $73500.0." | OK |
| PF12 | CU09 Modificar reserva (reemplazo) | Reserva 6, sale 29333444 (adulto), entra un jubilado | Total baja a 100500 y, como hay pagos, se registra una nota de ajuste por -4500 | "Total $100500.0", "Se registro una nota de ajuste por la diferencia de precio." | OK |
| PF13 | CU10 Cancelar reserva (cliente) | Reserva 7 (pagado 10000), faltan 40 días | Devolución del 90 % = 9000 por tarjeta, se liberan los lugares | "Reserva 7 anulada. Importe a devolver: $9000.0 por TARJETA". Salida 5 vuelve a 2/20 | OK |
| PF14 | CU10 Cancelar reserva (estado) | Reserva 7 otra vez | Se rechaza porque ya está anulada | "La reserva 7 esta ANULADA y no se puede cancelar." | OK |
| PF15 | CU10 Cancelar reserva (inexistente) | Reserva 99 | Se informa que no existe | "No existe la reserva 99." | OK |
| PF16 | CU03 Programar salida | Tour CU01 (cupo 5 a 20), cupo 50 | Se rechaza el cupo | "El cupo debe estar entre 5 y 20." | OK |
| PF17 | CU11 Asignar guía | Salida 1 (aventura), guía 202 (solo cultural) | Se rechaza la asignación | "Diego Fernandez no esta habilitado para tours de AVENTURA." | OK |
| PF18 | CU11 Asignar vehículo | Salida 3 (cupo 20), vehículo AB123CD (15 lugares) | Se rechaza por capacidad | "El vehiculo tiene 15 lugares y la salida tiene cupo 20." | OK |
| PF19 | CU13 Iniciar salida (sin lista) | Salida 2 sin lista emitida | No cambia de estado | "Primero hay que emitir la lista de pasajeros." | OK |
| PF20 | CU12 Emitir lista de pasajeros | Salida 2 | Lista de 6 pasajeros ordenada por apellido, con emergencia y restricciones, guardada en archivo | Lista de Alvarez Federico a Rios Valentina, "Archivo generado: data/listas/lista-salida-2.txt" | OK |
| PF21 | CU13 Iniciar salida | Salida 2 con guía y lista | Pasa a EN_CURSO | "[OK] Salida 2 en curso." | OK |
| PF22 | CU14 y CU15 Cerrar y liquidar | Salida 2 (1 día, guía con 38000 por día, sin adicionales) | FINALIZADA, liquidación 38000 + 0 | "Liquidacion: Diego Fernandez: 1 dias $38000.0 + 5% adicionales $0.0 = $38000.0" | OK |
| PF23 | CU16 Emitir reportes | Los 5 botones de la pestaña Reportes | Se muestran y se guardan en `data/reportes/` | Ocupación ordenada por fecha, ventas e ingresos de mayor a menor, cancelaciones (2) y liquidaciones (1). Se generaron los 5 archivos | OK |
| PF24 | CU01 Administrar catálogo | Alta de tour de aventura KY01 sin cargar seguro | El sistema avisa y pide el precio del seguro, que queda obligatorio | Se abrió el formulario "Seguro obligatorio" ("Los tours de aventura llevan seguro de viaje."). El tour quedó con "SEG - Seguro de viaje $5000.0 (obligatorio)" | OK |
| PF25 | Persistencia | Cerrar la ventana y volver a abrir el programa | Se recuperan todos los datos | El tour KY01, las 8 reservas con sus estados y la salida 2 FINALIZADA siguen cargados | OK |
| PF26 | Validación de datos | "dos" en Cantidad de pasajeros; fecha "10/05/1990" | Mensaje de dato inválido, sin cortar el programa | "Dato invalido: El campo Cantidad de pasajeros tiene que ser un numero entero." y "... tiene que tener el formato aaaa-mm-dd." | OK |

## 2. Pruebas unitarias

Se ejecutan con JUnit 4 (ver el README). Resultado: **57 pruebas, 0 fallas**.

| Clase de prueba | Qué verifica | Cantidad |
| --- | --- | --- |
| `modelo.PasajeroTest` | Edad según el cumpleaños, tipo de pasajero según la edad a la fecha de salida, orden por apellido, documento obligatorio | 4 |
| `modelo.TourTest` | Edad mínima en aventura, seguro obligatorio, tour sin seguro, tarifas superpuestas, tarifa vigente, cupo fuera de rango, itinerario ordenado, fecha de fin | 10 |
| `modelo.SalidaTest` | Confirmación al cupo mínimo, completa al máximo, cupo insuficiente, pasajero inválido sin dejar datos a medias, lugares liberados, guía habilitado y superposición, capacidad del vehículo, condiciones para iniciar, lista solo con confirmadas, liquidación con 5 % de adicionales, doble liquidación | 12 |
| `modelo.ReservaTest` | Seña del 30 %, recargo del 7 %, pago mayor al saldo, cuotas solo con tarjeta, saldo completo a menos de 10 días, escala de devolución (30/29/15/14/7/6 días), devolución de la agencia, cancelación sin pagos, doble cancelación, reemplazo hasta 3 días antes, reemplazo con adicionales, nota de ajuste | 12 |
| `servicio.CalculadoraPrecioTest` | Factor por tipo, descuento de jubilado, promoción anticipada (61 y 60 días), grupo (6 y 5 pasajeros), promociones no acumulables, adicionales sin descuento, sin promociones | 7 |
| `servicio.PoliticaCancelacionTest` | Límites de la escala del cliente y devolución total de la agencia | 2 |
| `controlador.ControladoresTest` | Flujo de los dos diagramas de secuencia (crear y cancelar reserva), servicio inexistente sin dejar la reserva cargada, cupo, legajo que no es vendedor, reserva inexistente, control de cupo mínimo a 7 días, saldo impago a 10 días, seña no pagada en 48 horas, salida en el pasado | 10 |
