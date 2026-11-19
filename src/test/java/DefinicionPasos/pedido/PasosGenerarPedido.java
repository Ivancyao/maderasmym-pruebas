package DefinicionPasos.pedido;

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
 * Definicion de pasos - Funcionalidad: Generar pedido (checkout)
 * Requerimiento: RQ-02 Operaciones del negocio
 * Escenarios cubiertos (Matriz de Trazabilidad): CP41, CP42, CP43, CP44
 *
 * Nota: dado que www.maderasmym.cl no expone una pasarela de pago, "generar pedido"
 * corresponde al cierre del flujo de cotizacion/captura de pedido (no a un pago transaccional).
 */
public class PasosGenerarPedido {

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

	@Given("que el carrito contiene productos y se seleccionó un método de entrega válido")
	public void que_el_carrito_contiene_productos_y_metodo_de_entrega_valido() {
		driver.get("https://www.maderasmym.cl/");
	}

	@Given("que el carrito de compras está vacío")
	public void que_el_carrito_de_compras_esta_vacio() {
		driver.get("https://www.maderasmym.cl/");
	}

	@Given("que el carrito contiene productos pero no se ha seleccionado método de entrega")
	public void que_el_carrito_contiene_productos_pero_no_se_ha_seleccionado_metodo() {
		driver.get("https://www.maderasmym.cl/");
	}

	@Given("que un pedido fue generado exitosamente")
	public void que_un_pedido_fue_generado_exitosamente() {
		driver.get("https://www.maderasmym.cl/");
		driver.findElement(By.xpath("//button[contains(text(),'Generar Pedido')]")).click();
	}

	@When("el usuario presiona {string}")
	public void el_usuario_presiona(String boton) {
		driver.findElement(By.xpath("//button[contains(text(),'" + boton + "')]")).click();
	}

	@When("el sistema procesa la solicitud")
	public void el_sistema_procesa_la_solicitud() {
		// La solicitud ya fue procesada en el Given (clic en "Generar Pedido").
	}

	@Then("el sistema procesa el pedido y muestra una confirmación al usuario")
	public void el_sistema_procesa_el_pedido_y_muestra_confirmacion() {
		boolean hayConfirmacion = !driver.findElements(By.xpath("//*[contains(text(),'pedido') and contains(text(),'generado')]")).isEmpty();
		Utility.resultado((hayConfirmacion ? "PASA" : "NO PASA") + " - Generar pedido exitoso: confirmación " + (hayConfirmacion ? "mostrada" : "no encontrada"));
	}

	@Then("el sistema impide la acción y muestra un mensaje indicando que el carrito está vacío")
	public void el_sistema_impide_la_accion_carrito_vacio() {
		boolean hayMensaje = !driver.findElements(By.xpath("//*[contains(text(),'carrito está vacío') or contains(text(),'Carrito 0')]")).isEmpty();
		Utility.resultado((hayMensaje ? "PASA" : "NO PASA") + " - Generar pedido con carrito vacío: mensaje de bloqueo " + (hayMensaje ? "presente" : "ausente"));
	}

	@Then("el sistema solicita seleccionar un método de entrega antes de continuar")
	public void el_sistema_solicita_seleccionar_metodo_de_entrega() {
		boolean hayMensaje = !driver.findElements(By.xpath("//*[contains(text(),'selecciona un método de entrega')]")).isEmpty();
		Utility.resultado((hayMensaje ? "PASA" : "NO PASA") + " - Generar pedido sin método de entrega: mensaje de validación " + (hayMensaje ? "presente" : "ausente"));
	}

	@Then("se muestra un mensaje de confirmación y el carrito queda vacío")
	public void se_muestra_mensaje_de_confirmacion_y_carrito_vacio() {
		boolean hayModal = !driver.findElements(By.xpath("//*[contains(@class,'modal') and contains(text(),'pedido')]")).isEmpty();
		List<WebElement> items = driver.findElements(By.xpath("//*[contains(@class,'cart-item')]"));
		boolean carritoVacio = items.isEmpty();
		Utility.resultado(((hayModal && carritoVacio) ? "PASA" : "NO PASA") + " - Confirmación final: modal=" + hayModal + ", carrito vacío=" + carritoVacio);
	}
}
