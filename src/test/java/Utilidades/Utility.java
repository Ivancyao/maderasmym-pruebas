package Utilidades;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import io.cucumber.java.Scenario;

/**
 * Utilidades para la captura de evidencias de ejecucion (Guia 3.1.3 / PPT 3.1.1).
 *
 * Cada paso de cada escenario genera una imagen en la carpeta "evidencias", organizada por
 * escenario. Si el escenario falla, la captura tambien se adjunta al reporte de Cucumber, para
 * que el paso que fallo quede visible junto al mensaje de error.
 */
public class Utility {

	/** Carpeta donde se guardan las capturas (relativa a la raiz del proyecto). */
	public static final String CARPETA_EVIDENCIAS = "evidencias";

	/**
	 * Toma una captura de pantalla y la guarda en la ruta indicada (crea las carpetas si no existen).
	 */
	public static void captureScreenShot(WebDriver webdriver, String filePath) throws IOException {
		// Convierte el webdriver en TakesScreenshot
		TakesScreenshot screenShot = (TakesScreenshot) webdriver;
		// Crea un archivo temporal con la imagen y lo copia al destino
		File screenFile = screenShot.getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(screenFile, new File(filePath));
	}

	/**
	 * Fecha y hora del sistema, para colocar en el nombre del archivo de evidencia.
	 */
	public static String GetTimeStampValue() {
		return new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
	}

	/**
	 * Deja un texto apto para nombre de carpeta o archivo (sin tildes, espacios ni simbolos).
	 */
	public static String sanear(String texto) {
		String limpio = java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD)
				.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
				.replaceAll("[^A-Za-z0-9]+", "_")
				.replaceAll("^_+|_+$", "");
		return limpio.length() > 60 ? limpio.substring(0, 60) : limpio;
	}

	/**
	 * Captura la pantalla despues de cada paso. Se invoca desde el hook @AfterStep de cada clase
	 * de definicion de pasos.
	 *
	 * @param driver     navegador en uso (puede ser null si el paso fallo antes de abrirlo)
	 * @param escenario  escenario en ejecucion (nombre y estado)
	 * @param numeroPaso posicion del paso dentro del escenario
	 */
	public static void capturarPaso(WebDriver driver, Scenario escenario, int numeroPaso) {
		if (driver == null) {
			return;
		}
		try {
			String estado = escenario.isFailed() ? "FALLA" : "OK";
			String ruta = CARPETA_EVIDENCIAS + "/" + sanear(escenario.getName()) + "/paso"
					+ numeroPaso + "_" + estado + "_" + GetTimeStampValue() + ".png";
			captureScreenShot(driver, ruta);
			if (escenario.isFailed()) {
				diagnostico("[captura FALLA] " + sanear(escenario.getName()) + " paso" + numeroPaso + ": " + describirVentana(driver));
			}
			// Deja la ruta de la evidencia dentro del reporte de Cucumber (JSON / HTML)
			escenario.log("Evidencia: " + ruta);
			if (escenario.isFailed()) {
				// Solo se adjunta la imagen al reporte cuando falla, para no inflar los archivos JSON
				byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
				escenario.attach(png, "image/png", "Paso " + numeroPaso + " - " + estado);
			}
		} catch (Exception e) {
			System.out.println("No se pudo capturar la evidencia del paso " + numeroPaso + ": " + e.getMessage());
		}
	}

	/**
	 * Registra el resultado de una verificacion. Si el mensaje comienza con "NO PASA" (o "NO EJECUTADO")
	 * se lanza un AssertionError, de modo que Cucumber marque el paso como fallido (y quede en el
	 * JSON/reporte); antes, estos resultados solo se imprimian por consola y el paso aparecia siempre
	 * como exitoso.
	 */
	public static void resultado(String mensaje) {
		System.out.println(mensaje);
		if (mensaje != null && (mensaje.startsWith("NO PASA") || mensaje.startsWith("NO EJECUTADO"))) {
			throw new AssertionError(mensaje);
		}
	}

	/**
	 * Marca un escenario como NO EJECUTADO (bloqueado) y explica el motivo. Se usa cuando la
	 * precondicion no puede cumplirse de forma responsable en el sitio real (por ejemplo: exige
	 * resolver un CAPTCHA, requiere una cuenta verificada que no se puede crear, o la accion
	 * generaria una cuenta o un pedido real en el sitio de la empresa). No es un defecto del sitio.
	 */
	public static void noEjecutado(String motivo) {
		resultado("NO EJECUTADO - " + motivo);
	}

	/** Agrega una linea de diagnostico al archivo entorno.txt (se sube junto con los resultados). */
	public static void diagnostico(String linea) {
		System.out.println(linea);
		try {
			java.io.FileWriter fw = new java.io.FileWriter("entorno.txt", true);
			fw.write(linea + System.lineSeparator());
			fw.close();
		} catch (Exception e) {
			// el diagnostico es opcional
		}
	}

	/** Describe el tamano de ventana y de viewport que ve el navegador (para diagnostico). */
	public static String describirVentana(WebDriver driver) {
		try {
			Object r = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript(
					"return 'inner=' + window.innerWidth + 'x' + window.innerHeight + ' outer=' + window.outerWidth + 'x' + window.outerHeight + ' dpr=' + window.devicePixelRatio + ' screen=' + screen.width + 'x' + screen.height;");
			return String.valueOf(r) + " ventana=" + driver.manage().window().getSize();
		} catch (Exception e) {
			return "no disponible (" + e.getMessage() + ")";
		}
	}

	/** Ancho real del viewport (window.innerWidth) o -1 si no se puede leer. */
	public static long anchoViewport(WebDriver driver) {
		try {
			Object r = ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("return window.innerWidth;");
			return ((Number) r).longValue();
		} catch (Exception e) {
			return -1;
		}
	}

	/**
	 * Fija el viewport de escritorio con el protocolo de depuracion de Chrome (CDP), a traves del
	 * endpoint /goog/cdp/execute de chromedriver. Es la via mas confiable en Chrome sin pantalla.
	 */
	public static boolean fijarViewportCdp(WebDriver driver, int ancho, int alto) {
		try {
			org.openqa.selenium.remote.RemoteWebDriver rwd = (org.openqa.selenium.remote.RemoteWebDriver) driver;
			java.net.URL base = ((org.openqa.selenium.remote.HttpCommandExecutor) rwd.getCommandExecutor())
					.getAddressOfRemoteServer();
			java.net.URL url = new java.net.URL(base.toString().replaceAll("/+$", "") + "/session/"
					+ rwd.getSessionId() + "/goog/cdp/execute");
			String cuerpo = "{\"cmd\":\"Emulation.setDeviceMetricsOverride\",\"params\":{\"width\":" + ancho
					+ ",\"height\":" + alto + ",\"deviceScaleFactor\":1,\"mobile\":false,\"screenWidth\":" + ancho + ",\"screenHeight\":" + alto + "}}";
			java.net.HttpURLConnection con = (java.net.HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			con.getOutputStream().write(cuerpo.getBytes("UTF-8"));
			int codigo = con.getResponseCode();
			con.disconnect();
			return codigo == 200;
		} catch (Exception e) {
			System.out.println("No se pudo fijar el viewport por CDP: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Deja el navegador con viewport de escritorio. Con ventana visible (ejecucion local) se maximiza.
	 * En Chrome sin pantalla (headless) maximize()/--window-size no son confiables (la ventana queda en
	 * ~800x457 y el sitio muestra su diseno de tablet), por eso se verifica el ancho real y, si es
	 * menor a 1200 px, se prueba setSize y luego el viewport por CDP.
	 */
	public static void ajustarVentana(WebDriver driver) {
		if (!"true".equalsIgnoreCase(System.getenv("HEADLESS"))) {
			driver.manage().window().maximize();
			return;
		}
		String log = "[viewport] inicial: " + describirVentana(driver);
		if (anchoViewport(driver) < 1200) {
			try {
				driver.manage().window().setSize(new org.openqa.selenium.Dimension(1920, 1080));
			} catch (Exception e) {
				log += " | setSize error: " + e.getMessage();
			}
			log += " | tras setSize: " + describirVentana(driver);
		}
		if (anchoViewport(driver) < 1200) {
			boolean ok = fijarViewportCdp(driver, 1366, 768);
			log += " | tras CDP (" + ok + "): " + describirVentana(driver);
		}
		diagnostico(log);
	}

	private static ChromeOptions opcionesBase() {
		ChromeOptions opciones = new ChromeOptions();
		String binario = System.getenv("CHROME_BIN");
		if (binario != null && !binario.trim().isEmpty()) {
			opciones.setBinary(binario);
		}
		// Los alert()/confirm() del sitio se leen desde las pruebas (no se descartan solos)
		opciones.setCapability("unhandledPromptBehavior", "ignore");
		if ("true".equalsIgnoreCase(System.getenv("HEADLESS"))) {
			opciones.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu",
					"--remote-allow-origins=*", "--window-size=1366,768");
			// Viewport de escritorio fijo (no movil, sin tactil), independiente del tamano real de la ventana
			Map<String, Object> metricas = new HashMap<String, Object>();
			metricas.put("width", 1366);
			metricas.put("height", 768);
			metricas.put("pixelRatio", 1.0);
			metricas.put("touch", false);
			metricas.put("mobile", false);
			Map<String, Object> escritorio = new HashMap<String, Object>();
			escritorio.put("deviceMetrics", metricas);
			opciones.setExperimentalOption("mobileEmulation", escritorio);
		}
		return opciones;
	}

	private static void fijarRutaDelDriver(String rutaPorDefecto) {
		String ruta = System.getenv("CHROMEDRIVER_PATH");
		if (ruta == null || ruta.trim().isEmpty()) {
			ruta = rutaPorDefecto;
		}
		System.setProperty("webdriver.chrome.driver", ruta);
	}

	/**
	 * Crea el navegador. Por defecto usa la ruta recibida (ejecucion local en Windows con ventana).
	 * En un servidor sin pantalla (GitHub Actions, Colab) se configura con variables de entorno:
	 *   CHROMEDRIVER_PATH = ruta del chromedriver | CHROME_BIN = ruta de Chrome (opcional) | HEADLESS=true = sin ventana.
	 */
	public static WebDriver crearDriver(String rutaPorDefecto) {
		fijarRutaDelDriver(rutaPorDefecto);
		return new ChromeDriver(opcionesBase());
	}

	/**
	 * Crea el navegador en modo de emulacion de dispositivo movil (viewport real de 375x667), de modo
	 * que las reglas CSS responsivas del sitio se apliquen igual que en un telefono. En Chrome sin
	 * pantalla (headless) no se puede reducir la ventana bajo ~500 px, por eso se usa la emulacion.
	 */
	public static WebDriver crearDriverMovil(String rutaPorDefecto, int ancho, int alto) {
		fijarRutaDelDriver(rutaPorDefecto);
		ChromeOptions opciones = opcionesBase();
		Map<String, Object> metricas = new HashMap<String, Object>();
		metricas.put("width", ancho);
		metricas.put("height", alto);
		metricas.put("pixelRatio", 2.0);
		Map<String, Object> movil = new HashMap<String, Object>();
		movil.put("deviceMetrics", metricas);
		movil.put("userAgent", "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 "
				+ "(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");
		opciones.setExperimentalOption("mobileEmulation", movil);
		return new ChromeDriver(opciones);
	}
}
