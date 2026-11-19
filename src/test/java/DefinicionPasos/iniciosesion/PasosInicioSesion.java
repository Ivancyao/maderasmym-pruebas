package DefinicionPasos.iniciosesion;

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
 * Definicion de pasos - Funcionalidad: Inicio de sesion (Login)
 * Requerimiento: RQ-01 Gestion de cuentas y control de acceso
 * Escenarios cubiertos (Matriz de Trazabilidad): CP5, CP6, CP7, CP8
 */
public class PasosInicioSesion {

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

	@Given("que un usuario accede a {string}")
	public void que_un_usuario_accede_a(String seccion) {
		driver.get("https://www.maderasmym.cl/login");
	}

	@Given("que un usuario se registró pero no ha verificado su cuenta")
	public void que_un_usuario_se_registro_pero_no_ha_verificado_su_cuenta() {
		driver.get("https://www.maderasmym.cl/login");
	}

	@When("ingresa usuario {string} y contraseña {string}")
	public void ingresa_usuario_y_contrasena(String usuario, String password) {
		driver.findElement(By.xpath("//input[@name='username']")).clear();
		driver.findElement(By.xpath("//input[@name='username']")).sendKeys(usuario);
		driver.findElement(By.xpath("//input[@name='password']")).clear();
		driver.findElement(By.xpath("//input[@name='password']")).sendKeys(password);
	}

	@When("presiona {string}")
	public void presiona_boton(String texto) {
		if (texto.equals("Iniciar sesión")) {
			driver.findElement(By.xpath("//button[contains(text(),'Iniciar sesión')]")).click();
		}
	}

	@When("intenta iniciar sesión y presiona {string}")
	public void intenta_iniciar_sesion_y_presiona(String textoBoton) {
		// "Reenviar verificación" puede ser un botón o un enlace según el estado de la página.
		driver.findElement(By.xpath("//*[self::button or self::a][contains(text(),'" + textoBoton + "')]")).click();
	}

	@Then("el sistema muestra un mensaje que contiene {string}")
	public void el_sistema_muestra_un_mensaje_que_contiene(String mensajeEsperado) {
		String mensajeObtenido;
		try {
			mensajeObtenido = driver.findElement(
					By.xpath("//*[contains(text(),'" + mensajeEsperado + "')]")).getText();
		} catch (Exception e) {
			mensajeObtenido = "";
		}
		if (mensajeObtenido.contains(mensajeEsperado)) {
			Utility.resultado("PASA - Inicio de sesión: mensaje obtenido = \"" + mensajeObtenido + "\"");
		} else {
			Utility.resultado("NO PASA - Inicio de sesión: se esperaba un mensaje con \""
					+ mensajeEsperado + "\"");
		}
	}

	@Then("el sistema envía un nuevo correo de verificación y muestra un mensaje que contiene {string}")
	public void el_sistema_envia_un_nuevo_correo_de_verificacion(String mensajeEsperado) {
		el_sistema_muestra_un_mensaje_que_contiene(mensajeEsperado);
	}
}
