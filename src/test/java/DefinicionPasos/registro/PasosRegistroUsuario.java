package DefinicionPasos.registro;

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
 * Definicion de pasos - Funcionalidad: Registro de nuevo usuario (Crear cuenta)
 * Requerimiento: RQ-01 Gestion de cuentas y control de acceso
 * Escenarios cubiertos (Matriz de Trazabilidad): CP1, CP2, CP3, CP4
 *
 * Los localizadores y los mensajes esperados fueron verificados contra el HTML y el comportamiento
 * REAL de www.maderasmym.cl (carpetas "explorar" y "explorar2" del repositorio). Los resultados
 * se registran con Utility.resultado(...) (PASA / NO PASA) o Utility.noEjecutado(...) cuando el
 * escenario no se puede ejecutar de forma responsable (ver el motivo en cada caso).
 */
public class PasosRegistroUsuario {

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


	private static final String POLITICA = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\w\\s]).{8,}$";
	private boolean crearia_cuenta_real = false;

	@Given("que un usuario visitante accede a la página {string}")
	public void que_un_usuario_visitante_accede_a_la_pagina(String nombrePagina) {
		driver.get(Sitio.BASE + "/register");
		Sitio.esperarHasta(() -> Sitio.visible(driver, "#register-form"), 20);
	}

	private void escribir(String id, String valor) {
		WebElement campo = driver.findElement(By.id(id));
		campo.clear();
		campo.sendKeys(valor);
	}

	@When("ingresa usuario {string}, correo {string}, contraseña {string} y repite la contraseña {string}")
	public void ingresa_datos_de_registro(String usuario, String correo, String password, String confirmPassword) {
		escribir("user", usuario);
		escribir("email", correo);
		escribir("password", password);
		escribir("password2", confirmPassword);
		// Datos validos con un correo que no esta registrado: el envio crearia una cuenta real.
		crearia_cuenta_real = password.equals(confirmPassword) && password.matches(POLITICA)
				&& !"contacto@maderasmym.cl".equalsIgnoreCase(correo);
	}

	@When("presiona {string}")
	public void presiona_boton(String textoBoton) {
		if (crearia_cuenta_real) {
			Utility.noEjecutado("el envio de datos validos crearia una cuenta real en el sitio de la empresa y el "
					+ "formulario exige ademas resolver un CAPTCHA (Cloudflare Turnstile), que no se puede automatizar");
		}
		WebElement boton = driver.findElement(
				By.xpath("//form[@id='register-form']//button[normalize-space()='" + textoBoton + "']"));
		Sitio.clic(driver, boton);
	}

	private String mensajeDelFormulario() {
		String error = Sitio.texto(driver, "#err-msg");
		return error.isEmpty() ? Sitio.texto(driver, "#ok-msg") : error;
	}

	@Then("el sistema muestra un mensaje que contiene {string}")
	public void el_sistema_muestra_un_mensaje_que_contiene(String mensajeEsperado) {
		Sitio.esperarHasta(() -> !mensajeDelFormulario().isEmpty(), 8);
		String obtenido = mensajeDelFormulario();
		if (obtenido.toLowerCase().startsWith("completa la v")) {
			Utility.noEjecutado("el formulario exige resolver el CAPTCHA (Cloudflare Turnstile) antes de enviar; "
					+ "mensaje del sitio: \"" + obtenido + "\"");
		}
		if (obtenido.toLowerCase().contains(mensajeEsperado.toLowerCase())) {
			Utility.resultado("PASA - Registro de usuario: mensaje obtenido = \"" + obtenido + "\"");
		} else {
			Utility.resultado("NO PASA - Registro de usuario: se esperaba un mensaje con \"" + mensajeEsperado
					+ "\" y el sitio mostro \"" + obtenido + "\"");
		}
	}
}
