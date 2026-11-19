package Utilidades;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	 * Registra el resultado de una verificacion. Si el mensaje comienza con "NO PASA" se lanza un
	 * AssertionError, de modo que Cucumber marque el paso como fallido (y quede en el JSON/reporte);
	 * antes, estos resultados solo se imprimian por consola y el paso aparecia siempre como exitoso.
	 */
	public static void resultado(String mensaje) {
		System.out.println(mensaje);
		if (mensaje != null && mensaje.startsWith("NO PASA")) {
			throw new AssertionError(mensaje);
		}
	}

	/**
	 * Crea el navegador. Por defecto usa la ruta recibida (ejecucion local en Windows con ventana).
	 * En un servidor sin pantalla (GitHub Actions, Colab) se configura con variables de entorno:
	 *   CHROMEDRIVER_PATH = ruta del chromedriver | CHROME_BIN = ruta de Chrome (opcional) | HEADLESS=true = sin ventana.
	 */
	public static WebDriver crearDriver(String rutaPorDefecto) {
		String ruta = System.getenv("CHROMEDRIVER_PATH");
		if (ruta == null || ruta.trim().isEmpty()) {
			ruta = rutaPorDefecto;
		}
		System.setProperty("webdriver.chrome.driver", ruta);
		ChromeOptions opciones = new ChromeOptions();
		String binario = System.getenv("CHROME_BIN");
		if (binario != null && !binario.trim().isEmpty()) {
			opciones.setBinary(binario);
		}
		if ("true".equalsIgnoreCase(System.getenv("HEADLESS"))) {
			opciones.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu",
					"--remote-allow-origins=*", "--window-size=1920,1080");
		}
		return new ChromeDriver(opciones);
	}
}
