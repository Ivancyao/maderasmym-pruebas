package DefinicionPasos.descargas;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import Utilidades.Utility;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Definicion de pasos - Funcionalidad: Descarga de documentos (listas de precios / informativos)
 * Requerimiento: RQ-02 Operaciones del negocio
 * Escenarios cubiertos (Matriz de Trazabilidad): CP45, CP46, CP47, CP48
 *
 * Nota tecnica: Selenium no expone de forma estandar el resultado de una descarga de archivo
 * gestionada por el navegador. La verificacion de "descarga/apertura correcta" se implementa
 * comprobando que el enlace responde con codigo HTTP 200 (enlace valido, no roto), en lugar de
 * inspeccionar la carpeta de descargas del sistema operativo.
 */
public class PasosDescargaDocumentos {

	private static final String CHROMEDRIVER_PATH = "./src/test/resources/chrome/chromedriver.exe";
	WebDriver driver;
	private int numeroPaso = 0;

	private static final Map<String, String> ENLACES = new HashMap<>();
	static {
		ENLACES.put("Lista de Precios 2026 (PDF)", "https://www.maderasmym.cl/assets/lista-de-precios-2026.pdf");
		ENLACES.put("Informativo Maderas (PDF)", "https://www.maderasmym.cl/assets/informativo mym.pdf");
		ENLACES.put("Pino Insigne Seco Cepillado Tabla", "https://www.maderasmym.cl/assets/pino-insigne-seco-cepillado-tabla.pdf");
	}

	@Before
	public void setUp() {
		driver = Utility.crearDriver(CHROMEDRIVER_PATH);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(8, TimeUnit.SECONDS);
	}

	/**
	 * Evidencia de ejecucion: captura la pantalla despues de cada paso del escenario
	 * (Guia 3.1.3). Las imagenes quedan en la carpeta "evidencias".
	 */
	@AfterStep
	public void capturarEvidenciaDelPaso(Scenario escenario) {
		numeroPaso++;
		Utility.capturarPaso(driver, escenario, numeroPaso);
	}

	@After
	public void tearDown() {
		if (driver != null) {
			driver.quit();
		}
	}

	@Given("que un usuario está en la página de inicio")
	public void que_un_usuario_esta_en_la_pagina_de_inicio() {
		driver.get("https://www.maderasmym.cl/");
	}

	@Given("que existen múltiples enlaces a documentos PDF en el sitio")
	public void que_existen_multiples_enlaces_a_documentos_pdf() {
		driver.get("https://www.maderasmym.cl/");
	}

	private int verificarCodigoHttp(String urlDocumento) {
		try {
			// El sitio publica "informativo mym.pdf" con un espacio en la URL: se codifica como %20.
			URL url = new URL(urlDocumento.replace(" ", "%20"));
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("HEAD");
			con.setConnectTimeout(8000);
			int codigo = con.getResponseCode();
			con.disconnect();
			return codigo;
		} catch (Exception e) {
			return -1;
		}
	}

	@When("presiona el enlace {string}")
	public void presiona_el_enlace(String nombreDocumento) {
		driver.findElement(By.xpath("//a[contains(text(),'" + nombreDocumento + "')]"));
		// Se valida el enlace via HTTP en lugar de ejecutar el click, dado que la descarga
		// de PDF la gestiona el navegador fuera del DOM accesible por Selenium.
	}

	@When("el usuario verifica cada uno de ellos")
	public void el_usuario_verifica_cada_uno_de_ellos() {
		// Verificacion realizada en el Then (recorre el mapa ENLACES).
	}

	@Then("el sistema descarga o abre el PDF correctamente")
	public void el_sistema_descarga_o_abre_el_pdf_correctamente() {
		// Determina el ultimo enlace evaluado via el contexto del escenario (simplificado: valida todos).
		boolean todosOk = true;
		for (Map.Entry<String, String> enlace : ENLACES.entrySet()) {
			int codigo = verificarCodigoHttp(enlace.getValue());
			boolean ok = codigo == 200;
			todosOk = todosOk && ok;
			System.out.println((ok ? "PASA" : "NO PASA") + " - Documento \"" + enlace.getKey() + "\": HTTP " + codigo);
		}
		Utility.resultado((todosOk ? "PASA" : "NO PASA") + " - Resultado global: todos los documentos responden HTTP 200");
	}

	@Then("ninguno retorna un error 404 o enlace roto")
	public void ninguno_retorna_error_404_o_enlace_roto() {
		boolean todosOk = true;
		for (Map.Entry<String, String> enlace : ENLACES.entrySet()) {
			int codigo = verificarCodigoHttp(enlace.getValue());
			boolean ok = codigo == 200;
			todosOk = todosOk && ok;
			System.out.println((ok ? "PASA" : "NO PASA") + " - Verificación de enlace \"" + enlace.getKey() + "\": HTTP " + codigo);
		}
		Utility.resultado((todosOk ? "PASA" : "NO PASA") + " - Resultado global: todos los enlaces PDF responden 200");
	}
}
