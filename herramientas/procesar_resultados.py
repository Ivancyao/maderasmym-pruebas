# -*- coding: utf-8 -*-
"""
Procesa los resultados de Cucumber (archivos JSON) y los vuelca en la planilla de escenarios
(formato 3.1.5 "Funcionalidad_Escenarios"): estado, % de avance, pasos, duracion y observaciones
por cada uno de los 60 escenarios (CP1..CP60), mas una hoja con el detalle de cada paso.

Uso (desde la carpeta raiz del proyecto, despues de ejecutar los Runner):

    python herramientas/procesar_resultados.py

Opciones:
    --json      carpeta con los .json de Cucumber        (por defecto target/cucumber-json)
    --features  carpeta con los .feature                 (por defecto src/test/resources/Features)
    --planilla  planilla de entrada                      (por defecto planilla/Planilla_Escenarios_Resultados_MaderasMyM.xlsx)
    --salida    planilla actualizada (se crea/sobrescribe) (por defecto la misma planilla de entrada)

Requiere Python 3 y la libreria openpyxl:   pip install openpyxl
Los CP que no aparecen en los resultados quedan como "Pendiente" (no ejecutado).
El script NO inventa datos: todo lo que escribe proviene de los archivos JSON de la ejecucion.
"""
import argparse
import glob
import json
import os
import re
import sys

import openpyxl
from openpyxl.styles import Alignment, Border, Font, PatternFill, Side

HOJA_ESC = "Escenarios de Prueba"
HOJA_DET = "Detalle de pasos"
FILA_INICIO = 5          # primera fila de escenarios (CP1)
TOTAL_CP = 60

# Palabras clave de paso en Gherkin espanol / ingles
PALABRAS_PASO = ("Dado", "Cuando", "Entonces", "Y", "Pero", "Given", "When", "Then", "And", "But", "*")


# --------------------------------------------------------------------------------------
# 1. Mapeo linea del .feature -> CP
# --------------------------------------------------------------------------------------
def parsear_feature(ruta):
    """Devuelve {numero_de_linea: [CPn, ...]} para escenarios y para cada fila de Ejemplos.

    - Escenario simple: la linea del escenario cubre todos los @CPn que lo etiquetan
      (ej. @CP49 @CP50 en un mismo escenario).
    - Esquema del escenario: si hay tantos @CPn como filas de Ejemplos, la fila i cubre el CP i;
      si hay un unico @CPn, todas las filas cubren ese CP.
    """
    with open(ruta, encoding="utf-8") as fh:
        lineas = fh.read().splitlines()

    mapa = {}
    tags = []
    esquema = None   # {"cps": [...], "filas": [lineas de datos]} mientras se lee un esquema
    en_ejemplos = False
    encabezado_visto = False

    def cerrar_esquema():
        nonlocal esquema
        if not esquema:
            return
        cps, filas = esquema["cps"], esquema["filas"]
        if len(cps) == len(filas):
            for cp, linea in zip(cps, filas):
                mapa[linea] = [cp]
        elif len(cps) == 1:
            for linea in filas:
                mapa[linea] = [cps[0]]
        else:
            for linea in filas:
                mapa[linea] = list(cps)
        esquema = None

    for n, texto in enumerate(lineas, start=1):
        t = texto.strip()
        if t.startswith("@"):
            tags += re.findall(r"@(CP\d+)\b", t)
            continue
        if re.match(r"^(Esquema del escenario|Esquema del Escenario|Scenario Outline)\s*:", t):
            cerrar_esquema()
            esquema = {"cps": tags, "filas": []}
            tags, en_ejemplos, encabezado_visto = [], False, False
        elif re.match(r"^(Escenario|Scenario)\s*:", t):
            cerrar_esquema()
            if tags:
                mapa[n] = list(tags)
            tags, en_ejemplos, encabezado_visto = [], False, False
        elif re.match(r"^(Ejemplos|Examples)\s*:", t):
            en_ejemplos, encabezado_visto = True, False
        elif t.startswith("|") and esquema is not None and en_ejemplos:
            if not encabezado_visto:
                encabezado_visto = True      # primera fila = encabezado de la tabla
            else:
                esquema["filas"].append(n)
        elif re.match(r"^(Antecedentes|Fondo|Background|Caracter[ií]stica|Feature)\s*:", t):
            cerrar_esquema()
            tags = []
    cerrar_esquema()
    return mapa


def mapear_features(carpeta_features):
    """{nombre_de_archivo.feature: {linea: [CPn]}}"""
    resultado = {}
    for ruta in sorted(glob.glob(os.path.join(carpeta_features, "*.feature"))):
        resultado[os.path.basename(ruta)] = parsear_feature(ruta)
    return resultado


# --------------------------------------------------------------------------------------
# 2. Lectura de los JSON de Cucumber
# --------------------------------------------------------------------------------------
def _salidas(objeto):
    """Textos que el codigo dejo con scenario.log() (ruta de la evidencia)."""
    textos = []
    for hook in objeto.get("after", []) + objeto.get("before", []):
        salida = hook.get("output", [])
        textos += [s for s in salida if isinstance(s, str)]
    textos += [s for s in objeto.get("output", []) if isinstance(s, str)]
    return textos


def _evidencia(paso):
    for texto in _salidas(paso):
        m = re.search(r"Evidencia:\s*(\S.*)", texto)
        if m:
            return m.group(1).strip()
    return ""


def leer_ejecuciones(carpeta_json, mapa_features):
    """Devuelve la lista de ejecuciones (una por escenario o fila de Ejemplos) ya asociadas a sus CP."""
    ejecuciones = []
    archivos = sorted(glob.glob(os.path.join(carpeta_json, "*.json")))
    if not archivos:
        sys.exit("No se encontraron archivos .json en: %s\n"
                 "Ejecute primero los Runner (o 'mvn clean verify')." % carpeta_json)
    for archivo in archivos:
        with open(archivo, encoding="utf-8") as fh:
            try:
                features = json.load(fh)
            except ValueError:
                print("AVISO: no se pudo leer", archivo)
                continue
        for feature in features:
            nombre_feature = os.path.basename(feature.get("uri", ""))
            mapa = mapa_features.get(nombre_feature, {})
            fondo = []
            for elemento in feature.get("elements", []):
                if elemento.get("type") == "background":
                    fondo = elemento.get("steps", [])
                    continue
                cps = mapa.get(elemento.get("line"), [])
                pasos = []
                for paso in fondo + elemento.get("steps", []):
                    res = paso.get("result", {})
                    pasos.append({
                        "palabra": paso.get("keyword", "").strip(),
                        "texto": paso.get("name", ""),
                        "estado": res.get("status", "undefined"),
                        "duracion": res.get("duration", 0) / 1e9,
                        "metodo": paso.get("match", {}).get("location", ""),
                        "error": (res.get("error_message") or "").strip(),
                        "evidencia": _evidencia(paso),
                    })
                fallo_preparacion = ""
                for hook in elemento.get("before", []):
                    r = hook.get("result", {})
                    if r.get("status") == "failed":
                        fallo_preparacion = (r.get("error_message") or "").strip()
                ejecuciones.append({
                    "feature": nombre_feature,
                    "nombre": elemento.get("name", ""),
                    "linea": elemento.get("line"),
                    "cps": cps,
                    "pasos": pasos,
                    "fallo_preparacion": fallo_preparacion,
                })
    return ejecuciones


# (texto que aparece en el error, categoria, origen probable a confirmar con la captura del paso)
CLASIFICACION = [
    ("NoSuchElementException", "Elemento no encontrado (localizador)", "Script (localizador referencial) o cambio en el sitio"),
    ("TimeoutException", "Tiempo de espera / sincronización", "Script (esperas) o lentitud del sitio"),
    ("ElementClickInterceptedException", "Elemento cubierto o no clicable", "Sitio (elemento superpuesto) o script"),
    ("ElementNotInteractableException", "Elemento no interactuable", "Sitio (elemento oculto) o script"),
    ("StaleElementReferenceException", "Elemento obsoleto (DOM recargado)", "Script (sincronización)"),
    ("UnhandledAlertException", "Alerta no controlada", "Sitio (ejecuta el código ingresado) o script"),
    ("SessionNotCreatedException", "Navegador/driver no iniciado", "Entorno (chromedriver y Chrome incompatibles)"),
    ("IllegalStateException", "Navegador/driver no iniciado", "Entorno (ruta o versión del chromedriver)"),
    ("WebDriverException", "Error del navegador", "Entorno"),
    ("NO PASA", "Resultado distinto del esperado", "Sitio o dato de prueba (la verificación no se cumplió)"),
    ("AssertionError", "Resultado distinto del esperado", "Sitio o dato de prueba (la verificación no se cumplió)"),
]


def clasificar_error(texto):
    for clave, categoria, origen in CLASIFICACION:
        if clave in (texto or ""):
            return categoria, origen
    return ("Otro", "Por analizar") if texto else ("", "")


def _primera_linea(texto, maximo=260):
    linea = (texto or "").strip().splitlines()
    linea = linea[0] if linea else ""
    return linea if len(linea) <= maximo else linea[:maximo - 1] + "…"


def consolidar_por_cp(ejecuciones):
    """Agrupa las ejecuciones por CP y calcula pasos, estado y observaciones."""
    por_cp = {}
    for ej in ejecuciones:
        for cp in ej["cps"]:
            por_cp.setdefault(cp, []).append(ej)

    resumen = {}
    for cp, lista in por_cp.items():
        pasos = [p for ej in lista for p in ej["pasos"]]
        total = len(pasos)
        ok = sum(1 for p in pasos if p["estado"] == "passed")
        duracion = sum(p["duracion"] for p in pasos)
        fallos = [(ej, p) for ej in lista for p in ej["pasos"] if p["estado"] == "failed"]
        sin_implementar = [p for p in pasos if p["estado"] in ("undefined", "pending", "ambiguous")]
        prep = next((ej["fallo_preparacion"] for ej in lista if ej["fallo_preparacion"]), "")

        categoria, origen, paso_fallido = "", "", ""
        if fallos:
            categoria, origen = clasificar_error(fallos[0][1]["error"])
            paso_fallido = "%s %s" % (fallos[0][1]["palabra"], fallos[0][1]["texto"])
        elif prep:
            categoria, origen = clasificar_error(prep)
        if total and ok == total:
            resultado, estado, obs = "PASA", "Finalizado", ""
        elif prep:
            resultado, estado = "NO PASA", "Bloqueado"
            obs = "Falló la preparación (@Before), ningún paso se ejecutó: " + _primera_linea(prep)
        elif fallos:
            ej, paso = fallos[0]
            resultado, estado = "NO PASA", "En construcción"
            obs = "[%s] Falla en el paso «%s %s»: %s" % (categoria, paso["palabra"], paso["texto"], _primera_linea(paso["error"]))
            if len(lista) > 1:
                obs = "[%d filas de Ejemplos] " % len(lista) + obs
        elif sin_implementar:
            resultado, estado = "NO PASA", "En construcción"
            obs = "Hay %d paso(s) sin implementar o ambiguos: %s" % (
                len(sin_implementar), _primera_linea(sin_implementar[0]["texto"]))
        else:
            resultado, estado, obs = "NO PASA", "En construcción", "Pasos omitidos por una falla previa."
        resumen[cp] = {"resultado": resultado, "estado": estado, "observaciones": obs,
                       "categoria": categoria, "origen": origen, "paso_fallido": paso_fallido,
                       "titulo": lista[0]["nombre"], "feature": lista[0]["feature"],
                       "pasos_total": total, "pasos_ok": ok, "duracion_s": round(duracion, 2),
                       "ejecuciones": lista}
    return resumen


# --------------------------------------------------------------------------------------
# 3. Escritura en la planilla
# --------------------------------------------------------------------------------------
COLUMNAS = {"resultado": 6, "estado": 7, "observaciones": 11, "pasos_total": 13, "pasos_ok": 14, "duracion_s": 15}
BORDE = Side(style="medium")


def escribir_planilla(ruta_entrada, ruta_salida, resumen):
    wb = openpyxl.load_workbook(ruta_entrada)
    ws = wb[HOJA_ESC]

    for numero in range(1, TOTAL_CP + 1):
        fila = FILA_INICIO + numero - 1
        datos = resumen.get("CP%d" % numero)
        if not datos:
            ws.cell(fila, COLUMNAS["resultado"]).value = "Pendiente de ejecución"
            ws.cell(fila, COLUMNAS["estado"]).value = "Pendiente"
            for k in ("observaciones", "pasos_total", "pasos_ok", "duracion_s"):
                ws.cell(fila, COLUMNAS[k]).value = None
            continue
        for k in COLUMNAS:
            ws.cell(fila, COLUMNAS[k]).value = datos[k] if datos[k] != "" else None

    # Hoja de detalle por paso
    if HOJA_DET in wb.sheetnames:
        del wb[HOJA_DET]
    wd = wb.create_sheet(HOJA_DET)
    encabezados = ["CP", "Funcionalidad (feature)", "Escenario ejecutado", "N° paso", "Palabra", "Paso",
                   "Estado", "Duración (s)", "Método Java", "Evidencia (captura)", "Error"]
    wd.append(encabezados)
    relleno = PatternFill("solid", fgColor="CCCCFF")
    for c in wd[1]:
        c.font = Font(name="Arial", size=10, bold=True)
        c.fill = relleno
        c.alignment = Alignment(horizontal="center", vertical="center", wrap_text=True)
        c.border = Border(left=BORDE, right=BORDE, top=BORDE, bottom=BORDE)
    for cp in sorted(resumen, key=lambda x: int(x[2:])):
        for ej in resumen[cp]["ejecuciones"]:
            for i, p in enumerate(ej["pasos"], start=1):
                wd.append([cp, ej["feature"], ej["nombre"], i, p["palabra"], p["texto"], p["estado"],
                           round(p["duracion"], 3), p["metodo"], p["evidencia"], _primera_linea(p["error"], 400)])
    for fila in wd.iter_rows(min_row=2):
        for c in fila:
            c.font = Font(name="Arial", size=10)
            c.alignment = Alignment(vertical="top", wrap_text=True)
    for col, ancho in zip("ABCDEFGHIJK", (7, 26, 40, 8, 11, 55, 11, 12, 38, 50, 60)):
        wd.column_dimensions[col].width = ancho
    wd.freeze_panes = "A2"
    wd.auto_filter.ref = "A1:K%d" % max(2, wd.max_row)

    wb.save(ruta_salida)


def escribir_json(ruta, resumen):
    """Resumen por CP (sin los objetos internos) para generar el informe."""
    datos = {}
    for cp, d in resumen.items():
        item = {k: v for k, v in d.items() if k != "ejecuciones"}
        item["pasos"] = [{"n": i, "palabra": p["palabra"], "texto": p["texto"], "estado": p["estado"],
                          "duracion_s": round(p["duracion"], 3), "evidencia": p["evidencia"],
                          "error": _primera_linea(p["error"], 300)}
                         for ej in d["ejecuciones"] for i, p in enumerate(ej["pasos"], start=1)]
        datos[cp] = item
    with open(ruta, "w", encoding="utf-8") as fh:
        json.dump(datos, fh, ensure_ascii=False, indent=1)


def imprimir_resumen(resumen):
    total = TOTAL_CP
    ejecutados = len(resumen)
    pasan = sum(1 for d in resumen.values() if d["resultado"] == "PASA")
    pasos_total = sum(d["pasos_total"] for d in resumen.values())
    pasos_ok = sum(d["pasos_ok"] for d in resumen.values())
    print("\n=== RESUMEN DE EJECUCIÓN ===")
    print("Escenarios (CP) con resultados : %d de %d" % (ejecutados, total))
    print("PASA                           : %d" % pasan)
    print("NO PASA                        : %d" % (ejecutados - pasan))
    print("Sin resultados (pendientes)    : %d" % (total - ejecutados))
    if pasos_total:
        print("Pasos exitosos / totales       : %d / %d (%.1f %%)" % (pasos_ok, pasos_total, 100.0 * pasos_ok / pasos_total))
    faltan = [("CP%d" % n) for n in range(1, total + 1) if ("CP%d" % n) not in resumen]
    if faltan:
        print("CP sin resultados              :", ", ".join(faltan))
    print("\nCP que NO pasan (revisar la captura del paso en la carpeta 'evidencias'):")
    for cp in sorted((c for c, d in resumen.items() if d["resultado"] != "PASA"), key=lambda x: int(x[2:])):
        print("  %-5s %s" % (cp, resumen[cp]["observaciones"]))


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--json", default="target/cucumber-json")
    ap.add_argument("--features", default="src/test/resources/Features")
    ap.add_argument("--planilla", default="planilla/Planilla_Escenarios_Resultados_MaderasMyM.xlsx")
    ap.add_argument("--salida", default=None)
    ap.add_argument("--resumen-json", default="resumen_resultados.json",
                    help="archivo JSON con el resumen por CP (insumo del informe)")
    args = ap.parse_args()

    mapa = mapear_features(args.features)
    if not mapa:
        sys.exit("No se encontraron archivos .feature en: %s" % args.features)
    ejecuciones = leer_ejecuciones(args.json, mapa)
    sin_cp = [e for e in ejecuciones if not e["cps"]]
    if sin_cp:
        print("AVISO: %d ejecución(es) no se pudieron asociar a un CP (revisar etiquetas @CPn):" % len(sin_cp))
        for e in sin_cp[:10]:
            print("   ", e["feature"], "línea", e["linea"], e["nombre"])
    resumen = consolidar_por_cp(ejecuciones)
    salida = args.salida or args.planilla
    escribir_planilla(args.planilla, salida, resumen)
    escribir_json(args.resumen_json, resumen)
    imprimir_resumen(resumen)
    print("\nPlanilla actualizada:", salida)
    print("Resumen JSON         :", args.resumen_json)


if __name__ == "__main__":
    main()
