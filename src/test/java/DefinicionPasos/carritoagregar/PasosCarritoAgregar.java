package DefinicionPasos.carritoagregar;

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
 * Definicion de pasos - Funcionalidad: Carrito de compras - agregar producto
 * Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
 * Escenarios cubiertos (Matriz de Trazabilidad): CP29, CP30, CP31, CP32
 *
 * Los localizadores y los mensajes esperados fueron verificados contra el HTML y el comportamiento
 * REAL de www.maderasmym.cl (carpetas "explorar" y "explorar2" del repositorio). Los resultados
 * se registran con Utility.resultado(...) (PASA / NO PASA) o Utility.noEjecutado(...) cuando el
 * escenario no se puede ejecutar de forma responsable (ver el motivo en cada caso).
 */
public class PasosCarritoAgregar {

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


	private long stockDelProducto = 0;
	private long cantidadFinal = 0;
	private String idProducto = "";

	@Given("que un usuario está en la ficha o listado de un producto disponible")
	public void que_un_usuario_esta_en_la_ficha_o_listado_de_un_producto_disponible() {
		Sitio.abrirInicio(driver);
		WebElement primera = driver.findElement(By.cssSelector("#catalog-grid .product-card"));
		stockDelProducto = Sitio.numero(primera.findElement(By.cssSelector(".meta div:nth-child(4)")).getText());
		idProducto = primera.findElement(By.cssSelector(".actions")).getAttribute("data-product-id");
	}

	private WebElement botonMas() {
		return driver.findElement(By.cssSelector("#catalog-grid .actions[data-product-id='" + idProducto + "'] .qty-btn-plus"));
	}

	private long cantidadEnTarjeta() {
		List<WebElement> d = driver.findElements(By.cssSelector("#catalog-grid .actions[data-product-id='" + idProducto + "'] .qty-display"));
		return d.isEmpty() ? 0 : Sitio.numero(d.get(0).getText().replace("en carrito", ""));
	}

	/** Agrega el producto y pulsa "+" hasta alcanzar la cantidad pedida (el sitio no tiene campo de cantidad). */
	private void agregarCantidad(long cantidad) {
		Sitio.ultimaAlerta = null;
		WebElement agregar = driver.findElement(By.cssSelector("#catalog-grid .actions[data-product-id='" + idProducto + "'] .add-btn"));
		Sitio.clic(driver, agregar);
		Sitio.pausa(500);
		long clics = 0;
		while (cantidadEnTarjeta() < cantidad && clics < cantidad + 2 && Sitio.ultimaAlerta == null) {
			Sitio.clicJs(driver, botonMas());
			Sitio.pausa(120);
			clics++;
		}
		cantidadFinal = cantidadEnTarjeta();
	}

	@When("indica la cantidad {string} y presiona {string}")
	public void indica_la_cantidad_y_presiona(String cantidad, String boton) {
		long pedida = Long.parseLong(cantidad);
		if (pedida > 500) {
			// El sitio no tiene un campo numerico: se prueba el limite real pulsando "+" hasta superar el stock.
			pedida = stockDelProducto + 1;
		}
		agregarCantidad(pedida);
	}

	@Then("el sistema responde de forma {string}")
	public void el_sistema_responde_de_forma(String comportamiento) {
		if (comportamiento.startsWith("agregado")) {
			int contador = Sitio.contadorCarrito(driver);
			if (contador == 2) {
				Utility.resultado("PASA - Se agregaron 2 unidades y el contador del carrito muestra " + contador);
			} else {
				Utility.resultado("NO PASA - Se esperaba contador = 2 y el sitio muestra " + contador);
			}
		} else {
			if (cantidadFinal > stockDelProducto) {
				Utility.resultado("NO PASA - El sitio permitio llevar " + cantidadFinal + " unidades de un producto con stock "
						+ stockDelProducto + " (no limita la cantidad al stock)");
			} else {
				Utility.resultado("PASA - El sitio no permite superar el stock (" + stockDelProducto + "); cantidad maxima = "
						+ cantidadFinal + (Sitio.ultimaAlerta != null ? ", aviso: " + Sitio.ultimaAlerta : ""));
			}
		}
	}

	// ---- CP31
	@Given("que el carrito se encuentra vacío")
	public void que_el_carrito_se_encuentra_vacio() {
		if (Sitio.contadorCarrito(driver) != 0) {
			Utility.resultado("NO PASA - El carrito no esta vacio al iniciar (contador = " + Sitio.contadorCarrito(driver) + ")");
		}
	}

	@When("el usuario agrega un producto con cantidad {string}")
	public void el_usuario_agrega_un_producto_con_cantidad(String cantidad) {
		agregarCantidad(Long.parseLong(cantidad));
	}

	@Then("el contador del carrito se actualiza a la cantidad de ítems agregados")
	public void el_contador_del_carrito_se_actualiza() {
		int contador = Sitio.contadorCarrito(driver);
		if (contador == 1) {
			Utility.resultado("PASA - El contador del carrito pasa de 0 a " + contador);
		} else {
			Utility.resultado("NO PASA - Se agrego 1 item y el contador muestra " + contador);
		}
	}

	// ---- CP32
	@Given("que un producto ya se encuentra en el carrito")
	public void que_un_producto_ya_se_encuentra_en_el_carrito() {
		agregarCantidad(1);
	}

	@When("el usuario lo agrega nuevamente con cantidad {string}")
	public void el_usuario_lo_agrega_nuevamente_con_cantidad(String cantidad) {
		long meta = cantidadEnTarjeta() + Long.parseLong(cantidad);
		Sitio.clicJs(driver, botonMas());
		Sitio.pausa(500);
		cantidadFinal = cantidadEnTarjeta();
		if (cantidadFinal != meta) {
			Sitio.pausa(500);
		}
	}

	@Then("el sistema acumula la cantidad en la misma línea de producto, sin duplicarlo")
	public void el_sistema_acumula_la_cantidad_en_la_misma_linea() {
		Sitio.abrirCarrito(driver);
		int lineas = Sitio.lineasCarrito(driver);
		long cantidad = lineas > 0 ? Sitio.cantidadDeLinea(driver, 0) : 0;
		if (lineas == 1 && cantidad == 2) {
			Utility.resultado("PASA - El carrito tiene 1 linea con cantidad 2 (acumulada, sin duplicar)");
		} else {
			Utility.resultado("NO PASA - Lineas en el carrito = " + lineas + ", cantidad de la primera linea = " + cantidad);
		}
	}
}
