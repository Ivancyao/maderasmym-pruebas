# language: es
#Autor: E1 / E2 - Equipo Automatizacion Maderas MyM
#Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
#Funcionalidad: Visualizacion de ficha de producto

Característica: Visualización de ficha de producto

  Como un cliente del sitio
  Quiero ver el detalle de un producto (tipo, medidas, dimensiones, disponibilidad e imágenes)
  Para decidir informadamente si lo agrego al carrito

  @CP25 @RQ-02 @ficha
  Escenario: Visualización exitosa del detalle de producto
    Dado que un usuario selecciona un producto desde el listado
    Cuando accede a su ficha
    Entonces el sistema muestra tipo, medidas, dimensiones y disponibilidad del producto

  @CP26 @RQ-02 @ficha
  Escenario: Visualización de producto sin stock
    Dado que un usuario accede a la ficha de un producto sin existencias
    Cuando revisa el campo "Disponibilidad"
    Entonces el sistema indica claramente que el producto no tiene stock

  @CP27 @RQ-02 @ficha
  Escenario: Acceso a la ficha desde un resultado de búsqueda
    Dado que un usuario realizó una búsqueda exitosa de "Pino Oregón"
    Cuando selecciona un producto del listado de resultados
    Entonces el sistema lo redirige correctamente a la ficha del producto seleccionado

  @CP28 @RQ-02 @ficha
  Escenario: Visualización de imágenes del producto
    Dado que un usuario está en la ficha de un producto
    Cuando revisa la sección de imágenes
    Entonces el sistema despliega correctamente las imágenes del producto sin errores de carga
