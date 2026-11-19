# language: es
#Autor: E1 / E2 - Equipo Automatizacion Maderas MyM
#Requerimiento: RQ-03 Navegabilidad e interaccion
#Funcionalidad: Menu de navegacion responsivo (movil)

Característica: Menú de navegación responsivo (móvil)

  Como un usuario que navega desde un dispositivo móvil
  Quiero un menú de navegación desplegable
  Para acceder a todas las secciones del sitio en pantallas pequeñas

  Antecedentes: Acceso al sitio desde un dispositivo móvil
    Dado que un usuario navega desde un dispositivo móvil con viewport 375x667

  @CP57 @RQ-03 @menu-movil
  Escenario: Apertura y cierre correcto del menú móvil
    Cuando presiona el ícono de menú hamburguesa
    Entonces el sistema despliega el menú de navegación y permite cerrarlo correctamente

  @CP58 @RQ-03 @menu-movil
  Escenario: Navegación a "Productos" desde el menú móvil
    Dado que el menú móvil está desplegado
    Cuando el usuario selecciona "Productos"
    Entonces el sistema muestra las categorías de productos disponibles

  @CP59 @RQ-03 @menu-movil
  Escenario: Visibilidad de carrito y login en el menú móvil
    Dado que el menú móvil está desplegado
    Cuando el usuario revisa las opciones disponibles
    Entonces se muestran correctamente los accesos a "Carrito" e "Iniciar sesión"

  @CP60 @RQ-03 @menu-movil
  Esquema del escenario: Accesibilidad de la opción <opcion> del menú
    Dado que el menú móvil está desplegado
    Cuando el usuario presiona la opción "<opcion>"
    Entonces la opción "<opcion>" responde correctamente

    Ejemplos:
      | opcion          |
      | Inicio          |
      | Productos       |
      | Sobre Nosotros  |
      | Carrito         |
      | Iniciar sesión  |
