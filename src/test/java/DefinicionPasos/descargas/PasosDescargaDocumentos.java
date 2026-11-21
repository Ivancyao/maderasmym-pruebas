package DefinicionPasos.descargas;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import Utilidades.Sitio;
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
 * Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
 * Escenarios cubiertos (Matriz de Trazabilidad): CP45, CP46, CP47, CP48
 *
 * Los localizadores y los mensajes esperados fueron verificados contra el HTML y el comportamiento
 * REAL de www.maderasmym.cl (carpetas "explorar" y "explorar2" del repositorio). Los resultados
 * se registran con Utility.resultado(...) (PASA / NO PASA) o Utility.noEjecutado(...) cuando el
 * escenario no se puede ejecutar de forma responsable (ver el motivo en cada caso).
 */
public class PasosDescargaDocumentos {

	private static final String CHROMEDRIVER_PATH = "./src/test/resources/chrome/chromedriver.exe";
	WebDriver driver;
	private int numeroPaso = 0;

	@Before
	public void setUp() {
		driver = Utility.crearDriver(CHROMEDRIVER_PATH);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
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


	private String hrefDelDocumento = "";
	private List<String> enlacesPdf;

	@Given("que un usuario está en la página de inicio")
	public void que_un_usuario_esta_en_la_pagina_de_inicio() {
		Sitio.abrirInicio(driver);
	}

	@When("presiona el enlace {string}")
	public void presiona_el_enlace(String nombreDocumento) {
		WebElement enlace = null;
		for (WebElement e : driver.findElements(By.xpath("//a[contains(normalize-space(.),'" + nombreDocumento + "')]"))) {
			if (e.isDisplayed()) {
				enlace = e;
				break;
			}
		}
		if (enlace == null) {
			Utility.resultado("NO PASA - No hay un enlace visible con el texto \"" + nombreDocumento + "\"");
		}
		hrefDelDocumento = String.valueOf(Sitio.js(driver, "return arguments[0].href;", enlace));
		String original = driver.getWindowHandle();
		Sitio.clic(driver, enlace);
		Sitio.pausa(2000);
		if (driver.getWindowHandles().size() > 1) {
			Sitio.volverALaOriginal(driver, original);
		}
	}

	@Then("el sistema descarga o abre el PDF correctamente")
	public void el_sistema_descarga_o_abre_el_pdf_correctamente() {
		String estado = Sitio.estadoHttp(driver, hrefDelDocumento);
		if (estado.startsWith("200") && estado.toLowerCase().contains("pdf")) {
			Utility.resultado("PASA - " + hrefDelDocumento + " responde " + estado);
		} else {
			Utility.resultado("NO PASA - " + hrefDelDocumento + " responde \"" + estado + "\" (se esperaba 200 application/pdf)");
		}
	}

	@Given("que existen múltiples enlaces a documentos PDF en el sitio")
	public void que_existen_multiples_enlaces_a_documentos_pdf() {
		Sitio.abrirInicio(driver);
		@SuppressWarnings("unchecked")
		List<String> hrefs = (List<String>) Sitio.js(driver,
				"var s={};document.querySelectorAll('a[href$=\".pdf\"]').forEach(function(a){s[a.href]=1;});return Object.keys(s);");
		enlacesPdf = hrefs;
		if (enlacesPdf.size() < 2) {
			Utility.resultado("NO PASA - Se esperaban varios enlaces PDF y se encontraron " + enlacesPdf.size());
		}
	}

	@When("el usuario verifica cada uno de ellos")
	public void el_usuario_verifica_cada_uno_de_ellos() {
		Sitio.pausa(300);
	}

	@Then("ninguno retorna un error 404 o enlace roto")
	public void ninguno_retorna_un_error_404_o_enlace_roto() {
		StringBuilder rotos = new StringBuilder();
		for (String url : enlacesPdf) {
			String estado = Sitio.estadoHttp(driver, url);
			System.out.println("Enlace PDF: " + url + " -> " + estado);
			if (!estado.startsWith("200")) {
				rotos.append(url).append(" (").append(estado).append(") ");
			}
		}
		if (rotos.length() == 0) {
			Utility.resultado("PASA - Los " + enlacesPdf.size() + " enlaces PDF responden 200");
		} else {
			Utility.resultado("NO PASA - Enlaces con error: " + rotos);
		}
	}
}
