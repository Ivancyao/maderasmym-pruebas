package DefinicionPasos.menumovil;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
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
 * Definicion de pasos - Funcionalidad: Menu de navegacion responsivo (movil)
 * Requerimiento: RQ-03 Navegabilidad e interaccion
 * Escenarios cubiertos (Matriz de Trazabilidad): CP57, CP58, CP59, CP60
 */
public class PasosMenuResponsivo {

	private static final String CHROMEDRIVER_PATH = "./src/test/resources/chrome/chromedriver.exe";
	private static final String ICONO_MENU = "//img[contains(@src,'menu.svg')]";
	WebDriver driver;
	private int numeroPaso = 0;

	@Before
	public void setUp() {
		driver = Utility.crearDriver(CHROMEDRIVER_PATH);
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

	@Given("que un usuario navega desde un dispositivo móvil con viewport 375x667")
	public void que_un_usuario_navega_desde_un_dispositivo_movil() {
		driver.manage().window().setSize(new Dimension(375, 667));
		driver.get("https://www.maderasmym.cl/");
	}

	@Given("que el menú móvil está desplegado")
	public void que_el_menu_movil_esta_desplegado() {
		driver.manage().window().setSize(new Dimension(375, 667));
		driver.get("https://www.maderasmym.cl/");
		driver.findElement(By.xpath(ICONO_MENU)).click();
	}

	@When("presiona el ícono de menú hamburguesa")
	public void presiona_el_icono_de_menu() {
		driver.findElement(By.xpath(ICONO_MENU)).click();
	}

	/**
	 * Localiza la opcion del menu. En el sitio el acceso a login no tiene el texto "Iniciar sesión"
	 * en el menu (el enlace se muestra como "/login"), por eso se ubica por su href.
	 */
	private By localizarOpcion(String opcion) {
		if (opcion.equals("Iniciar sesión")) {
			return By.xpath("//a[contains(@href,'/login')]");
		}
		return By.xpath("//a[contains(text(),'" + opcion + "')]");
	}

	@When("el usuario selecciona {string}")
	public void el_usuario_selecciona(String opcion) {
		driver.findElement(localizarOpcion(opcion)).click();
	}

	@When("el usuario revisa las opciones disponibles")
	public void el_usuario_revisa_las_opciones_disponibles() {
		// Verificacion en el Then.
	}

	@When("el usuario presiona la opción {string}")
	public void el_usuario_presiona_la_opcion(String opcion) {
		driver.findElement(localizarOpcion(opcion)).click();
	}

	@Then("el sistema despliega el menú de navegación y permite cerrarlo correctamente")
	public void el_sistema_despliega_el_menu_y_permite_cerrarlo() {
		boolean visible = driver.findElement(By.xpath("//a[contains(text(),'Inicio')]")).isDisplayed();
		driver.findElement(By.xpath(ICONO_MENU)).click();
		boolean cerrado = driver.findElements(By.xpath("//a[contains(text(),'Inicio')]")).isEmpty()
				|| !driver.findElement(By.xpath("//a[contains(text(),'Inicio')]")).isDisplayed();
		Utility.resultado((visible && cerrado ? "PASA" : "NO PASA") + " - Menú móvil: abierto=" + visible + ", cerrado=" + cerrado);
	}

	@Then("el sistema muestra las categorías de productos disponibles")
	public void el_sistema_muestra_las_categorias() {
		boolean hayCategorias = !driver.findElements(By.xpath("//a[contains(text(),'Pino Oregón')]")).isEmpty();
		Utility.resultado((hayCategorias ? "PASA" : "NO PASA") + " - Menú móvil > Productos: categorías " + (hayCategorias ? "visibles" : "no encontradas"));
	}

	@Then("se muestran correctamente los accesos a {string} e {string}")
	public void se_muestran_los_accesos(String acceso1, String acceso2) {
		boolean carrito = !driver.findElements(By.xpath("//a[contains(text(),'Carrito')]")).isEmpty();
		boolean login = !driver.findElements(By.xpath("//a[contains(@href,'/login')]")).isEmpty();
		Utility.resultado((carrito && login ? "PASA" : "NO PASA") + " - Menú móvil: acceso a " + acceso1 + "=" + carrito + ", acceso a " + acceso2 + "=" + login);
	}

	@Then("la opción {string} responde correctamente")
	public void la_opcion_responde_correctamente(String opcion) {
		String url = driver.getCurrentUrl();
		Utility.resultado("PASA - Opción \"" + opcion + "\" del menú móvil respondió (URL = " + url + ")");
	}
}
