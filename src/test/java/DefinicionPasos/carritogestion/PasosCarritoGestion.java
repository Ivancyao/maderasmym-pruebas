package DefinicionPasos.carritogestion;

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
 * Definicion de pasos - Funcionalidad: Carrito de compras - gestion (editar/eliminar/vaciar)
 * Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
 * Escenarios cubiertos (Matriz de Trazabilidad): CP33, CP34, CP35, CP36
 *
 * Los localizadores y los mensajes esperados fueron verificados contra el HTML y el comportamiento
 * REAL de www.maderasmym.cl (carpetas "explorar" y "explorar2" del repositorio). Los resultados
 * se registran con Utility.resultado(...) (PASA / NO PASA) o Utility.noEjecutado(...) cuando el
 * escenario no se puede ejecutar de forma responsable (ver el motivo en cada caso).
 */
public class PasosCarritoGestion {

	private static final String CHROMEDRIVER_PATH = "./src/test/resources/chrome/chromedriver.exe";
	WebDriver driver;
	private int numeroPaso = 0;

	@Before
	public void setUp() {
		driver = Utility.crearDriver(CHROMEDRIVER_PATH);
		driver.manage().window().maximize();
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


	private String nombreEliminado = "";

	@Given("que el carrito contiene uno o más productos")
	public void que_el_carrito_contiene_uno_o_mas_productos() {
		Sitio.abrirInicio(driver);
		Sitio.agregarProductos(driver, 2);
		Sitio.abrirCarrito(driver);
	}

	@When("el usuario presiona {string}")
	public void el_usuario_presiona(String boton) {
		Sitio.clic(driver, driver.findElement(By.id("clear-cart-btn")));
		Sitio.pausa(800);
	}

	@Then("el sistema elimina todos los productos y el total vuelve a $0")
	public void el_sistema_elimina_todos_los_productos() {
		int contador = Sitio.contadorCarrito(driver);
		long total = Sitio.totalCarrito(driver);
		int lineas = Sitio.lineasCarrito(driver);
		if (contador == 0 && total == 0 && lineas == 0) {
			Utility.resultado("PASA - Carrito vaciado: contador = 0, total = $0, sin lineas");
		} else {
			Utility.resultado("NO PASA - Tras vaciar: contador = " + contador + ", total = " + total + ", lineas = " + lineas);
		}
	}

	@When("el usuario modifica la cantidad de dicho producto a {string}")
	public void el_usuario_modifica_la_cantidad_de_dicho_producto_a(String cantidad) {
		Sitio.ponerCantidadEnCarrito(driver, 0, Integer.parseInt(cantidad));
	}

	@Then("el sistema actualiza la cantidad y recalcula el total")
	public void el_sistema_actualiza_la_cantidad_y_recalcula_el_total() {
		long cantidad = Sitio.cantidadDeLinea(driver, 0);
		long total = Sitio.totalCarrito(driver);
		long esperado = Sitio.totalCalculado(driver);
		if (cantidad == 5 && total == esperado) {
			Utility.resultado("PASA - Cantidad = " + cantidad + " y total = $" + total + " (coincide con la suma de las lineas)");
		} else {
			Utility.resultado("NO PASA - Cantidad = " + cantidad + ", total mostrado = " + total + ", total esperado = " + esperado);
		}
	}

	@When("el usuario elimina uno de los productos del carrito")
	public void el_usuario_elimina_uno_de_los_productos_del_carrito() {
		List<WebElement> lineas = driver.findElements(By.cssSelector("#cartModal .cart-item"));
		nombreEliminado = lineas.get(0).findElement(By.cssSelector("h4")).getText();
		Sitio.clic(driver, lineas.get(0).findElement(By.cssSelector(".remove-btn")));
		Sitio.pausa(800);
	}

	@Then("el sistema retira solo ese producto y conserva el resto en el carrito")
	public void el_sistema_retira_solo_ese_producto() {
		List<WebElement> lineas = driver.findElements(By.cssSelector("#cartModal .cart-item"));
		boolean sigue = false;
		for (WebElement l : lineas) {
			if (l.findElement(By.cssSelector("h4")).getText().equals(nombreEliminado)) {
				sigue = true;
			}
		}
		if (lineas.size() == 1 && !sigue) {
			Utility.resultado("PASA - Se elimino \"" + nombreEliminado + "\" y quedo 1 producto en el carrito");
		} else {
			Utility.resultado("NO PASA - Lineas restantes = " + lineas.size() + ", el producto eliminado sigue = " + sigue);
		}
	}

	@When("el usuario modifica la cantidad de un producto en el carrito a {string} y confirma el cambio")
	public void el_usuario_modifica_la_cantidad_de_un_producto_a_y_confirma(String cantidad) {
		Sitio.ponerCantidadEnCarrito(driver, 0, Integer.parseInt(cantidad));
	}

	@Then("el sistema recalcula automáticamente el {string} mostrado")
	public void el_sistema_recalcula_automaticamente_el_mostrado(String campo) {
		long total = Sitio.totalCarrito(driver);
		long esperado = Sitio.totalCalculado(driver);
		long cantidad = Sitio.cantidadDeLinea(driver, 0);
		if (cantidad == 3 && total == esperado) {
			Utility.resultado("PASA - Con cantidad " + cantidad + " el total mostrado ($" + total + ") coincide con el calculo");
		} else {
			Utility.resultado("NO PASA - Cantidad = " + cantidad + ", total mostrado = " + total + ", total esperado = " + esperado);
		}
	}
}
