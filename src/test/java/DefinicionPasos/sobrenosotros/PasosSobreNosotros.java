package DefinicionPasos.sobrenosotros;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
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
 * Definicion de pasos - Funcionalidad: Pagina "Sobre Nosotros"
 * Requerimiento: RQ-03 Navegabilidad e interaccion
 * Escenarios cubiertos (Matriz de Trazabilidad): CP53, CP54, CP55, CP56
 */
public class PasosSobreNosotros {

	private static final String CHROMEDRIVER_PATH = "./src/test/resources/chrome/chromedriver.exe";
	WebDriver driver;
	private int numeroPaso = 0;

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

	@Given("que un usuario accede al menú {string}")
	public void que_un_usuario_accede_al_menu(String menu) {
		driver.get("https://www.maderasmym.cl/");
		driver.findElement(By.xpath("//a[@href='https://www.maderasmym.cl/aboutus']")).click();
	}

	@Given("que un usuario accede a {string} desde un dispositivo móvil")
	public void que_un_usuario_accede_a_desde_un_dispositivo_movil(String pagina) {
		driver.manage().window().setSize(new Dimension(375, 667));
		driver.get("https://www.maderasmym.cl/aboutus");
	}

	@When("la página carga completamente")
	public void la_pagina_carga_completamente() {
		// La carga completa se garantiza con el implicit wait y la verificacion del Then.
	}

	@When("revisa la sección de ubicación")
	public void revisa_la_seccion_de_ubicacion() {
		// Lectura directa del iframe del mapa en el Then.
	}

	@When("presiona {string}")
	public void presiona(String texto) {
		driver.findElement(By.xpath("//a[contains(@href,'wa.me/56976200646')]")).click();
	}

	@When("la página se despliega")
	public void la_pagina_se_despliega() {
		// Sin accion: se valida el layout en el Then.
	}

	@Then("se despliega correctamente la historia, misión, visión y equipo de Maderas MyM")
	public void se_despliega_correctamente_la_informacion_institucional() {
		String url = driver.getCurrentUrl();
		String cuerpo = driver.findElement(By.tagName("body")).getText().toLowerCase();
		boolean tieneContenido = cuerpo.length() > 200;
		Utility.resultado((url.contains("/aboutus") && tieneContenido ? "PASA" : "NO PASA")
				+ " - Sobre Nosotros: URL = " + url + ", longitud de contenido = " + cuerpo.length());
	}

	@Then("el mapa de Google Maps embebido se carga correctamente")
	public void el_mapa_de_google_maps_embebido_se_carga_correctamente() {
		List<WebElement> mapa = driver.findElements(By.xpath("//iframe[contains(@src,'google.com/maps')]"));
		Utility.resultado((!mapa.isEmpty() ? "PASA" : "NO PASA") + " - Mapa embebido: iframes de Google Maps encontrados = " + mapa.size());
	}

	@Then("el sistema lo redirige correctamente a WhatsApp")
	public void el_sistema_lo_redirige_correctamente_a_whatsapp() {
		Set<String> ventanas = driver.getWindowHandles();
		String url = "";
		try {
			driver.switchTo().window((String) ventanas.toArray()[ventanas.size() - 1]);
			url = driver.getCurrentUrl();
		} catch (Exception e) {
			// se mantiene cadena vacia
		}
		boolean ok = url.contains("wa.me") || url.contains("whatsapp");
		Utility.resultado((ok ? "PASA" : "NO PASA") + " - Enlace a WhatsApp desde Sobre Nosotros: URL = " + url);
	}

	@Then("el contenido se adapta correctamente al tamaño de pantalla, sin elementos superpuestos")
	public void el_contenido_se_adapta_al_tamano_de_pantalla() {
		long anchoDocumento = (Long) ((org.openqa.selenium.JavascriptExecutor) driver)
				.executeScript("return document.documentElement.scrollWidth;");
		boolean sinScrollHorizontal = anchoDocumento <= 375 + 5;
		Utility.resultado((sinScrollHorizontal ? "PASA" : "NO PASA") + " - Vista móvil 375x667: ancho del documento = " + anchoDocumento + " px");
	}
}
