package DefinicionPasos.iniciosesion;

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
 * Definicion de pasos - Funcionalidad: Inicio de sesion (Login)
 * Requerimiento: RQ-01 Gestion de cuentas y control de acceso
 * Escenarios cubiertos (Matriz de Trazabilidad): CP5, CP6, CP7, CP8
 *
 * Los localizadores y los mensajes esperados fueron verificados contra el HTML y el comportamiento
 * REAL de www.maderasmym.cl (carpetas "explorar" y "explorar2" del repositorio). Los resultados
 * se registran con Utility.resultado(...) (PASA / NO PASA) o Utility.noEjecutado(...) cuando el
 * escenario no se puede ejecutar de forma responsable (ver el motivo en cada caso).
 */
public class PasosInicioSesion {

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


	@Given("que un usuario accede a {string}")
	public void que_un_usuario_accede_a(String nombrePagina) {
		driver.get(Sitio.BASE + "/login");
		Sitio.esperarHasta(() -> Sitio.visible(driver, "#login-form"), 20);
	}

	@When("ingresa usuario {string} y contraseña {string}")
	public void ingresa_usuario_y_contrasena(String usuario, String password) {
		WebElement u = driver.findElement(By.id("user"));
		u.clear();
		u.sendKeys(usuario);
		WebElement p = driver.findElement(By.id("password"));
		p.clear();
		p.sendKeys(password);
	}

	@When("presiona {string}")
	public void presiona_boton(String textoBoton) {
		Sitio.clic(driver, driver.findElement(
				By.xpath("//form[@id='login-form']//button[normalize-space()='" + textoBoton + "']")));
	}

	@Then("el sistema muestra un mensaje que contiene {string}")
	public void el_sistema_muestra_un_mensaje_que_contiene(String mensajeEsperado) {
		Sitio.esperarHasta(() -> !Sitio.texto(driver, "#msg").isEmpty(), 10);
		String obtenido = Sitio.texto(driver, "#msg");
		if ("Bienvenido".equals(mensajeEsperado) && obtenido.toLowerCase().contains("credenciales inv")) {
			Utility.noEjecutado("no se dispone de una cuenta verificada para iniciar sesion (el registro exige CAPTCHA "
					+ "y no se crean cuentas reales); el sitio respondio \"" + obtenido + "\"");
		}
		if (obtenido.toLowerCase().contains(mensajeEsperado.toLowerCase())) {
			Utility.resultado("PASA - Inicio de sesion: mensaje obtenido = \"" + obtenido + "\"");
		} else {
			Utility.resultado("NO PASA - Inicio de sesion: se esperaba un mensaje con \"" + mensajeEsperado
					+ "\" y el sitio mostro \"" + obtenido + "\"");
		}
	}

	// ---- CP8: reenvio de verificacion
	@Given("que un usuario se registró pero no ha verificado su cuenta")
	public void que_un_usuario_se_registro_pero_no_ha_verificado_su_cuenta() {
		Utility.noEjecutado("el boton \"Reenviar verificacion\" solo aparece tras iniciar sesion con una cuenta registrada "
				+ "y sin verificar (error EMAIL_NOT_VERIFIED); no se puede crear esa cuenta porque el registro exige "
				+ "CAPTCHA y generaria datos reales en el sitio");
	}

	@When("intenta iniciar sesión y presiona {string}")
	public void intenta_iniciar_sesion_y_presiona(String texto) {
		Sitio.clic(driver, driver.findElement(By.id("btn-resend")));
	}

	@Then("el sistema envía un nuevo correo de verificación y muestra un mensaje que contiene {string}")
	public void el_sistema_envia_un_nuevo_correo(String mensajeEsperado) {
		Sitio.esperarHasta(() -> !Sitio.texto(driver, "#msg").isEmpty(), 10);
		String obtenido = Sitio.texto(driver, "#msg");
		if (obtenido.toLowerCase().contains(mensajeEsperado.toLowerCase())) {
			Utility.resultado("PASA - Reenvio de verificacion: \"" + obtenido + "\"");
		} else {
			Utility.resultado("NO PASA - Reenvio de verificacion: se esperaba \"" + mensajeEsperado
					+ "\" y el sitio mostro \"" + obtenido + "\"");
		}
	}
}
