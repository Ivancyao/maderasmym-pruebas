package DefinicionPasos.registro;

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
 * Definicion de pasos - Funcionalidad: Registro de nuevo usuario (Crear cuenta)
 * Requerimiento: RQ-01 Gestion de cuentas y control de acceso
 * Escenarios cubiertos (Matriz de Trazabilidad): CP1, CP2, CP3, CP4
 *
 * NOTA: los localizadores XPath fueron tomados del levantamiento de la Guia 1.3.4/1.3.5
 * (columna "Datos y Localizadores"). Estan marcados como referenciales: antes de ejecutar
 * contra el sitio real www.maderasmym.cl se deben validar/ajustar con "Inspeccionar elemento".
 */
public class PasosRegistroUsuario {

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

	@Given("que un usuario visitante accede a la página {string}")
	public void que_un_usuario_visitante_accede_a_la_pagina(String nombrePagina) {
		driver.get("https://www.maderasmym.cl/register");
	}

	@When("ingresa usuario {string}, correo {string}, contraseña {string} y repite la contraseña {string}")
	public void ingresa_datos_de_registro(String usuario, String correo, String password, String confirmPassword) {
		driver.findElement(By.xpath("//input[@name='username']")).clear();
		driver.findElement(By.xpath("//input[@name='username']")).sendKeys(usuario);

		driver.findElement(By.xpath("//input[@type='email']")).clear();
		driver.findElement(By.xpath("//input[@type='email']")).sendKeys(correo);

		driver.findElement(By.xpath("//input[@name='password']")).clear();
		driver.findElement(By.xpath("//input[@name='password']")).sendKeys(password);

		driver.findElement(By.xpath("//input[@name='confirmPassword']")).clear();
		driver.findElement(By.xpath("//input[@name='confirmPassword']")).sendKeys(confirmPassword);
	}

	@When("presiona {string}")
	public void presiona_boton(String textoBoton) {
		driver.findElement(By.xpath("//button[contains(text(),'" + textoBoton + "')]")).click();
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
			Utility.resultado("PASA - Registro de usuario: mensaje obtenido = \"" + mensajeObtenido + "\"");
		} else {
			Utility.resultado("NO PASA - Registro de usuario: se esperaba un mensaje con \""
					+ mensajeEsperado + "\" y no fue encontrado en pantalla");
		}
	}
}
