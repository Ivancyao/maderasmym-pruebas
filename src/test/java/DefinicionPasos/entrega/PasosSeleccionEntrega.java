package DefinicionPasos.entrega;

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
 * Definicion de pasos - Funcionalidad: Seleccion de metodo de entrega
 * Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
 * Escenarios cubiertos (Matriz de Trazabilidad): CP37, CP38, CP39, CP40
 *
 * Los localizadores y los mensajes esperados fueron verificados contra el HTML y el comportamiento
 * REAL de www.maderasmym.cl (carpetas "explorar" y "explorar2" del repositorio). Los resultados
 * se registran con Utility.resultado(...) (PASA / NO PASA) o Utility.noEjecutado(...) cuando el
 * escenario no se puede ejecutar de forma responsable (ver el motivo en cada caso).
 */
public class PasosSeleccionEntrega {

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


	private long totalAntes = 0;
	private String direccion = "";

	private void elegir(String opcion) {
		String id = opcion.toLowerCase().contains("retiro") ? "delivery-pickup" : "delivery-shipping";
		Sitio.clic(driver, driver.findElement(By.cssSelector("label[for='" + id + "']")));
		Sitio.pausa(500);
	}

	private boolean marcado(String id) {
		return driver.findElement(By.id(id)).isSelected();
	}

	@Given("que el usuario está en el carrito de compras con productos agregados")
	public void que_el_usuario_esta_en_el_carrito_con_productos() {
		Sitio.abrirInicio(driver);
		Sitio.agregarProductos(driver, 1);
		Sitio.abrirCarrito(driver);
		totalAntes = Sitio.totalCarrito(driver);
	}

	@When("selecciona la opción {string}")
	public void selecciona_la_opcion(String opcion) {
		elegir(opcion);
	}

	@Then("el sistema registra el método de entrega sin agregar costo de flete")
	public void el_sistema_registra_el_metodo_sin_costo_de_flete() {
		long totalDespues = Sitio.totalCarrito(driver);
		if (marcado("delivery-pickup") && totalDespues == totalAntes) {
			Utility.resultado("PASA - Retiro en tienda seleccionado y el total no cambia ($" + totalDespues + ")");
		} else {
			Utility.resultado("NO PASA - Retiro seleccionado = " + marcado("delivery-pickup") + ", total antes = " + totalAntes
					+ ", total despues = " + totalDespues);
		}
	}

	@When("selecciona {string} e indica la dirección {string} en el campo {string}")
	public void selecciona_e_indica_la_direccion(String opcion, String dir, String campo) {
		elegir(opcion);
		direccion = dir;
		WebElement comentarios = driver.findElement(By.id("order-comments"));
		comentarios.clear();
		comentarios.sendKeys(dir);
	}

	@Then("el sistema registra el método de entrega junto con la dirección proporcionada")
	public void el_sistema_registra_el_metodo_junto_con_la_direccion() {
		String valor = driver.findElement(By.id("order-comments")).getAttribute("value");
		if (marcado("delivery-shipping") && direccion.equals(valor)) {
			Utility.resultado("PASA - Flete externo seleccionado y la direccion queda en Comentarios: \"" + valor + "\"");
		} else {
			Utility.resultado("NO PASA - Flete seleccionado = " + marcado("delivery-shipping") + ", comentarios = \"" + valor + "\"");
		}
	}

	// ---- CP39: validar la direccion exige pulsar "Generar pedido" (requiere sesion y crea un pedido real)
	@When("selecciona {string} y no indica ninguna dirección en {string} e intenta continuar")
	public void selecciona_y_no_indica_ninguna_direccion(String opcion, String campo) {
		elegir(opcion);
		Utility.noEjecutado("comprobar que se exige la direccion obliga a pulsar \"Generar pedido\", accion que requiere sesion "
				+ "iniciada y generaria un pedido real en el sitio de la empresa");
	}

	@Then("el sistema solicita completar la dirección antes de generar el pedido")
	public void el_sistema_solicita_completar_la_direccion() {
		Utility.noEjecutado("verificacion no realizada (ver el paso anterior)");
	}

	// ---- CP40
	@Given("que el usuario ya seleccionó el método {string}")
	public void que_el_usuario_ya_selecciono_el_metodo(String opcion) {
		Sitio.abrirInicio(driver);
		Sitio.agregarProductos(driver, 1);
		Sitio.abrirCarrito(driver);
		elegir(opcion);
	}

	@When("lo cambia a {string} antes de generar el pedido")
	public void lo_cambia_a_antes_de_generar_el_pedido(String opcion) {
		elegir(opcion);
	}

	@Then("el sistema actualiza correctamente el método seleccionado")
	public void el_sistema_actualiza_correctamente_el_metodo_seleccionado() {
		if (marcado("delivery-shipping") && !marcado("delivery-pickup")) {
			Utility.resultado("PASA - El metodo cambio de \"Retiro en tienda\" a \"Flete externo\"");
		} else {
			Utility.resultado("NO PASA - Flete = " + marcado("delivery-shipping") + ", retiro = " + marcado("delivery-pickup"));
		}
	}
}
