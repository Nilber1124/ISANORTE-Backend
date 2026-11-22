# Pagos online, envío y avisos — cómo pasar de la demostración a lo real

Estado actual (rama `feat/tienda-pago-simulado`): todo el flujo funciona de punta a punta con datos reales de la
base, pero el **cobro es simulado** y los **avisos solo llegan a la campana del panel de administración**.

## Qué hay hoy
| Pieza | Dónde | Estado |
|---|---|---|
| Envío (delivery / recojo en tienda) | `TiendaConfig`, `PedidoService`, V11 | Real. Tarifa en `COSTO_DELIVERY` (S/ 10 por defecto); `RECOJO_HABILITADO=false` lo oculta. |
| Cobro online | `PasarelaPago` → `PasarelaSimulada` | **Simulado**: no mueve dinero. El checkout muestra un banner "Modo de prueba". |
| Estado `PAGADO_ONLINE` + referencia | `PagoPedidoService`, `POST /api/publico/cuenta/pedidos/{codigo}/pago` | Real (el cambio de estado, seguimiento y aviso se hacen igual con una pasarela real). |
| Avisos al administrador | `Notificador` → `NotificadorBandeja`, `GET /api/notificaciones` | Real, pero solo en el panel. |
| Comprobante | Botón "Imprimir comprobante" (checkout y detalle de pedido) | Documento informativo, **no es boleta ni factura electrónica**. |

## Activar el cobro real (cuando el cliente tenga su cuenta de comercio)
1. El cliente abre su cuenta en la pasarela (p. ej. Culqi) y entrega las llaves de **pruebas** primero.
2. Completar `service/pago/PasarelaCulqi.java`: llamar a la API de cargos con la llave secreta (variable de
   entorno, nunca en el código) y el `token` que genera el formulario de la pasarela.
3. En el checkout, sustituir los botones de demostración por el formulario/checkout de la pasarela (que genera el
   token en el navegador; los datos de la tarjeta nunca pasan por nuestro servidor).
4. Configurar `PAGOS_PROVEEDOR=culqi` (en Render). Para Yape/Plin automáticos, usar los métodos que la pasarela ofrezca.
5. Añadir el webhook de la pasarela para confirmar pagos que se completen fuera del navegador.

## Activar avisos reales (correo, Telegram, WhatsApp)
Crear otra implementación de `Notificador` (p. ej. `NotificadorCorreo` con `spring-boot-starter-mail`) y registrar
varias a la vez (una lista de `Notificador`). Credenciales SMTP/token del bot → variables de entorno. Quien avisa
(`PedidoService`, `PagoPedidoService`) no cambia.

## Pendiente fuera de este alcance
Boleta/factura electrónica (SUNAT, proveedor como Nubefact), expiración automática de pedidos sin pagar,
cupones, reportes y devoluciones.
