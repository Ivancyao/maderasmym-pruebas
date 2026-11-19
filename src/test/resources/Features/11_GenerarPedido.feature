# language: es
#Autor: E1 / E2 - Equipo Automatizacion Maderas MyM
#Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
#Funcionalidad: Generar pedido (checkout)

Característica: Generar pedido (checkout)

  Como un cliente con productos en el carrito y un método de entrega seleccionado
  Quiero generar mi pedido
  Para coordinar luego la compra por WhatsApp (el sitio no tiene pasarela de pago en línea)

  @CP41 @RQ-02 @pedido
  Escenario: Generar pedido exitosamente
    Dado que el carrito contiene productos y se seleccionó un método de entrega válido
    Cuando el usuario presiona "Generar Pedido"
    Entonces el sistema procesa el pedido y muestra una confirmación al usuario

  @CP42 @RQ-02 @pedido
  Escenario: Intento de generar pedido con el carrito vacío
    Dado que el carrito de compras está vacío
    Cuando el usuario presiona "Generar Pedido"
    Entonces el sistema impide la acción y muestra un mensaje indicando que el carrito está vacío

  @CP43 @RQ-02 @pedido
  Escenario: Generar pedido sin seleccionar método de entrega
    Dado que el carrito contiene productos pero no se ha seleccionado método de entrega
    Cuando el usuario presiona "Generar Pedido"
    Entonces el sistema solicita seleccionar un método de entrega antes de continuar

  @CP44 @RQ-02 @pedido
  Escenario: Confirmación tras generar el pedido
    Dado que un pedido fue generado exitosamente
    Cuando el sistema procesa la solicitud
    Entonces se muestra un mensaje de confirmación y el carrito queda vacío
