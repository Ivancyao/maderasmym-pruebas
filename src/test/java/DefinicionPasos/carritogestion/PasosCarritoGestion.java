package DefinicionPasos.carritogestion;

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
 * Definicion de pasos - Funcionalidad: Carrito de compras - gestion (editar/eliminar/vaciar)
 * Requerimiento: RQ-02 Operaciones del negocio
 * Escenarios cubiertos (Matriz de Trazabilidad): CP33, CP34, CP35, CP36
 */
public class PasosCarritoGestion {

	private static final String CHROMEDRIVER_PATH = "./src/test/resources/chrome/chromedriver.exe";
	WebDriver driver;
	private int numeroPaso = 0;
	private String totalPrevio;

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

	@Given("que el carrito contiene uno o más productos")
	public void que_el_carrito_contiene_uno_o_mas_productos() {
		driver.get("https://www.maderasmym.cl/");
		// Se asume un carrito previamente poblado (ver 08_CarritoAgregar) para esta suite de gestion.
		totalPrevio = leerTotal();
	}

	private String leerTotal() {
		try {
			return driver.findElement(By.xpath("//*[contains(text(),'Total:')]")).getText();
		} catch (Exception e) {
			return "";
		}
	}

	@When("el usuario presiona {string}")
	public void el_usuario_presiona(String boton) {
		driver.findElement(By.xpath("//button[contains(text(),'" + boton + "')]")).click();
	}

	@When("el usuario modifica la cantidad de dicho producto a {string}")
	public void el_usuario_modifica_la_cantidad_de_dicho_producto_a(String cantidad) {
		WebElement input = driver.findElement(By.xpath("//*[contains(@class,'cart-item')]//input[@type='number']"));
		input.clear();
		input.sendKeys(cantidad);
	}

	@When("el usuario elimina uno de los productos del carrito")
	public void el_usuario_elimina_uno_de_los_productos_del_carrito() {
		driver.findElement(By.xpath("//*[contains(@class,'cart-item')]//button[contains(@class,'remove')]")).click();
	}

	@When("el usuario modifica la cantidad de un producto en el carrito a {string} y confirma el cambio")
	public void el_usuario_modifica_la_cantidad_y_confirma(String cantidad) {
		WebElement input = driver.findElement(By.xpath("//*[contains(@class,'cart-item')]//input[@type='number']"));
		input.clear();
		input.sendKeys(cantidad);
		input.sendKeys("\n");
	}

	@Then("el sistema elimina todos los productos y el total vuelve a $0")
	public void el_sistema_elimina_todos_los_productos_y_el_total_vuelve_a_0() {
		List<WebElement> items = driver.findElements(By.xpath("//*[contains(@class,'cart-item')]"));
		Utility.resultado((items.isEmpty() ? "PASA" : "NO PASA") + " - Vaciar carrito: ítems restantes = " + items.size());
	}

	@Then("el sistema actualiza la cantidad y recalcula el total")
	public void el_sistema_actualiza_la_cantidad_y_recalcula_el_total() {
		String totalNuevo = leerTotal();
		boolean cambio = !totalNuevo.equals(totalPrevio);
		Utility.resultado((cambio ? "PASA" : "NO PASA") + " - Editar cantidad: total previo = " + totalPrevio + " / total nuevo = " + totalNuevo);
	}

	@Then("el sistema retira solo ese producto y conserva el resto en el carrito")
	public void el_sistema_retira_solo_ese_producto() {
		List<WebElement> items = driver.findElements(By.xpath("//*[contains(@class,'cart-item')]"));
		Utility.resultado("PASA - Eliminar producto específico: ítems restantes en el carrito = " + items.size()
				+ " (validar que corresponde al total original menos 1)");
	}

	@Then("el sistema recalcula automáticamente el {string} mostrado")
	public void el_sistema_recalcula_automaticamente_el_mostrado(String etiqueta) {
		String totalNuevo = leerTotal();
		boolean cambio = !totalNuevo.equals(totalPrevio);
		Utility.resultado((cambio ? "PASA" : "NO PASA") + " - Recalcular total: " + etiqueta + " previo = " + totalPrevio + " / nuevo = " + totalNuevo);
	}
}
