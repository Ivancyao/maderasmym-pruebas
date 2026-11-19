# language: es
#Autor: E1 / E2 - Equipo Automatizacion Maderas MyM
#Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
#Funcionalidad: Carrito de compras - agregar producto

Característica: Carrito de compras – agregar producto

  Como un cliente del sitio
  Quiero agregar productos al carrito indicando una cantidad
  Para preparar mi pedido antes de generarlo

  Antecedentes: Acceso a un producto disponible
    Dado que un usuario está en la ficha o listado de un producto disponible

  @CP29 @CP30 @RQ-02 @carrito
  Esquema del escenario: Carrito - agregar producto - <titulo>
    Cuando indica la cantidad "<cantidad>" y presiona "Agregar"
    Entonces el sistema responde de forma "<comportamientoEsperado>"

    Ejemplos:
      | titulo                                             | cantidad | comportamientoEsperado        |
      | Agregar producto exitosamente al carrito           | 2        | agregado y contador actualizado|
      | Agregar producto con cantidad superior al stock     | 999999   | error de stock insuficiente    |

  @CP31 @RQ-02 @carrito
  Escenario: Actualización del contador del carrito
    Dado que el carrito se encuentra vacío
    Cuando el usuario agrega un producto con cantidad "1"
    Entonces el contador del carrito se actualiza a la cantidad de ítems agregados

  @CP32 @RQ-02 @carrito
  Escenario: Agregar el mismo producto más de una vez
    Dado que un producto ya se encuentra en el carrito
    Cuando el usuario lo agrega nuevamente con cantidad "1"
    Entonces el sistema acumula la cantidad en la misma línea de producto, sin duplicarlo
