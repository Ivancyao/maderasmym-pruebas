package DefinicionPasos.whatsapp;

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
 * Definicion de pasos - Funcionalidad: Contacto via WhatsApp
 * Requerimiento: RQ-03 Navegabilidad e interaccion
 * Escenarios cubiertos (Matriz de Trazabilidad): CP49, CP50, CP51, CP52
 *
 * Los localizadores y los mensajes esperados fueron verificados contra el HTML y el comportamiento
 * REAL de www.maderasmym.cl (carpetas "explorar" y "explorar2" del repositorio). Los resultados
 * se registran con Utility.resultado(...) (PASA / NO PASA) o Utility.noEjecutado(...) cuando el
 * escenario no se puede ejecutar de forma responsable (ver el motivo en cada caso).
 */
public class PasosContactoWhatsApp {

	private static final String CHROMEDRIVER_PATH = "./src/test/resources/chrome/chromedriver.exe";
	WebDriver driver;
	private int numeroPaso = 0;

	@Before
	public void setUp() {
		driver = Utility.crearDriver(CHROMEDRIVER_PATH);
		Utility.ajustarVentana(driver);
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


	private String original = "";
	private String hrefBoton = "";

	@Given("que un usuario está en cualquier página del sitio")
	public void que_un_usuario_esta_en_cualquier_pagina_del_sitio() {
		Sitio.abrirInicio(driver);
		original = driver.getWindowHandle();
	}

	@Given("que un usuario navega desde un dispositivo móvil")
	public void que_un_usuario_navega_desde_un_dispositivo_movil() {
		abrirComoMovil();
		Sitio.abrirInicio(driver);
		original = driver.getWindowHandle();
	}

	@When("presiona el botón {string}")
	public void presiona_el_boton(String boton) {
		// Se agrega un producto para comprobar despues que la "sesion de compra" no se pierde
		Sitio.agregarProductos(driver, 1);
		WebElement enlace = driver.findElement(By.cssSelector("a.whatsapp-float"));
		hrefBoton = enlace.getAttribute("href");
		Sitio.clic(driver, enlace);
		Sitio.esperarHasta(() -> driver.getWindowHandles().size() > 1, 8);
	}

	/** Cambia a la pestana nueva y espera a que cargue WhatsApp (devuelve la URL). */
	private String urlDeLaPestanaNueva() {
		for (String h : driver.getWindowHandles()) {
			if (!h.equals(original)) {
				driver.switchTo().window(h);
			}
		}
		Sitio.esperarHasta(() -> driver.getCurrentUrl().contains("whatsapp") || driver.getCurrentUrl().contains("wa.me"), 15);
		return driver.getCurrentUrl();
	}

	@Then("el sistema lo redirige a WhatsApp con el número {string} y un mensaje predefinido")
	public void el_sistema_lo_redirige_a_whatsapp(String numero) {
		boolean enlaceCorrecto = hrefBoton.contains("wa.me/" + numero) && hrefBoton.contains("text=");
		boolean nuevaPestana = driver.getWindowHandles().size() > 1;
		String url = nuevaPestana ? urlDeLaPestanaNueva() : driver.getCurrentUrl();
		boolean enWhatsapp = url.contains("whatsapp") || url.contains("wa.me");
		if (enlaceCorrecto && enWhatsapp) {
			Utility.resultado("PASA - El boton apunta a " + hrefBoton + " y abre WhatsApp (" + url + ")");
		} else {
			Utility.resultado("NO PASA - Enlace con numero y mensaje = " + enlaceCorrecto + " (" + hrefBoton + "), pagina abierta = " + url);
		}
	}

	@Then("se abre en una nueva pestaña o aplicación, sin cerrar la sesión de compra del usuario")
	public void se_abre_en_una_nueva_pestana() {
		boolean nuevaPestana = driver.getWindowHandles().size() > 1;
		Sitio.volverALaOriginal(driver, original);
		boolean sigueEnElSitio = driver.getCurrentUrl().startsWith(Sitio.BASE);
		int contador = Sitio.contadorCarrito(driver);
		if (nuevaPestana && sigueEnElSitio && contador == 1) {
			Utility.resultado("PASA - WhatsApp se abrio en otra pestana y el carrito conserva " + contador + " producto");
		} else {
			Utility.resultado("NO PASA - Nueva pestana = " + nuevaPestana + ", sigue en el sitio = " + sigueEnElSitio
					+ ", contador del carrito = " + contador);
		}
	}

	@Then("el sistema abre correctamente la aplicación de WhatsApp instalada")
	public void el_sistema_abre_la_aplicacion_de_whatsapp() {
		// En el navegador de pruebas no hay aplicacion instalada: se verifica que el boton sea un enlace universal
		// de WhatsApp (wa.me), que en un telefono abre la aplicacion, y que se abre en la pestana nueva.
		boolean enlaceUniversal = hrefBoton.startsWith("https://wa.me/");
		boolean nuevaPestana = driver.getWindowHandles().size() > 1;
		String url = nuevaPestana ? urlDeLaPestanaNueva() : driver.getCurrentUrl();
		if (enlaceUniversal && (url.contains("whatsapp") || url.contains("wa.me"))) {
			Utility.resultado("PASA (verificacion parcial: no hay app instalada en el servidor) - enlace universal " + hrefBoton);
		} else {
			Utility.resultado("NO PASA - Enlace = " + hrefBoton + ", pagina abierta = " + url);
		}
	}
}
