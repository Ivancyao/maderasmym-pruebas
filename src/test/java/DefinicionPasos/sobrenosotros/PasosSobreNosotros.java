package DefinicionPasos.sobrenosotros;

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
 * Definicion de pasos - Funcionalidad: Pagina "Sobre Nosotros"
 * Requerimiento: RQ-03 Navegabilidad e interaccion
 * Escenarios cubiertos (Matriz de Trazabilidad): CP53, CP54, CP55, CP56
 *
 * Los localizadores y los mensajes esperados fueron verificados contra el HTML y el comportamiento
 * REAL de www.maderasmym.cl (carpetas "explorar" y "explorar2" del repositorio). Los resultados
 * se registran con Utility.resultado(...) (PASA / NO PASA) o Utility.noEjecutado(...) cuando el
 * escenario no se puede ejecutar de forma responsable (ver el motivo en cada caso).
 */
public class PasosSobreNosotros {

	private static final String CHROMEDRIVER_PATH = "./src/test/resources/chrome/chromedriver.exe";
	WebDriver driver;
	private int numeroPaso = 0;

	@Before
	public void setUp() {
		driver = Utility.crearDriver(CHROMEDRIVER_PATH);
		Utility.ajustarVentana(driver);
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

	/** Reemplaza el navegador por uno con emulacion de movil (375x667, como un telefono). */
	private void abrirComoMovil() {
		if (driver != null) {
			driver.quit();
		}
		driver = Utility.crearDriverMovil(CHROMEDRIVER_PATH, 375, 667);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
	}


	private String original = "";

	@Given("que un usuario accede al menú {string}")
	public void que_un_usuario_accede_al_menu(String menu) {
		Sitio.abrirInicio(driver);
		WebElement enlace = driver.findElement(By.xpath("//ul[contains(@class,'menu__links')]//a[normalize-space()='" + menu + "']"));
		Sitio.clic(driver, enlace);
		Sitio.esperarHasta(() -> driver.getCurrentUrl().contains("/aboutus"), 15);
	}

	@When("la página carga completamente")
	public void la_pagina_carga_completamente() {
		Sitio.esperarHasta(() -> Sitio.texto(driver, "body").toLowerCase().contains("misión"), 15);
	}

	@Then("se despliega correctamente la historia, misión, visión y equipo de Maderas MyM")
	public void se_despliega_correctamente_la_informacion() {
		String pagina = Sitio.texto(driver, "body").toLowerCase();
		String[] secciones = { "empresa familiar", "misión", "visión", "nuestro equipo" };
		StringBuilder faltan = new StringBuilder();
		for (String s : secciones) {
			if (!pagina.contains(s)) {
				faltan.append(s).append("; ");
			}
		}
		if (faltan.length() == 0) {
			Utility.resultado("PASA - La pagina muestra historia, mision, vision y equipo");
		} else {
			Utility.resultado("NO PASA - Secciones que no aparecen: " + faltan);
		}
	}

	@When("revisa la sección de ubicación")
	public void revisa_la_seccion_de_ubicacion() {
		List<WebElement> mapas = driver.findElements(By.cssSelector("iframe[src*='google.com/maps']"));
		if (!mapas.isEmpty()) {
			Sitio.js(driver, "arguments[0].scrollIntoView({block:'center'});", mapas.get(0));
			Sitio.pausa(2500);
		}
	}

	@Then("el mapa de Google Maps embebido se carga correctamente")
	public void el_mapa_de_google_maps_se_carga_correctamente() {
		List<WebElement> mapas = driver.findElements(By.cssSelector("iframe[src*='google.com/maps']"));
		if (!mapas.isEmpty() && mapas.get(0).isDisplayed() && mapas.get(0).getSize().getHeight() > 50) {
			Utility.resultado("PASA - El mapa de Google Maps embebido esta presente y visible (" + mapas.get(0).getSize() + ")");
		} else {
			Utility.resultado("NO PASA - No se encontro un mapa de Google Maps visible (iframes = " + mapas.size() + ")");
		}
	}

	@When("presiona {string}")
	public void presiona(String texto) {
		original = driver.getWindowHandle();
		WebElement enlace = driver.findElement(By.xpath("//a[contains(normalize-space(.),'" + texto + "')]"));
		Sitio.clic(driver, enlace);
		Sitio.esperarHasta(() -> driver.getWindowHandles().size() > 1, 8);
	}

	@Then("el sistema lo redirige correctamente a WhatsApp")
	public void el_sistema_lo_redirige_correctamente_a_whatsapp() {
		boolean nuevaPestana = driver.getWindowHandles().size() > 1;
		for (String h : driver.getWindowHandles()) {
			if (!h.equals(original)) {
				driver.switchTo().window(h);
			}
		}
		Sitio.esperarHasta(() -> driver.getCurrentUrl().contains("whatsapp") || driver.getCurrentUrl().contains("wa.me"), 15);
		String url = driver.getCurrentUrl();
		if (url.contains("whatsapp") || url.contains("wa.me")) {
			Utility.resultado("PASA - \"Contactar por WhatsApp\" abre " + url + " (nueva pestana = " + nuevaPestana + ")");
		} else {
			Utility.resultado("NO PASA - El enlace abrio " + url);
		}
	}

	// ---- CP56: vista movil real (375x667)
	@Given("que un usuario accede a {string} desde un dispositivo móvil")
	public void que_un_usuario_accede_desde_un_dispositivo_movil(String pagina) {
		abrirComoMovil();
		driver.get(Sitio.BASE + "/aboutus");
		Sitio.esperarHasta(() -> Sitio.texto(driver, "body").toLowerCase().contains("misión"), 15);
	}

	@When("la página se despliega")
	public void la_pagina_se_despliega() {
		Sitio.pausa(1500);
	}

	@Then("el contenido se adapta correctamente al tamaño de pantalla, sin elementos superpuestos")
	public void el_contenido_se_adapta_al_tamano_de_pantalla() {
		Object r = Sitio.js(driver, "return [document.documentElement.scrollWidth, window.innerWidth];");
		@SuppressWarnings("unchecked")
		List<? extends Number> medidas = (List<? extends Number>) r;
		long contenido = medidas.get(0).longValue();
		long ventana = medidas.get(1).longValue();
		if (contenido <= ventana) {
			Utility.resultado("PASA - Vista movil: ancho del contenido " + contenido + " px <= ancho de pantalla " + ventana + " px (sin desbordes)");
		} else {
			Utility.resultado("NO PASA - Vista movil: el contenido mide " + contenido + " px y la pantalla " + ventana + " px (desborde horizontal)");
		}
	}
}
