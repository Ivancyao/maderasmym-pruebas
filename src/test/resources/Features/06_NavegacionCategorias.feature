# language: es
#Autor: E1 / E2 - Equipo Automatizacion Maderas MyM
#Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
#Funcionalidad: Navegacion por categorias de productos

Característica: Navegación por categorías de productos

  Como un cliente del sitio
  Quiero navegar por categorías y subcategorías (medidas)
  Para encontrar productos agrupados según mi necesidad

  Antecedentes: Acceso al menú de categorías
    Dado que un usuario está en el menú "Categorías"

  @CP21 @RQ-02 @navegacion
  Escenario: Navegación exitosa a categoría "Pino Oregón Bruto"
    Cuando selecciona "Pino Oregón Bruto"
    Entonces el sistema despliega el listado de productos de esa categoría

  @CP22 @RQ-02 @navegacion
  Escenario: Navegación a subcategoría por medida
    Cuando despliega la categoría "Pino Oregón Cepillado" y selecciona la medida "4.00 mts"
    Entonces el sistema muestra solo los productos de esa medida específica

  @CP23 @RQ-02 @navegacion
  Escenario: Verificación de listado filtrado correctamente
    Cuando navega a la categoría "Pino Oregón Bruto" y revisa el listado de productos desplegado
    Entonces todos los productos mostrados pertenecen exclusivamente a la categoría seleccionada

  @CP24 @RQ-02 @navegacion
  Escenario: Navegación a categoría sin stock disponible
    Cuando accede a una categoría sin productos disponibles
    Entonces el sistema muestra un mensaje indicando que no hay productos disponibles
