# language: es
#Autor: E1 / E2 - Equipo Automatizacion Maderas MyM
#Requerimiento: RQ-01 Gestion de cuentas y control de acceso (seguridad)
#Funcionalidad: Recuperacion de contrasena

Característica: Recuperación de contraseña

  Como un cliente registrado que olvidó su contraseña
  Quiero solicitar un enlace de recuperación
  Para poder restablecer el acceso a mi cuenta

  Antecedentes: Acceso al formulario de recuperación
    Dado que un usuario accede a "¿Olvidaste tu contraseña?"

  @CP9 @CP10 @CP11 @RQ-01 @recuperacion
  Esquema del escenario: Recuperación de contraseña - <titulo>
    Cuando ingresa el correo "<correo>" y confirma la solicitud
    Entonces el sistema muestra un mensaje que contiene "<mensajeEsperado>"

    Ejemplos:
      | titulo                                                 | correo                         | mensajeEsperado      |
      | Solicitud exitosa de recuperación con correo registrado| contacto@maderasmym.cl         | enlace               |
      | Solicitud fallida con correo no registrado             | noexiste@correo.cl             | no está registrado   |
      | Solicitud con campo de correo vacío                    |                                 | obligatorio          |

  @CP12 @RQ-01 @recuperacion
  Escenario: Restablecimiento con enlace expirado
    Dado que un usuario recibió un correo de recuperación
    Cuando utiliza el enlace después de su periodo de validez
    Entonces el sistema informa que el enlace expiró y ofrece solicitar uno nuevo
