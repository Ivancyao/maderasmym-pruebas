# language: es
#Autor: E1 / E2 - Equipo Automatizacion Maderas MyM
#Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
#Funcionalidad: Seleccion de metodo de entrega

Característica: Selección de método de entrega

  Como un cliente con productos en el carrito
  Quiero elegir cómo recibiré mi pedido (retiro en tienda o flete externo)
  Para completar correctamente mi solicitud antes de generar el pedido

  Antecedentes: Carrito con productos agregados
    Dado que el usuario está en el carrito de compras con productos agregados

  @CP37 @RQ-02 @entrega
  Escenario: Selección de "Retiro en tienda"
    Cuando selecciona la opción "Retiro en tienda (Sin costo)"
    Entonces el sistema registra el método de entrega sin agregar costo de flete

  @CP38 @RQ-02 @entrega
  Escenario: Selección de "Flete externo" con dirección indicada
    Cuando selecciona "Flete externo" e indica la dirección "Av. Siempre Viva 123, Santiago" en el campo "Comentarios"
    Entonces el sistema registra el método de entrega junto con la dirección proporcionada

  @CP39 @RQ-02 @entrega
  Escenario: Selección de "Flete externo" sin indicar dirección
    Cuando selecciona "Flete externo" y no indica ninguna dirección en "Comentarios" e intenta continuar
    Entonces el sistema solicita completar la dirección antes de generar el pedido

  @CP40 @RQ-02 @entrega
  Escenario: Cambio de método de entrega antes de confirmar
    Dado que el usuario ya seleccionó el método "Retiro en tienda (Sin costo)"
    Cuando lo cambia a "Flete externo" antes de generar el pedido
    Entonces el sistema actualiza correctamente el método seleccionado
