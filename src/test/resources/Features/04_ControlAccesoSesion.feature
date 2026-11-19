# language: es
#Autor: E1 / E2 - Equipo Automatizacion Maderas MyM
#Requerimiento: RQ-01 Gestion de cuentas y control de acceso (seguridad)
#Funcionalidad: Control de acceso y seguridad de sesion

Característica: Control de acceso y seguridad de sesión

  Como responsable de la seguridad del sitio
  Quiero que las rutas de cuenta estén protegidas y que la sesión se gestione correctamente
  Para evitar accesos no autorizados a la información del cliente

  @CP13 @RQ-01 @seguridad
  Escenario: Bloqueo de acceso a datos de cuenta sin sesión iniciada
    Dado que un usuario no ha iniciado sesión
    Cuando intenta acceder directamente a la sección "Mi cuenta"
    Entonces el sistema lo redirige a la página de "Iniciar sesión"

  @CP14 @RQ-01 @seguridad
  Escenario: Cierre de sesión exitoso
    Dado que un usuario tiene una sesión iniciada
    Cuando selecciona la opción "Cerrar sesión"
    Entonces el sistema finaliza la sesión y lo redirige a la página de inicio como visitante

  @CP15 @RQ-01 @seguridad
  Escenario: Expiración de sesión por inactividad
    Dado que un usuario tiene una sesión iniciada
    Cuando permanece inactivo durante un periodo prolongado
    Entonces el sistema cierra la sesión automáticamente y solicita autenticarse nuevamente

  @CP16 @RQ-01 @seguridad
  Escenario: Intento de acceso directo a una ruta protegida
    Dado que un usuario no autenticado conoce la URL de una ruta protegida
    Cuando la ingresa directamente en el navegador
    Entonces el sistema deniega el acceso y lo redirige al login, sin exponer información de la cuenta
