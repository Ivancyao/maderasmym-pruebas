# language: es
#Autor: E1 / E2 - Equipo Automatizacion Maderas MyM
#Requerimiento: RQ-03 Navegabilidad e interaccion
#Funcionalidad: Pagina "Sobre Nosotros"

Característica: Página "Sobre Nosotros"

  Como un visitante del sitio
  Quiero conocer la historia, misión, visión, equipo y ubicación de Maderas MyM
  Para generar confianza antes de realizar una compra

  Antecedentes: Acceso a la página "Sobre Nosotros"
    Dado que un usuario accede al menú "Sobre Nosotros"

  @CP53 @RQ-03 @sobre-nosotros
  Escenario: Visualización exitosa de la información institucional
    Cuando la página carga completamente
    Entonces se despliega correctamente la historia, misión, visión y equipo de Maderas MyM

  @CP54 @RQ-03 @sobre-nosotros
  Escenario: Visualización del mapa de ubicación
    Cuando revisa la sección de ubicación
    Entonces el mapa de Google Maps embebido se carga correctamente

  @CP55 @RQ-03 @sobre-nosotros
  Escenario: Enlace a WhatsApp desde "Sobre Nosotros"
    Cuando presiona "Contactar por WhatsApp"
    Entonces el sistema lo redirige correctamente a WhatsApp

  @CP56 @RQ-03 @sobre-nosotros
  Escenario: Visualización correcta en dispositivo móvil
    Dado que un usuario accede a "Sobre Nosotros" desde un dispositivo móvil
    Cuando la página se despliega
    Entonces el contenido se adapta correctamente al tamaño de pantalla, sin elementos superpuestos
