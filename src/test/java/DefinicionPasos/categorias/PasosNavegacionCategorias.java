package DefinicionPasos.categorias;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
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
 * Definicion de pasos - Funcionalidad: Navegacion por categorias de productos
 * Requerimiento: RQ-02 Operaciones del negocio
 * Escenarios cubiertos (Matriz de Trazabilidad): CP21, CP22, CP23, CP24
 */
public class PasosNavegacionCategorias {

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

	@Given("que un usuario está en el menú {string}")
	public void que_un_usuario_esta_en_el_menu(String menu) {
		driver.get("https://www.maderasmym.cl/");
	}

	@When("selecciona {string}")
	public void selecciona_categoria(String categoria) {
		driver.findElement(By.xpath("//a[contains(text(),'" + categoria + "')]")).click();
	}

	@When("despliega la categoría {string} y selecciona la medida {string}")
	public void despliega_la_categoria_y_selecciona_la_medida(String categoria, String medida) {
		driver.findElement(By.xpath("//a[contains(text(),'" + categoria + "')]")).click();
		driver.findElement(By.xpath("//a[contains(text(),'" + medida + "')]")).click();
	}

	@When("navega a la categoría {string} y revisa el listado de productos desplegado")
	public void navega_a_la_categoria_y_revisa_el_listado(String categoria) {
		driver.findElement(By.xpath("//a[contains(text(),'" + categoria + "')]")).click();
	}

	@When("accede a una categoría sin productos disponibles")
	public void accede_a_una_categoria_sin_productos_disponibles() {
		driver.get("https://www.maderasmym.cl/categoria/sin-stock");
	}

	@Then("el sistema despliega el listado de productos de esa categoría")
	public void el_sistema_despliega_el_listado_de_productos() {
		List<WebElement> productos = driver.findElements(By.xpath("//*[contains(@class,'product-grid')]"));
		Utility.resultado((!productos.isEmpty() ? "PASA" : "NO PASA") + " - Navegación por categoría: productos encontrados = " + productos.size());
	}

	@Then("el sistema muestra solo los productos de esa medida específica")
	public void el_sistema_muestra_solo_los_productos_de_esa_medida() {
		List<WebElement> productos = driver.findElements(By.xpath("//*[contains(@class,'product-grid')]"));
		Utility.resultado((!productos.isEmpty() ? "PASA" : "NO PASA") + " - Filtro por medida: productos encontrados = " + productos.size()
				+ " (validar manualmente que todos correspondan a la medida seleccionada)");
	}

	@Then("todos los productos mostrados pertenecen exclusivamente a la categoría seleccionada")
	public void todos_los_productos_pertenecen_a_la_categoria() {
		List<WebElement> productos = driver.findElements(By.xpath("//*[contains(@class,'product-grid')]"));
		Utility.resultado((!productos.isEmpty() ? "PASA" : "NO PASA") + " - Consistencia de categoría: " + productos.size()
				+ " producto(s) listados para revisión de coherencia");
	}

	@Then("el sistema muestra un mensaje indicando que no hay productos disponibles")
	public void el_sistema_muestra_mensaje_sin_productos() {
		boolean hayMensaje = !driver.findElements(By.xpath("//*[contains(text(),'sin productos disponibles')]")).isEmpty();
		Utility.resultado((hayMensaje ? "PASA" : "NO PASA") + " - Categoría sin stock: mensaje de 'sin productos' " + (hayMensaje ? "presente" : "ausente"));
	}
}
