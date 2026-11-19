# language: es
#Autor: E1 / E2 - Equipo Automatizacion Maderas MyM
#Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
#Funcionalidad: Carrito de compras - gestion (editar/eliminar/vaciar)

Característica: Carrito de compras – gestión (editar/eliminar/vaciar)

  Como un cliente del sitio
  Quiero editar cantidades, eliminar productos o vaciar el carrito
  Para ajustar mi pedido antes de generarlo

  Antecedentes: Carrito con productos agregados
    Dado que el carrito contiene uno o más productos

  @CP33 @RQ-02 @carrito
  Escenario: Vaciar carrito exitosamente
    Cuando el usuario presiona "Vaciar Carrito"
    Entonces el sistema elimina todos los productos y el total vuelve a $0

  @CP34 @RQ-02 @carrito
  Escenario: Editar cantidad de un producto en el carrito
    Cuando el usuario modifica la cantidad de dicho producto a "5"
    Entonces el sistema actualiza la cantidad y recalcula el total

  @CP35 @RQ-02 @carrito
  Escenario: Eliminar un producto específico del carrito
    Cuando el usuario elimina uno de los productos del carrito
    Entonces el sistema retira solo ese producto y conserva el resto en el carrito

  @CP36 @RQ-02 @carrito
  Escenario: Recalcular el total al modificar cantidades
    Cuando el usuario modifica la cantidad de un producto en el carrito a "3" y confirma el cambio
    Entonces el sistema recalcula automáticamente el "Total" mostrado
