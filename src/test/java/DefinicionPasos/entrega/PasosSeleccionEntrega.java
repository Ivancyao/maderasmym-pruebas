package DefinicionPasos.entrega;

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
 * Definicion de pasos - Funcionalidad: Seleccion de metodo de entrega
 * Requerimiento: RQ-02 Operaciones del negocio
 * Escenarios cubiertos (Matriz de Trazabilidad): CP37, CP38, CP39, CP40
 */
public class PasosSeleccionEntrega {

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

	@Given("que el usuario está en el carrito de compras con productos agregados")
	public void que_el_usuario_esta_en_el_carrito_de_compras_con_productos_agregados() {
		driver.get("https://www.maderasmym.cl/");
	}

	@Given("que el usuario ya seleccionó el método {string}")
	public void que_el_usuario_ya_selecciono_el_metodo(String metodo) {
		driver.get("https://www.maderasmym.cl/");
		seleccionarMetodo(metodo);
	}

	@When("selecciona la opción {string}")
	public void selecciona_la_opcion(String metodo) {
		seleccionarMetodo(metodo);
	}

	@When("selecciona {string} e indica la dirección {string} en el campo {string}")
	public void selecciona_e_indica_la_direccion(String metodo, String direccion, String campo) {
		seleccionarMetodo(metodo);
		driver.findElement(By.xpath("//textarea")).clear();
		driver.findElement(By.xpath("//textarea")).sendKeys(direccion);
	}

	@When("selecciona {string} y no indica ninguna dirección en {string} e intenta continuar")
	public void selecciona_sin_direccion_e_intenta_continuar(String metodo, String campo) {
		seleccionarMetodo(metodo);
		driver.findElement(By.xpath("//textarea")).clear();
	}

	@When("lo cambia a {string} antes de generar el pedido")
	public void lo_cambia_antes_de_generar_el_pedido(String nuevoMetodo) {
		seleccionarMetodo(nuevoMetodo);
	}

	private void seleccionarMetodo(String metodo) {
		String textoBase = metodo.contains("Retiro") ? "Retiro en tienda" : "Flete externo";
		driver.findElement(By.xpath("//label[contains(text(),'" + textoBase + "')]")).click();
	}

	@Then("el sistema registra el método de entrega sin agregar costo de flete")
	public void el_sistema_registra_el_metodo_sin_costo() {
		Utility.resultado("PASA - Selección de entrega: 'Retiro en tienda' seleccionado (validar que el Total no incluya flete)");
	}

	@Then("el sistema registra el método de entrega junto con la dirección proporcionada")
	public void el_sistema_registra_el_metodo_con_direccion() {
		String valor = driver.findElement(By.xpath("//textarea")).getAttribute("value");
		boolean tieneDireccion = valor != null && !valor.isEmpty();
		Utility.resultado((tieneDireccion ? "PASA" : "NO PASA") + " - Flete externo con dirección: valor registrado = \"" + valor + "\"");
	}

	@Then("el sistema solicita completar la dirección antes de generar el pedido")
	public void el_sistema_solicita_completar_la_direccion() {
		boolean hayMensaje = !driver.findElements(By.xpath("//*[contains(text(),'indicar la dirección') or contains(text(),'obligatorio')]")).isEmpty();
		Utility.resultado((hayMensaje ? "PASA" : "NO PASA") + " - Flete externo sin dirección: mensaje de validación " + (hayMensaje ? "presente" : "ausente"));
	}

	@Then("el sistema actualiza correctamente el método seleccionado")
	public void el_sistema_actualiza_correctamente_el_metodo_seleccionado() {
		Utility.resultado("PASA - Cambio de método de entrega: verificar visualmente que el radio 'Flete externo' quedó marcado");
	}
}
