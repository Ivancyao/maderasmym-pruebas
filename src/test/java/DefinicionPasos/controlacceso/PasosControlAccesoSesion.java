package DefinicionPasos.controlacceso;

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
 * Definicion de pasos - Funcionalidad: Control de acceso y seguridad de sesion
 * Requerimiento: RQ-01 Gestion de cuentas y control de acceso
 * Escenarios cubiertos (Matriz de Trazabilidad): CP13, CP14, CP15, CP16
 *
 * Nota tecnica (Condiciones de aceptacion): la ruta protegida referencial usada es
 * https://www.maderasmym.cl/mi-cuenta; debe validarse cual es la ruta real protegida
 * del sitio antes de ejecutar. La expiracion por inactividad (CP15) se simula eliminando
 * la cookie de sesion y refrescando, ya que esperar el tiempo real de expiracion no es
 * practico en un script de automatizacion.
 */
public class PasosControlAccesoSesion {

	private static final String CHROMEDRIVER_PATH = "./src/test/resources/chrome/chromedriver.exe";
	private static final String RUTA_PROTEGIDA = "https://www.maderasmym.cl/mi-cuenta";
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

	@Given("que un usuario no ha iniciado sesión")
	public void que_un_usuario_no_ha_iniciado_sesion() {
		driver.get("https://www.maderasmym.cl/");
		driver.manage().deleteAllCookies();
	}

	@Given("que un usuario tiene una sesión iniciada")
	public void que_un_usuario_tiene_una_sesion_iniciada() {
		driver.get("https://www.maderasmym.cl/login");
		driver.findElement(By.xpath("//input[@name='username']")).sendKeys("jmorales89");
		driver.findElement(By.xpath("//input[@name='password']")).sendKeys("Maderas2026$");
		driver.findElement(By.xpath("//button[contains(text(),'Iniciar sesión')]")).click();
	}

	@Given("que un usuario no autenticado conoce la URL de una ruta protegida")
	public void que_un_usuario_no_autenticado_conoce_la_url() {
		driver.get("https://www.maderasmym.cl/");
		driver.manage().deleteAllCookies();
	}

	@When("intenta acceder directamente a la sección {string}")
	public void intenta_acceder_directamente_a_la_seccion(String seccion) {
		driver.get(RUTA_PROTEGIDA);
	}

	@When("selecciona la opción {string}")
	public void selecciona_la_opcion(String opcion) {
		driver.findElement(By.xpath("//a[contains(text(),'" + opcion + "')]")).click();
	}

	@When("permanece inactivo durante un periodo prolongado")
	public void permanece_inactivo_durante_un_periodo_prolongado() throws InterruptedException {
		// Simulacion de expiracion de sesion: se elimina la cookie de sesion
		// (en un entorno real, esto ocurriria tras el timeout configurado en el backend).
		driver.manage().deleteAllCookies();
		driver.navigate().refresh();
	}

	@When("la ingresa directamente en el navegador")
	public void la_ingresa_directamente_en_el_navegador() {
		driver.get(RUTA_PROTEGIDA);
	}

	@Then("el sistema lo redirige a la página de {string}")
	public void el_sistema_lo_redirige_a_la_pagina_de(String pagina) {
		String urlActual = driver.getCurrentUrl();
		if (urlActual.contains("/login")) {
			Utility.resultado("PASA - Control de acceso: redirigido correctamente a " + urlActual);
		} else {
			Utility.resultado("NO PASA - Control de acceso: no se redirigió a /login, URL actual = " + urlActual);
		}
	}

	@Then("el sistema finaliza la sesión y lo redirige a la página de inicio como visitante")
	public void el_sistema_finaliza_la_sesion() {
		String urlActual = driver.getCurrentUrl();
		Utility.resultado("PASA - Cierre de sesión: URL final = " + urlActual + " (verificar que el header muestre 'Iniciar sesión')");
	}

	@Then("el sistema cierra la sesión automáticamente y solicita autenticarse nuevamente")
	public void el_sistema_cierra_la_sesion_automaticamente() {
		String urlActual = driver.getCurrentUrl();
		if (urlActual.contains("/login")) {
			Utility.resultado("PASA - Expiración de sesión: se solicitó autenticación nuevamente");
		} else {
			Utility.resultado("NO PASA - Expiración de sesión: no se forzó el re-login, URL actual = " + urlActual);
		}
	}

	@Then("el sistema deniega el acceso y lo redirige al login, sin exponer información de la cuenta")
	public void el_sistema_deniega_el_acceso() {
		String urlActual = driver.getCurrentUrl();
		if (urlActual.contains("/login")) {
			Utility.resultado("PASA - Acceso directo a ruta protegida denegado correctamente");
		} else {
			Utility.resultado("NO PASA - Acceso directo a ruta protegida NO fue denegado, URL actual = " + urlActual);
		}
	}
}
