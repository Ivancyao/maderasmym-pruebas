# language: es
#Autor: E1 / E2 - Equipo Automatizacion Maderas MyM
#Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
#Funcionalidad: Busqueda de productos (buscador principal)

Característica: Búsqueda de productos (buscador principal)

  Como un visitante o cliente del sitio
  Quiero buscar productos por nombre
  Para encontrar rápidamente lo que necesito comprar

  Antecedentes: Acceso a la página de inicio
    Dado que un usuario se encuentra en la página de inicio

  @CP17 @CP18 @CP20 @RQ-02 @busqueda
  Esquema del escenario: Búsqueda de productos - <titulo>
    Cuando escribe "<texto>" en el buscador
    Y ejecuta la búsqueda
    Entonces el sistema responde de forma "<comportamientoEsperado>"

    Ejemplos:
      | titulo                                              | texto                           | comportamientoEsperado        |
      | Búsqueda exitosa de un producto existente           | Pino Oregón                     | listado de resultados         |
      | Búsqueda sin resultados                              | asdfgh123                       | sin resultados                |
      | Búsqueda con caracteres especiales o código          | <script>alert(1)</script>       | sanitizada sin ejecutar codigo|

  @CP19 @RQ-02 @busqueda
  Escenario: Búsqueda con campo vacío
    Cuando presiona buscar sin ingresar texto
    Entonces el sistema no ejecuta la búsqueda o solicita ingresar un término
