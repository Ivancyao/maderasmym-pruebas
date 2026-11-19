# language: es
#Autor: E1 / E2 - Equipo Automatizacion Maderas MyM
#Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
#Funcionalidad: Descarga de documentos (listas de precios / informativos)

Característica: Descarga de documentos (listas de precios / informativos)

  Como un cliente o visitante del sitio
  Quiero descargar los documentos PDF publicados (listas de precios e informativos)
  Para consultar la información sin depender de la navegación web

  Antecedentes: Acceso a la página de inicio
    Dado que un usuario está en la página de inicio

  @CP45 @CP46 @CP47 @RQ-02 @descargas
  Esquema del escenario: Descarga de documentos - <titulo>
    Cuando presiona el enlace "<nombreDocumento>"
    Entonces el sistema descarga o abre el PDF correctamente

    Ejemplos:
      | titulo                                               | nombreDocumento                   |
      | Descarga exitosa de "Lista de Precios 2026"           | Lista de Precios 2026 (PDF)       |
      | Descarga exitosa del "Informativo Maderas"            | Informativo Maderas (PDF)         |
      | Apertura del PDF sin errores de formato                | Pino Insigne Seco Cepillado Tabla |

  @CP48 @RQ-02 @descargas
  Escenario: Verificación de enlaces no rotos
    Dado que existen múltiples enlaces a documentos PDF en el sitio
    Cuando el usuario verifica cada uno de ellos
    Entonces ninguno retorna un error 404 o enlace roto
