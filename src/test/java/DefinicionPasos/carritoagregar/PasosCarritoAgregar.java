package DefinicionPasos.carritoagregar;

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
 * Definicion de pasos - Funcionalidad: Carrito de compras - agregar producto
 * Requerimiento: RQ-02 Operaciones del negocio
 * Escenarios cubiertos (Matriz de Trazabilidad): CP29, CP30, CP31, CP32
 */
public class PasosCarritoAgregar {

	private static final String CHROMEDRIVER_PATH = "./src/test/resources/chrome/chromedriver.exe";
	WebDriver driver;
	private int numeroPaso = 0;
	private int contadorCarritoPrevio;

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

	@Given("que un usuario está en la ficha o listado de un producto disponible")
	public void que_un_usuario_esta_en_la_ficha_o_listado_de_un_producto_disponible() {
		driver.get("https://www.maderasmym.cl/");
	}

	@Given("que el carrito se encuentra vacío")
	public void que_el_carrito_se_encuentra_vacio() {
		driver.get("https://www.maderasmym.cl/");
		contadorCarritoPrevio = 0;
	}

	@Given("que un producto ya se encuentra en el carrito")
	public void que_un_producto_ya_se_encuentra_en_el_carrito() {
		driver.get("https://www.maderasmym.cl/");
		agregarCantidad("1");
	}

	@When("indica la cantidad {string} y presiona {string}")
	public void indica_la_cantidad_y_presiona(String cantidad, String boton) {
		agregarCantidad(cantidad);
	}

	@When("el usuario agrega un producto con cantidad {string}")
	public void el_usuario_agrega_un_producto_con_cantidad(String cantidad) {
		agregarCantidad(cantidad);
	}

	@When("el usuario lo agrega nuevamente con cantidad {string}")
	public void el_usuario_lo_agrega_nuevamente_con_cantidad(String cantidad) {
		agregarCantidad(cantidad);
	}

	private void agregarCantidad(String cantidad) {
		WebElement inputCantidad = driver.findElement(By.xpath("//input[@type='number']"));
		inputCantidad.clear();
		inputCantidad.sendKeys(cantidad);
		driver.findElement(By.xpath("//button[contains(text(),'Agregar')]")).click();
	}

	private int leerContadorCarrito() {
		try {
			String texto = driver.findElement(By.xpath("//a[contains(text(),'Carrito')]")).getText();
			String soloNumeros = texto.replaceAll("[^0-9]", "");
			return soloNumeros.isEmpty() ? 0 : Integer.parseInt(soloNumeros);
		} catch (Exception e) {
			return -1;
		}
	}

	@Then("el sistema responde de forma {string}")
	public void el_sistema_responde_de_forma(String comportamientoEsperado) {
		switch (comportamientoEsperado) {
			case "agregado y contador actualizado":
				int contador = leerContadorCarrito();
				Utility.resultado((contador > 0 ? "PASA" : "NO PASA") + " - Agregar al carrito: contador = " + contador);
				break;
			case "error de stock insuficiente":
				boolean hayError = !driver.findElements(By.xpath("//*[contains(text(),'stock disponible') or contains(text(),'excede')]")).isEmpty();
				Utility.resultado((hayError ? "PASA" : "NO PASA") + " - Cantidad superior al stock: mensaje de error " + (hayError ? "presente" : "ausente"));
				break;
			default:
				Utility.resultado("NO PASA - comportamiento esperado no reconocido: " + comportamientoEsperado);
		}
	}

	@Then("el contador del carrito se actualiza a la cantidad de ítems agregados")
	public void el_contador_del_carrito_se_actualiza() {
		int contador = leerContadorCarrito();
		Utility.resultado((contador == 1 ? "PASA" : "NO PASA") + " - Actualización de contador: valor obtenido = " + contador);
	}

	@Then("el sistema acumula la cantidad en la misma línea de producto, sin duplicarlo")
	public void el_sistema_acumula_la_cantidad_sin_duplicar() {
		List<WebElement> lineas = driver.findElements(By.xpath("//*[contains(@class,'cart-item')]"));
		Utility.resultado((lineas.size() <= 1 ? "PASA" : "NO PASA") + " - Agregar producto repetido: líneas de carrito encontradas = " + lineas.size()
				+ " (se espera 1 línea con cantidad acumulada, no 2 líneas duplicadas)");
	}
}
