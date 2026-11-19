# MaderasMyM_Pruebas — Evaluación 3 (API0101 Automatización de Pruebas)

Proyecto Maven (Eclipse) con Cucumber 6.10.0 + Selenium 3.141.59 + JUnit 4.13.1 + Java 8 que automatiza
los 60 escenarios (CP1–CP60) de las 15 funcionalidades del caso Maderas MyM (www.maderasmym.cl).

## Estructura
- `src/test/resources/Features/` : 15 `.feature` etiquetados con `@CPn` y `@RQ-0n`.
- `src/test/java/DefinicionPasos/<funcionalidad>/` : pasos Java + Selenium. Cada clase captura una **imagen por paso** (`@AfterStep`).
- `src/test/java/Ejecutador/` : un Runner JUnit por funcionalidad.
- `src/test/java/Utilidades/Utility.java` : capturas de pantalla y `resultado(...)`, que hace fallar el paso cuando una verificación da «NO PASA».
- `planilla/Planilla_Escenarios_Resultados_MaderasMyM.xlsx` : formato 3.1.5 con los 60 escenarios y la hoja **Resultados** (indicadores por fórmula).
- `herramientas/procesar_resultados.py` : vuelca los resultados de Cucumber en la planilla.

## 1. Preparar
1. Importar como *Existing Maven Project* en Eclipse (o usar Maven por consola).
2. Dejar el `chromedriver` que coincida con su Chrome en `src/test/resources/chrome/` (`chromedriver.exe` en Windows; en Mac/Linux ajustar `CHROMEDRIVER_PATH`).
3. Para los escenarios que necesitan datos reales (login válido, recuperación de contraseña, etc.) usar cuentas de prueba propias; no subir credenciales reales al informe.

## 2. Ejecutar
- Todo: `mvn clean verify` (ejecuta los 15 Runner y genera el reporte HTML consolidado).
- Una funcionalidad: botón derecho sobre un Runner > Run As > JUnit Test (o `mvn test -Dtest=Runner02InicioSesion`).

Salidas:
| Qué | Dónde |
|---|---|
| Captura por paso (nombre `paso<n>_OK/FALLA_...png`) | `evidencias/<escenario>/` |
| JSON de Cucumber (entrada del procesador) | `target/cucumber-json/*.json` |
| Reporte HTML por Runner | `target/cucumber-reports/*.html` |
| Reporte HTML consolidado (con gráficos) | `target/cucumber-report-html/cucumber-html-reports/overview-features.html` |
| XML JUnit | `target/cucumber-results/*.xml` |

## 2b. Ejecutar sin su computador (nube)
- **GitHub Actions:** subir el proyecto a un repositorio (con la carpeta `.github/workflows/`), pestaña *Actions* >
  «Ejecutar pruebas Maderas MyM» > *Run workflow*. Al terminar, descargar el artefacto `resultados-pruebas`.
- **Google Colab:** abrir `herramientas/ejecutar_pruebas_Colab.ipynb`, subir el zip del proyecto al panel de archivos y ejecutar las celdas;
  al final descarga `resultados_pruebas.zip`.
En ambos casos Chrome corre sin ventana (`HEADLESS=true`) y el chromedriver se indica con `CHROMEDRIVER_PATH`.

## 3. Pasar resultados a la planilla
```
pip install openpyxl
python herramientas/procesar_resultados.py
```
Completa en la planilla: resultado (PASA / NO PASA), estado (Finalizado / En construcción / Bloqueado), % avance,
pasos totales y exitosos, duración y observaciones (primer paso que falló y su error); además crea la hoja
«Detalle de pasos» con la ruta de cada captura. Los CP sin resultados quedan «Pendiente».
Después completen a mano las columnas amarillas: **HH estimadas** y **HH de construcción** (por escenario) y, en la hoja
Resultados, cuántos NO PASA son defecto del sitio y cuántos del script. Con eso se calculan las métricas de productividad.

## Notas
- Si un paso falla por un localizador, abrir la captura `..._FALLA_...png` del paso: permite distinguir si el defecto es del sitio o del script.
- `cucumber.properties` desactiva la publicación en el servicio público de Cucumber (evita exponer datos de prueba).
- La verificación de descargas (CP45–CP48) se hace por código HTTP 200 del enlace.
