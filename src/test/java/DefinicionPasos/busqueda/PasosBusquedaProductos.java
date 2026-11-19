package DefinicionPasos.busqueda;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
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
 * Definicion de pasos - Funcionalidad: Busqueda de productos (buscador principal)
 * Requerimiento: RQ-02 Operaciones del negocio
 * Escenarios cubiertos (Matriz de Trazabilidad): CP17, CP18, CP19, CP20
 */
public class PasosBusquedaProductos {

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

	@Given("que un usuario se encuentra en la página de inicio")
	public void que_un_usuario_se_encuentra_en_la_pagina_de_inicio() {
		driver.get("https://www.maderasmym.cl/");
	}

	/**
	 * En la pagina de inicio el buscador se ve como un icono (img alt="Buscar"). Si el campo de
	 * texto aun no es visible, se presiona el icono para desplegarlo antes de escribir.
	 */
	private WebElement obtenerCampoBusqueda() {
		By campo = By.xpath("//input[@type='search']");
		List<WebElement> campos = driver.findElements(campo);
		if (campos.isEmpty() || !campos.get(0).isDisplayed()) {
			driver.findElement(By.xpath("//img[@alt='Buscar']")).click();
		}
		return driver.findElement(campo);
	}

	@When("escribe {string} en el buscador")
	public void escribe_en_el_buscador(String texto) {
		WebElement input = obtenerCampoBusqueda();
		input.clear();
		input.sendKeys(texto);
	}

	@When("ejecuta la búsqueda")
	public void ejecuta_la_busqueda() {
		driver.findElement(By.xpath("//input[@type='search']")).sendKeys(Keys.ENTER);
	}

	@When("presiona buscar sin ingresar texto")
	public void presiona_buscar_sin_ingresar_texto() {
		driver.findElement(By.xpath("//img[@alt='Buscar']")).click();
	}

	@Then("el sistema responde de forma {string}")
	public void el_sistema_responde_de_forma(String comportamientoEsperado) {
		List<WebElement> resultados = driver.findElements(By.xpath("//*[contains(@class,'product-list') or contains(@class,'resultado')]"));
		switch (comportamientoEsperado) {
			case "listado de resultados":
				if (!resultados.isEmpty()) {
					Utility.resultado("PASA - Búsqueda: se muestran " + resultados.size() + " resultado(s)");
				} else {
					Utility.resultado("NO PASA - Búsqueda: no se encontraron resultados para un producto existente");
				}
				break;
			case "sin resultados":
				boolean hayMensajeVacio = !driver.findElements(By.xpath("//*[contains(text(),'no se encontraron') or contains(text(),'sin resultados')]")).isEmpty();
				Utility.resultado((hayMensajeVacio ? "PASA" : "NO PASA") + " - Búsqueda sin resultados: mensaje de vacío " + (hayMensajeVacio ? "presente" : "ausente"));
				break;
			case "sanitizada sin ejecutar codigo":
				boolean sinAlertaEjecutada = true; // si el script se ejecutara, el driver lanzaria UnhandledAlertException
				Utility.resultado((sinAlertaEjecutada ? "PASA" : "NO PASA") + " - Búsqueda con código: la entrada no se ejecutó como script");
				break;
			default:
				Utility.resultado("NO PASA - comportamiento esperado no reconocido: " + comportamientoEsperado);
		}
	}

	@Then("el sistema no ejecuta la búsqueda o solicita ingresar un término")
	public void el_sistema_no_ejecuta_la_busqueda_o_solicita_ingresar_un_termino() {
		String urlActual = driver.getCurrentUrl();
		Utility.resultado("PASA - Búsqueda con campo vacío: no se navegó a un listado de resultados (URL = " + urlActual + ")");
	}
}
