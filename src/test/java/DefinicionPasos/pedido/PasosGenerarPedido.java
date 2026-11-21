package DefinicionPasos.pedido;

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
 * Definicion de pasos - Funcionalidad: Generar pedido (checkout)
 * Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
 * Escenarios cubiertos (Matriz de Trazabilidad): CP41, CP42, CP43, CP44
 *
 * Los localizadores y los mensajes esperados fueron verificados contra el HTML y el comportamiento
 * REAL de www.maderasmym.cl (carpetas "explorar" y "explorar2" del repositorio). Los resultados
 * se registran con Utility.resultado(...) (PASA / NO PASA) o Utility.noEjecutado(...) cuando el
 * escenario no se puede ejecutar de forma responsable (ver el motivo en cada caso).
 */
public class PasosGenerarPedido {

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


	private String alerta = null;

	// ---- CP41: generar un pedido exitoso crea un pedido real y exige sesion iniciada
	@Given("que el carrito contiene productos y se seleccionó un método de entrega válido")
	public void que_el_carrito_contiene_productos_y_se_selecciono_un_metodo_valido() {
		Utility.noEjecutado("generar un pedido exitoso exige sesion iniciada (el sitio responde 401 y pide ir al login) y "
				+ "crearia un pedido real en el sitio de la empresa");
	}

	// ---- CP42: carrito vacio
	@Given("que el carrito de compras está vacío")
	public void que_el_carrito_de_compras_esta_vacio() {
		Sitio.abrirInicio(driver);
		if (Sitio.contadorCarrito(driver) != 0) {
			Utility.resultado("NO PASA - El carrito no esta vacio al iniciar");
		}
	}

	@When("el usuario presiona {string}")
	public void el_usuario_presiona(String boton) {
		Sitio.abrirCarrito(driver);
		Sitio.ultimaAlerta = null;
		Sitio.clicJs(driver, driver.findElement(By.id("generateOrder")));
		alerta = Sitio.ultimaAlerta != null ? Sitio.ultimaAlerta : Sitio.leerAlerta(driver, 5);
	}

	@Then("el sistema impide la acción y muestra un mensaje indicando que el carrito está vacío")
	public void el_sistema_impide_la_accion_y_muestra_un_mensaje() {
		if (alerta != null && alerta.toLowerCase().contains("vac")) {
			Utility.resultado("PASA - Con el carrito vacio el sitio muestra: \"" + alerta + "\"");
		} else {
			Utility.resultado("NO PASA - Con el carrito vacio no se mostro el aviso esperado (aviso obtenido: " + alerta + ")");
		}
	}

	// ---- CP43: el sitio deja "Flete externo" preseleccionado
	@Given("que el carrito contiene productos pero no se ha seleccionado método de entrega")
	public void que_el_carrito_contiene_productos_pero_no_se_ha_seleccionado_metodo() {
		Sitio.abrirInicio(driver);
		Sitio.agregarProductos(driver, 1);
		Sitio.abrirCarrito(driver);
		boolean preseleccionado = driver.findElement(By.id("delivery-shipping")).isSelected()
				|| driver.findElement(By.id("delivery-pickup")).isSelected();
		if (preseleccionado) {
			Utility.noEjecutado("la precondicion no puede darse: el carrito trae \"Flete externo\" preseleccionado y la interfaz "
					+ "no permite dejar el metodo de entrega sin elegir");
		}
	}

	@Then("el sistema solicita seleccionar un método de entrega antes de continuar")
	public void el_sistema_solicita_seleccionar_un_metodo_de_entrega() {
		Utility.noEjecutado("verificacion no realizada (ver el paso anterior)");
	}

	// ---- CP44: confirmacion tras generar pedido
	@Given("que un pedido fue generado exitosamente")
	public void que_un_pedido_fue_generado_exitosamente() {
		Utility.noEjecutado("requiere generar un pedido real con una sesion iniciada, lo que no se hace en el sitio de la empresa");
	}

	@When("el sistema procesa la solicitud")
	public void el_sistema_procesa_la_solicitud() {
		Sitio.pausa(500);
	}

	@Then("se muestra un mensaje de confirmación y el carrito queda vacío")
	public void se_muestra_un_mensaje_de_confirmacion_y_el_carrito_queda_vacio() {
		Utility.noEjecutado("verificacion no realizada (ver el paso anterior)");
	}

	@Then("el sistema procesa el pedido y muestra una confirmación al usuario")
	public void el_sistema_procesa_el_pedido_y_muestra_una_confirmacion() {
		Utility.noEjecutado("verificacion no realizada (ver el paso anterior)");
	}
}
