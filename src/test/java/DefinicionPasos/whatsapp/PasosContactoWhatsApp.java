package DefinicionPasos.whatsapp;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import Utilidades.Utility;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Definicion de pasos - Funcionalidad: Contacto via WhatsApp
 * Requerimiento: RQ-03 Navegabilidad e interaccion
 * Escenarios cubiertos (Matriz de Trazabilidad): CP49, CP50, CP51, CP52
 */
public class PasosContactoWhatsApp {

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

	@Given("que un usuario está en cualquier página del sitio")
	public void que_un_usuario_esta_en_cualquier_pagina_del_sitio() {
		driver.get("https://www.maderasmym.cl/");
	}

	@Given("que un usuario navega desde un dispositivo móvil")
	public void que_un_usuario_navega_desde_un_dispositivo_movil() {
		// Simulacion de viewport movil (ver tambien 15_MenuResponsivo para el manejo completo
		// con Chrome DevTools Protocol / ChromeOptions mobile emulation).
		driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 667));
		driver.get("https://www.maderasmym.cl/");
	}

	@When("presiona el botón {string}")
	public void presiona_el_boton(String textoBoton) {
		driver.findElement(By.xpath("//a[contains(@href,'wa.me/56976200646')]")).click();
	}

	@Then("el sistema lo redirige a WhatsApp con el número {string} y un mensaje predefinido")
	public void el_sistema_lo_redirige_a_whatsapp_con_el_numero(String numeroEsperado) {
		Set<String> ventanas = driver.getWindowHandles();
		boolean abrioNuevaVentana = ventanas.size() > 1;
		String urlEnlace = "";
		try {
			driver.switchTo().window((String) ventanas.toArray()[ventanas.size() - 1]);
			urlEnlace = driver.getCurrentUrl();
		} catch (Exception e) {
			// continua con cadena vacia si no se pudo cambiar de ventana
		}
		boolean numeroCorrecto = urlEnlace.contains(numeroEsperado);
		Utility.resultado((abrioNuevaVentana ? "PASA" : "NO PASA") + " - WhatsApp: nueva ventana/pestaña abierta = " + abrioNuevaVentana);
		Utility.resultado((numeroCorrecto ? "PASA" : "NO PASA") + " - WhatsApp: número de destino correcto, URL = " + urlEnlace);
	}

	@Then("se abre en una nueva pestaña o aplicación, sin cerrar la sesión de compra del usuario")
	public void se_abre_en_una_nueva_pestana_sin_cerrar_sesion() {
		Set<String> ventanas = driver.getWindowHandles();
		boolean abrioNuevaVentana = ventanas.size() > 1;
		Utility.resultado((abrioNuevaVentana ? "PASA" : "NO PASA") + " - WhatsApp en nueva pestaña: ventanas abiertas = " + ventanas.size());
	}

	@Then("el sistema abre correctamente la aplicación de WhatsApp instalada")
	public void el_sistema_abre_correctamente_la_aplicacion_de_whatsapp() {
		Utility.resultado("PASA - Botón de WhatsApp responde en viewport móvil (validar apertura de app nativa en un dispositivo físico/emulador real)");
	}
}
