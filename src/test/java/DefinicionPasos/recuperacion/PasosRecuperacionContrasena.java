package DefinicionPasos.recuperacion;

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
 * Definicion de pasos - Funcionalidad: Recuperacion de contrasena
 * Requerimiento: RQ-01 Gestion de cuentas y control de acceso
 * Escenarios cubiertos (Matriz de Trazabilidad): CP9, CP10, CP11, CP12
 *
 * Los localizadores y los mensajes esperados fueron verificados contra el HTML y el comportamiento
 * REAL de www.maderasmym.cl (carpetas "explorar" y "explorar2" del repositorio). Los resultados
 * se registran con Utility.resultado(...) (PASA / NO PASA) o Utility.noEjecutado(...) cuando el
 * escenario no se puede ejecutar de forma responsable (ver el motivo en cada caso).
 */
public class PasosRecuperacionContrasena {

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
		driver.get(Sitio.BASE + "/forgot-password");
		Sitio.esperarHasta(() -> Sitio.visible(driver, "#forgot-form"), 20);
	}

	@When("ingresa el correo {string} y confirma la solicitud")
	public void ingresa_el_correo_y_confirma_la_solicitud(String correo) {
		WebElement campo = driver.findElement(By.id("identificador"));
		campo.clear();
		if (!correo.isEmpty()) {
			campo.sendKeys(correo);
		}
		Sitio.clic(driver, driver.findElement(By.xpath("//form[@id='forgot-form']//button[@type='submit']")));
	}

	@Then("el sistema muestra un mensaje que contiene {string}")
	public void el_sistema_muestra_un_mensaje_que_contiene(String mensajeEsperado) {
		Sitio.esperarHasta(() -> !Sitio.texto(driver, "#msg").isEmpty(), 10);
		String obtenido = Sitio.texto(driver, "#msg");
		if (obtenido.toLowerCase().contains(mensajeEsperado.toLowerCase())) {
			Utility.resultado("PASA - Recuperacion de contrasena: mensaje obtenido = \"" + obtenido + "\"");
		} else {
			Utility.resultado("NO PASA - Recuperacion de contrasena: se esperaba un mensaje con \"" + mensajeEsperado
					+ "\" y el sitio mostro \"" + obtenido + "\"");
		}
	}

	// ---- CP12: enlace expirado (se usa un token inexistente/expirado: el sitio lo trata como expirado)
	@Given("que un usuario recibió un correo de recuperación")
	public void que_un_usuario_recibio_un_correo_de_recuperacion() {
		// Precondicion simulada: no se puede leer el correo; se usa un token vencido en el paso siguiente.
	}

	@When("utiliza el enlace después de su periodo de validez")
	public void utiliza_el_enlace_despues_de_su_periodo_de_validez() {
		driver.get(Sitio.BASE + "/reset-password?token=EXPIRADO123");
		Sitio.esperarHasta(() -> Sitio.texto(driver, "body").length() > 20, 10);
	}

	@Then("el sistema informa que el enlace expiró y ofrece solicitar uno nuevo")
	public void el_sistema_informa_que_el_enlace_expiro_y_ofrece_solicitar_uno_nuevo() {
		String pagina = Sitio.texto(driver, "body").toLowerCase();
		boolean informaExpiracion = pagina.contains("expir");
		boolean ofreceNuevo = !driver.findElements(By.xpath("//a[contains(normalize-space(),'Solicitar un enlace nuevo')]")).isEmpty();
		if (informaExpiracion && ofreceNuevo) {
			Utility.resultado("PASA - Enlace expirado: el sitio informa la expiracion y ofrece \"Solicitar un enlace nuevo\"");
		} else {
			Utility.resultado("NO PASA - Enlace expirado: informa expiracion = " + informaExpiracion
					+ ", ofrece solicitar uno nuevo = " + ofreceNuevo);
		}
	}
}
