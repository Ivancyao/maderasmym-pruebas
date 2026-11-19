# language: es
#Autor: E1 / E2 - Equipo Automatizacion Maderas MyM
#Requerimiento: RQ-01 Gestion de cuentas y control de acceso (seguridad)
#Funcionalidad: Registro de nuevo usuario (Crear cuenta)

Característica: Registro de nuevo usuario (Crear cuenta)

  Como un usuario visitante del sitio Maderas MyM
  Quiero poder crear una cuenta con mis datos
  Para acceder a las funcionalidades disponibles para clientes registrados

  Antecedentes: Acceso al formulario de registro
    Dado que un usuario visitante accede a la página "Crear cuenta"

  @CP1 @CP2 @CP3 @CP4 @RQ-01 @registro
  Esquema del escenario: Registro de usuario - <titulo>
    Cuando ingresa usuario "<usuario>", correo "<correo>", contraseña "<password>" y repite la contraseña "<confirmPassword>"
    Y presiona "Crear cuenta"
    Entonces el sistema muestra un mensaje que contiene "<mensajeEsperado>"

    Ejemplos:
      | titulo                                                     | usuario    | correo                     | password      | confirmPassword | mensajeEsperado        |
      | Registro exitoso con datos válidos                         | jmorales89 | jmorales89@example.com     | Maderas2026$  | Maderas2026$     | verificación            |
      | Registro fallido por contraseña que no cumple el formato   | jmorales89 | jmorales89@example.com     | maderas        | maderas          | mínimo 8 caracteres    |
      | Registro fallido por contraseñas no coincidentes           | jmorales89 | jmorales89@example.com     | Maderas2026$  | Maderas2025$     | no coincide            |
      | Registro fallido por correo ya registrado                  | jmorales89 | contacto@maderasmym.cl     | Maderas2026$  | Maderas2026$     | ya está registrado     |
