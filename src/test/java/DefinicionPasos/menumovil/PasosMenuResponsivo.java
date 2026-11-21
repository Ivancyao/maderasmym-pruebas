package DefinicionPasos.menumovil;

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
 * Definicion de pasos - Funcionalidad: Menu de navegacion responsivo (movil)
 * Requerimiento: RQ-03 Navegabilidad e interaccion
 * Escenarios cubiertos (Matriz de Trazabilidad): CP57, CP58, CP59, CP60
 *
 * Los localizadores y los mensajes esperados fueron verificados contra el HTML y el comportamiento
 * REAL de www.maderasmym.cl (carpetas "explorar" y "explorar2" del repositorio). Los resultados
 * se registran con Utility.resultado(...) (PASA / NO PASA) o Utility.noEjecutado(...) cuando el
 * escenario no se puede ejecutar de forma responsable (ver el motivo en cada caso).
 */
public class PasosMenuResponsivo {

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

	/** Reemplaza el navegador por uno con emulacion de movil (375x667, como un telefono). */
	private void abrirComoMovil() {
		if (driver != null) {
			driver.quit();
		}
		driver = Utility.crearDriverMovil(CHROMEDRIVER_PATH, 375, 667);
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
	}


	private boolean hamburguesaVisible() {
		Object r = Sitio.js(driver, "var e=document.querySelector('.menu__hamburguer');return e?[e.offsetWidth,e.offsetHeight]:[0,0];");
		@SuppressWarnings("unchecked")
		List<? extends Number> m = (List<? extends Number>) r;
		return m.get(0).longValue() > 0 && m.get(1).longValue() > 0;
	}

	private String descripcionDelEncabezado() {
		return "la barra movil muestra el logo, el icono de usuario (visible = " + Sitio.visible(driver, "#mobile-login-link")
				+ ") y el boton Carrito (visible = " + Sitio.visible(driver, "#mobile-cart-link") + ")";
	}

	@Given("que un usuario navega desde un dispositivo móvil con viewport 375x667")
	public void que_un_usuario_navega_desde_un_dispositivo_movil_con_viewport() {
		abrirComoMovil();
		Sitio.abrirInicio(driver);
	}

	// ---- CP57
	@When("presiona el ícono de menú hamburguesa")
	public void presiona_el_icono_de_menu_hamburguesa() {
		if (!hamburguesaVisible()) {
			Utility.resultado("NO PASA - El icono de menu hamburguesa no esta visible en el viewport 375x667 (su tamano es 0x0); "
					+ descripcionDelEncabezado());
		}
		Sitio.clic(driver, driver.findElement(By.cssSelector(".menu__hamburguer")));
		Sitio.pausa(1000);
	}

	@Then("el sistema despliega el menú de navegación y permite cerrarlo correctamente")
	public void el_sistema_despliega_el_menu_y_permite_cerrarlo() {
		boolean abierto = Boolean.TRUE.equals(Sitio.js(driver, "return document.querySelector('.menu__links').classList.contains('menu__links--show');"));
		Sitio.clic(driver, driver.findElement(By.cssSelector(".menu__hamburguer")));
		Sitio.pausa(800);
		boolean cerrado = !Boolean.TRUE.equals(Sitio.js(driver, "return document.querySelector('.menu__links').classList.contains('menu__links--show');"));
		if (abierto && cerrado) {
			Utility.resultado("PASA - El menu se despliega y se cierra con el icono");
		} else {
			Utility.resultado("NO PASA - Menu desplegado = " + abierto + ", cerrado despues = " + cerrado);
		}
	}

	// ---- CP58, CP59, CP60 (precondicion: menu desplegado)
	@Given("que el menú móvil está desplegado")
	public void que_el_menu_movil_esta_desplegado() {
		if (hamburguesaVisible()) {
			Sitio.clic(driver, driver.findElement(By.cssSelector(".menu__hamburguer")));
			Sitio.pausa(800);
		}
		// Si no hay hamburguesa visible, el escenario continua para comprobar que accesos existen realmente
	}

	private WebElement opcionVisible(String texto) {
		for (WebElement e : driver.findElements(By.xpath("//nav[contains(@class,'menu')]//a[contains(normalize-space(.),'" + texto + "')]"))) {
			if (e.isDisplayed()) {
				return e;
			}
		}
		return null;
	}

	@When("el usuario selecciona {string}")
	public void el_usuario_selecciona(String opcion) {
		WebElement enlace = opcionVisible(opcion);
		if (enlace == null) {
			Utility.resultado("NO PASA - La opcion \"" + opcion + "\" no esta visible en el menu movil; " + descripcionDelEncabezado());
		}
		Sitio.clic(driver, enlace);
		Sitio.pausa(1000);
	}

	@Then("el sistema muestra las categorías de productos disponibles")
	public void el_sistema_muestra_las_categorias() {
		int visibles = 0;
		for (WebElement e : driver.findElements(By.cssSelector(".menu__nesting a"))) {
			if (e.isDisplayed()) {
				visibles++;
			}
		}
		Utility.resultado(visibles > 0 ? "PASA - Se muestran " + visibles + " categorias"
				: "NO PASA - No se muestran categorias");
	}

	@When("el usuario revisa las opciones disponibles")
	public void el_usuario_revisa_las_opciones_disponibles() {
		Sitio.pausa(500);
	}

	@Then("se muestran correctamente los accesos a {string} e {string}")
	public void se_muestran_los_accesos(String a, String b) {
		boolean carrito = Sitio.visible(driver, "#mobile-cart-link");
		boolean login = Sitio.visible(driver, "#mobile-login-link");
		if (carrito && login) {
			Utility.resultado("PASA - En el viewport movil se ven los accesos a Carrito e Iniciar sesion (barra superior fija)");
		} else {
			Utility.resultado("NO PASA - Acceso a Carrito visible = " + carrito + ", a Iniciar sesion visible = " + login);
		}
	}

	@When("el usuario presiona la opción {string}")
	public void el_usuario_presiona_la_opcion(String opcion) {
		WebElement elemento = null;
		if (opcion.equals("Inicio")) {
			// En el encabezado movil, "Inicio" es el logo (enlace a "/")
			for (WebElement e : driver.findElements(By.cssSelector(".mobile-header a[href='/']"))) {
				if (e.isDisplayed()) {
					elemento = e;
				}
			}
		} else if (opcion.equals("Carrito")) {
			elemento = Sitio.visible(driver, "#mobile-cart-link") ? driver.findElement(By.id("mobile-cart-link")) : null;
		} else if (opcion.equals("Iniciar sesión")) {
			elemento = Sitio.visible(driver, "#mobile-login-link") ? driver.findElement(By.id("mobile-login-link")) : null;
		} else {
			elemento = opcionVisible(opcion);
		}
		if (elemento == null) {
			Utility.resultado("NO PASA - La opcion \"" + opcion + "\" no esta visible/accesible en el menu movil; " + descripcionDelEncabezado());
		}
		Sitio.clic(driver, elemento);
		Sitio.pausa(1500);
	}

	@Then("la opción {string} responde correctamente")
	public void la_opcion_responde_correctamente(String opcion) {
		boolean ok;
		String url = driver.getCurrentUrl();
		if (opcion.equals("Inicio")) {
			ok = url.equals(Sitio.BASE + "/");
		} else if (opcion.equals("Carrito")) {
			ok = Sitio.visible(driver, "#cartFullscreen .cart-fullscreen-header") || Sitio.visible(driver, "#cartModal .modal-content");
		} else if (opcion.equals("Iniciar sesión")) {
			ok = url.contains("/login");
		} else if (opcion.equals("Sobre Nosotros")) {
			ok = url.contains("/aboutus");
		} else {
			ok = Sitio.visible(driver, ".menu__nesting a");
		}
		Utility.resultado(ok ? "PASA - La opcion \"" + opcion + "\" responde (URL: " + url + ")"
				: "NO PASA - La opcion \"" + opcion + "\" no respondio como se esperaba (URL: " + url + ")");
	}
}
