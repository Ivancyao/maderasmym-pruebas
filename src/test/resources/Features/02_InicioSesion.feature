# language: es
#Autor: E1 / E2 - Equipo Automatizacion Maderas MyM
#Requerimiento: RQ-01 Gestion de cuentas y control de acceso (seguridad)
#Funcionalidad: Inicio de sesion (Login)

Característica: Inicio de sesión (Login)

  Como un cliente registrado de Maderas MyM
  Quiero iniciar sesión con mis credenciales
  Para acceder a la tienda con mi cuenta identificada

  Antecedentes: Acceso al formulario de inicio de sesión
    Dado que un usuario accede a "Iniciar sesión"

  @CP5 @CP6 @CP7 @RQ-01 @login
  Esquema del escenario: Inicio de sesión - <titulo>
    Cuando ingresa usuario "<usuario>" y contraseña "<password>"
    Y presiona "Iniciar sesión"
    Entonces el sistema muestra un mensaje que contiene "<mensajeEsperado>"

    Ejemplos:
      | titulo                                                | usuario    | password      | mensajeEsperado       |
      | Inicio de sesión exitoso                              | jmorales89 | Maderas2026$  | Bienvenido            |
      | Inicio de sesión fallido por contraseña incorrecta    | jmorales89 | ClaveMala123  | contraseña incorrecta |
      | Inicio de sesión fallido por usuario inexistente      | usuarioXYZ | Maderas2026$  | no existe             |

  @CP8 @RQ-01 @login
  Escenario: Reenvío de verificación de cuenta
    Dado que un usuario se registró pero no ha verificado su cuenta
    Cuando intenta iniciar sesión y presiona "Reenviar verificación"
    Entonces el sistema envía un nuevo correo de verificación y muestra un mensaje que contiene "correo de verificación"
