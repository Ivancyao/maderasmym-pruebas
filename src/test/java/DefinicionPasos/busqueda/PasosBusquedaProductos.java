package DefinicionPasos.busqueda;

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
 * Definicion de pasos - Funcionalidad: Busqueda de productos (buscador principal)
 * Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
 * Escenarios cubiertos (Matriz de Trazabilidad): CP17, CP18, CP19, CP20
 *
 * Los localizadores y los mensajes esperados fueron verificados contra el HTML y el comportamiento
 * REAL de www.maderasmym.cl (carpetas "explorar" y "explorar2" del repositorio). Los resultados
 * se registran con Utility.resultado(...) (PASA / NO PASA) o Utility.noEjecutado(...) cuando el
 * escenario no se puede ejecutar de forma responsable (ver el motivo en cada caso).
 */
public class PasosBusquedaProductos {

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


	private int tarjetasAntes = 0;

	@Given("que un usuario se encuentra en la página de inicio")
	public void que_un_usuario_se_encuentra_en_la_pagina_de_inicio() {
		Sitio.abrirInicio(driver);
		tarjetasAntes = Sitio.tarjetas(driver);
	}

	@When("escribe {string} en el buscador")
	public void escribe_en_el_buscador(String texto) {
		WebElement campo = driver.findElement(By.id("search-input"));
		campo.clear();
		campo.sendKeys(texto);
	}

	@When("ejecuta la búsqueda")
	public void ejecuta_la_busqueda() {
		Sitio.clic(driver, driver.findElement(By.cssSelector("#search-form .search-btn")));
		Sitio.pausa(2500);
	}

	@Then("el sistema responde de forma {string}")
	public void el_sistema_responde_de_forma(String comportamiento) {
		int tarjetas = Sitio.tarjetas(driver);
		String grilla = Sitio.texto(driver, "#catalog-grid");
		if (comportamiento.startsWith("listado")) {
			if (tarjetas > 0) {
				Utility.resultado("PASA - Busqueda: el sistema muestra " + tarjetas + " producto(s) en el listado");
			} else {
				Utility.resultado("NO PASA - Busqueda: se esperaba un listado de resultados y no hay productos (" + grilla + ")");
			}
		} else if (comportamiento.startsWith("sin resultados")) {
			if (tarjetas == 0 && grilla.toLowerCase().contains("no hay productos")) {
				Utility.resultado("PASA - Busqueda sin resultados: el sitio muestra \"" + grilla + "\"");
			} else {
				Utility.resultado("NO PASA - Busqueda sin resultados: productos mostrados = " + tarjetas + ", texto = \"" + grilla + "\"");
			}
		} else {
			String alerta = Sitio.leerAlerta(driver, 1);
			boolean scriptInyectado = Boolean.TRUE.equals(Sitio.js(driver,
					"return document.querySelectorAll('#catalog-grid script, #resultq script').length > 0;"));
			if (alerta == null && !scriptInyectado) {
				Utility.resultado("PASA - Busqueda con codigo: el texto se trato como dato (sin alert ni script inyectado)");
			} else {
				Utility.resultado("NO PASA - Busqueda con codigo: alert ejecutado = " + alerta + ", script inyectado = " + scriptInyectado);
			}
		}
	}

	// ---- CP19: campo vacio
	@When("presiona buscar sin ingresar texto")
	public void presiona_buscar_sin_ingresar_texto() {
		Sitio.clic(driver, driver.findElement(By.cssSelector("#search-form .search-btn")));
		Sitio.pausa(1500);
	}

	@Then("el sistema no ejecuta la búsqueda o solicita ingresar un término")
	public void el_sistema_no_ejecuta_la_busqueda() {
		String aviso = String.valueOf(Sitio.js(driver, "return document.getElementById('search-input').validationMessage;"));
		boolean sinCambios = Sitio.tarjetas(driver) == tarjetasAntes;
		if (!aviso.isEmpty() && !"null".equals(aviso)) {
			Utility.resultado("PASA - Busqueda vacia: el navegador solicita completar el campo (\"" + aviso + "\")");
		} else if (sinCambios) {
			Utility.resultado("PASA - Busqueda vacia: no se ejecuto la busqueda (el listado no cambio)");
		} else {
			Utility.resultado("NO PASA - Busqueda vacia: se ejecuto una busqueda sin texto");
		}
	}
}
