package DefinicionPasos.recuperacion;

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
 * Definicion de pasos - Funcionalidad: Recuperacion de contrasena
 * Requerimiento: RQ-01 Gestion de cuentas y control de acceso
 * Escenarios cubiertos (Matriz de Trazabilidad): CP9, CP10, CP11, CP12
 */
public class PasosRecuperacionContrasena {

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
		driver.get("https://www.maderasmym.cl/forgot-password");
	}

	@Given("que un usuario recibió un correo de recuperación")
	public void que_un_usuario_recibio_un_correo_de_recuperacion() {
		// Simula el acceso directo con un token expirado (referencial)
		driver.get("https://www.maderasmym.cl/reset-password?token=EXPIRADO123");
	}

	@When("ingresa el correo {string} y confirma la solicitud")
	public void ingresa_el_correo_y_confirma_la_solicitud(String correo) {
		if (!correo.isEmpty()) {
			driver.findElement(By.xpath("//input[@name='email']")).clear();
			driver.findElement(By.xpath("//input[@name='email']")).sendKeys(correo);
		}
		driver.findElement(By.xpath("//button[contains(text(),'Enviar enlace')]")).click();
	}

	@When("utiliza el enlace después de su periodo de validez")
	public void utiliza_el_enlace_despues_de_su_periodo_de_validez() {
		// La navegacion ya se realizo en el Given; este paso valida el estado de la pagina.
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
			Utility.resultado("PASA - Recuperación de contraseña: mensaje obtenido = \"" + mensajeObtenido + "\"");
		} else {
			Utility.resultado("NO PASA - Recuperación de contraseña: se esperaba un mensaje con \""
					+ mensajeEsperado + "\"");
		}
	}

	@Then("el sistema informa que el enlace expiró y ofrece solicitar uno nuevo")
	public void el_sistema_informa_que_el_enlace_expiro() {
		el_sistema_muestra_un_mensaje_que_contiene("expirado");
	}
}
