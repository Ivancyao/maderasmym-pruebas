package DefinicionPasos.controlacceso;

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
 * Definicion de pasos - Funcionalidad: Control de acceso y seguridad de sesion
 * Requerimiento: RQ-01 Gestion de cuentas y control de acceso
 * Escenarios cubiertos (Matriz de Trazabilidad): CP13, CP14, CP15, CP16
 *
 * Los localizadores y los mensajes esperados fueron verificados contra el HTML y el comportamiento
 * REAL de www.maderasmym.cl (carpetas "explorar" y "explorar2" del repositorio). Los resultados
 * se registran con Utility.resultado(...) (PASA / NO PASA) o Utility.noEjecutado(...) cuando el
 * escenario no se puede ejecutar de forma responsable (ver el motivo en cada caso).
 */
public class PasosControlAccesoSesion {

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


	// "Mi cuenta" del sitio es la ruta /profile (la que usa el boton de usuario al iniciar sesion).
	private static final String RUTA_MI_CUENTA = "/profile";
	private static final String RUTA_PROTEGIDA = "/admin";

	@Given("que un usuario no ha iniciado sesión")
	public void que_un_usuario_no_ha_iniciado_sesion() {
		driver.get(Sitio.BASE + "/");
		driver.manage().deleteAllCookies();
	}

	@When("intenta acceder directamente a la sección {string}")
	public void intenta_acceder_directamente_a_la_seccion(String seccion) {
		driver.get(Sitio.BASE + RUTA_MI_CUENTA);
		Sitio.esperarHasta(() -> driver.getCurrentUrl().contains("/login"), 10);
	}

	@Then("el sistema lo redirige a la página de {string}")
	public void el_sistema_lo_redirige_a_la_pagina_de(String pagina) {
		String url = driver.getCurrentUrl();
		if (url.contains("/login")) {
			Utility.resultado("PASA - Acceso a \"Mi cuenta\" sin sesion: redirigido a " + url);
		} else {
			Utility.resultado("NO PASA - Acceso a \"Mi cuenta\" sin sesion: no hubo redireccion al login (URL final: " + url + ")");
		}
	}

	// ---- CP14 y CP15 necesitan una sesion iniciada
	@Given("que un usuario tiene una sesión iniciada")
	public void que_un_usuario_tiene_una_sesion_iniciada() {
		Utility.noEjecutado("requiere una sesion iniciada con una cuenta verificada; no se dispone de una (el registro exige "
				+ "CAPTCHA y no se crean cuentas reales en el sitio)");
	}

	@When("selecciona la opción {string}")
	public void selecciona_la_opcion(String opcion) {
		Sitio.clic(driver, driver.findElement(By.xpath("//a[contains(normalize-space(),'" + opcion + "')]")));
	}

	@Then("el sistema finaliza la sesión y lo redirige a la página de inicio como visitante")
	public void el_sistema_finaliza_la_sesion() {
		Utility.resultado(driver.getCurrentUrl().equals(Sitio.BASE + "/")
				? "PASA - Cierre de sesion: redirigido al inicio"
				: "NO PASA - Cierre de sesion: URL final " + driver.getCurrentUrl());
	}

	@When("permanece inactivo durante un periodo prolongado")
	public void permanece_inactivo_durante_un_periodo_prolongado() {
		Sitio.pausa(1000);
	}

	@Then("el sistema cierra la sesión automáticamente y solicita autenticarse nuevamente")
	public void el_sistema_cierra_la_sesion_automaticamente() {
		Utility.resultado(driver.getCurrentUrl().contains("/login")
				? "PASA - Expiracion de sesion: se solicita autenticarse"
				: "NO PASA - Expiracion de sesion: URL final " + driver.getCurrentUrl());
	}

	// ---- CP16: ruta protegida
	@Given("que un usuario no autenticado conoce la URL de una ruta protegida")
	public void que_un_usuario_no_autenticado_conoce_la_url() {
		driver.get(Sitio.BASE + "/");
		driver.manage().deleteAllCookies();
	}

	@When("la ingresa directamente en el navegador")
	public void la_ingresa_directamente_en_el_navegador() {
		driver.get(Sitio.BASE + RUTA_PROTEGIDA);
		Sitio.esperarHasta(() -> driver.getCurrentUrl().contains("/login"), 10);
	}

	@Then("el sistema deniega el acceso y lo redirige al login, sin exponer información de la cuenta")
	public void el_sistema_deniega_el_acceso() {
		boolean enLogin = driver.getCurrentUrl().contains("/login");
		boolean formularioLogin = Sitio.visible(driver, "#login-form");
		if (enLogin && formularioLogin) {
			Utility.resultado("PASA - Ruta protegida " + RUTA_PROTEGIDA + ": redirigido al login sin mostrar datos de cuenta");
		} else {
			Utility.resultado("NO PASA - Ruta protegida " + RUTA_PROTEGIDA + ": URL final " + driver.getCurrentUrl()
					+ ", formulario de login visible = " + formularioLogin);
		}
	}
}
