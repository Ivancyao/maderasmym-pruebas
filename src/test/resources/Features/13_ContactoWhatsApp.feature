# language: es
#Autor: E1 / E2 - Equipo Automatizacion Maderas MyM
#Requerimiento: RQ-03 Navegabilidad e interaccion
#Funcionalidad: Contacto via WhatsApp

Característica: Contacto vía WhatsApp

  Como un visitante o cliente del sitio
  Quiero contactar a Maderas MyM directamente por WhatsApp
  Para consultar por productos sin necesidad de un checkout transaccional

  Antecedentes: Acceso al sitio
    Dado que un usuario está en cualquier página del sitio

  @CP49 @CP50 @RQ-03 @whatsapp
  Escenario: Redirección exitosa a WhatsApp con mensaje predefinido y número correcto
    Cuando presiona el botón "¡Contáctanos!"
    Entonces el sistema lo redirige a WhatsApp con el número "56976200646" y un mensaje predefinido

  @CP51 @RQ-03 @whatsapp
  Escenario: Apertura del enlace en una nueva pestaña
    Cuando presiona el botón "¡Contáctanos!"
    Entonces se abre en una nueva pestaña o aplicación, sin cerrar la sesión de compra del usuario

  @CP52 @RQ-03 @whatsapp
  Escenario: Funcionamiento del botón en dispositivo móvil
    Dado que un usuario navega desde un dispositivo móvil
    Cuando presiona el botón "¡Contáctanos!"
    Entonces el sistema abre correctamente la aplicación de WhatsApp instalada
