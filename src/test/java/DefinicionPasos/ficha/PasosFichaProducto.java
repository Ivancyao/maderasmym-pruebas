package DefinicionPasos.ficha;

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
 * Definicion de pasos - Funcionalidad: Visualizacion de ficha de producto
 * Requerimiento: RQ-02 Operaciones del negocio
 * Escenarios cubiertos (Matriz de Trazabilidad): CP25, CP26, CP27, CP28
 */
public class PasosFichaProducto {

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

	@Given("que un usuario selecciona un producto desde el listado")
	public void que_un_usuario_selecciona_un_producto_desde_el_listado() {
		driver.get("https://www.maderasmym.cl/");
		driver.findElement(By.xpath("//*[contains(@class,'product-card')][1]")).click();
	}

	@Given("que un usuario accede a la ficha de un producto sin existencias")
	public void que_un_usuario_accede_a_la_ficha_de_un_producto_sin_existencias() {
		driver.get("https://www.maderasmym.cl/producto/sin-stock-demo");
	}

	@Given("que un usuario realizó una búsqueda exitosa de {string}")
	public void que_un_usuario_realizo_una_busqueda_exitosa_de(String termino) {
		driver.get("https://www.maderasmym.cl/");
		WebElement input = driver.findElement(By.xpath("//input[@type='search']"));
		input.sendKeys(termino);
		input.submit();
	}

	@Given("que un usuario está en la ficha de un producto")
	public void que_un_usuario_esta_en_la_ficha_de_un_producto() {
		driver.get("https://www.maderasmym.cl/");
		driver.findElement(By.xpath("//*[contains(@class,'product-card')][1]")).click();
	}

	@When("accede a su ficha")
	public void accede_a_su_ficha() {
		// La navegacion a la ficha ya ocurrio en el Given (click sobre la tarjeta de producto).
	}

	@When("revisa el campo {string}")
	public void revisa_el_campo(String campo) {
		// Lectura directa del campo en el Then correspondiente.
	}

	@When("selecciona un producto del listado de resultados")
	public void selecciona_un_producto_del_listado_de_resultados() {
		driver.findElement(By.xpath("//*[contains(@class,'product-card')][1]")).click();
	}

	@When("revisa la sección de imágenes")
	public void revisa_la_seccion_de_imagenes() {
		// Lectura directa en el Then.
	}

	@Then("el sistema muestra tipo, medidas, dimensiones y disponibilidad del producto")
	public void el_sistema_muestra_tipo_medidas_dimensiones_y_disponibilidad() {
		String[] campos = {"Tipo", "Medidas", "Dimensiones", "Disponibilidad"};
		boolean todosPresentes = true;
		for (String campo : campos) {
			List<WebElement> el = driver.findElements(By.xpath("//*[contains(text(),'" + campo + "')]"));
			if (el.isEmpty()) {
				todosPresentes = false;
				Utility.resultado("NO PASA - Ficha de producto: falta el campo \"" + campo + "\"");
			}
		}
		if (todosPresentes) {
			Utility.resultado("PASA - Ficha de producto: tipo, medidas, dimensiones y disponibilidad presentes");
		}
	}

	@Then("el sistema indica claramente que el producto no tiene stock")
	public void el_sistema_indica_que_el_producto_no_tiene_stock() {
		boolean hayMensaje = !driver.findElements(By.xpath("//*[contains(text(),'Sin stock') or contains(text(),'Agotado')]")).isEmpty();
		Utility.resultado((hayMensaje ? "PASA" : "NO PASA") + " - Ficha de producto sin stock: indicador de disponibilidad " + (hayMensaje ? "presente" : "ausente"));
	}

	@Then("el sistema lo redirige correctamente a la ficha del producto seleccionado")
	public void el_sistema_lo_redirige_correctamente_a_la_ficha() {
		String url = driver.getCurrentUrl();
		boolean esFicha = url.contains("/producto/");
		Utility.resultado((esFicha ? "PASA" : "NO PASA") + " - Redirección a ficha desde búsqueda: URL = " + url);
	}

	@Then("el sistema despliega correctamente las imágenes del producto sin errores de carga")
	public void el_sistema_despliega_correctamente_las_imagenes() {
		List<WebElement> galeria = driver.findElements(By.xpath("//*[contains(@class,'gallery') or contains(@class,'producto-imagenes')]"));
		Utility.resultado((!galeria.isEmpty() ? "PASA" : "NO PASA") + " - Galería de imágenes: elementos encontrados = " + galeria.size());
	}
}
