package DefinicionPasos.categorias;

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
 * Definicion de pasos - Funcionalidad: Navegacion por categorias de productos
 * Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
 * Escenarios cubiertos (Matriz de Trazabilidad): CP21, CP22, CP23, CP24
 *
 * Los localizadores y los mensajes esperados fueron verificados contra el HTML y el comportamiento
 * REAL de www.maderasmym.cl (carpetas "explorar" y "explorar2" del repositorio). Los resultados
 * se registran con Utility.resultado(...) (PASA / NO PASA) o Utility.noEjecutado(...) cuando el
 * escenario no se puede ejecutar de forma responsable (ver el motivo en cada caso).
 */
public class PasosNavegacionCategorias {

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


	private String tipoEsperado = "";
	private String medidaEsperada = "";

	private WebElement enlaceDelMenuLateral(String texto) {
		return driver.findElement(By.xpath("//div[contains(@class,'sidebar')]//ul[contains(@class,'sidebar-categories')]"
				+ "//a[normalize-space()='" + texto + "']"));
	}

	@Given("que un usuario está en el menú {string}")
	public void que_un_usuario_esta_en_el_menu(String menu) {
		Sitio.abrirInicio(driver);
	}

	@When("selecciona {string}")
	public void selecciona(String categoria) {
		tipoEsperado = categoria.replace("Pino Oregón ", "");
		Sitio.clic(driver, enlaceDelMenuLateral(categoria));
		Sitio.pausa(2000);
	}

	@Then("el sistema despliega el listado de productos de esa categoría")
	public void el_sistema_despliega_el_listado_de_productos_de_esa_categoria() {
		int tarjetas = Sitio.tarjetas(driver);
		if (tarjetas > 0) {
			Utility.resultado("PASA - Categoria \"" + tipoEsperado + "\": el sitio muestra " + tarjetas + " producto(s)");
		} else {
			Utility.resultado("NO PASA - Categoria \"" + tipoEsperado + "\": no se muestran productos");
		}
	}

	@When("despliega la categoría {string} y selecciona la medida {string}")
	public void despliega_la_categoria_y_selecciona_la_medida(String categoria, String medida) {
		medidaEsperada = medida.replace(" mts", "").trim();
		Sitio.clic(driver, enlaceDelMenuLateral(categoria));
		Sitio.pausa(1500);
		WebElement enlaceMedida = driver.findElement(By.xpath("//div[contains(@class,'sidebar')]//a[normalize-space()='"
				+ categoria + "']/following-sibling::ul[1]//a[normalize-space()='" + medida + "']"));
		Sitio.clic(driver, enlaceMedida);
		Sitio.pausa(2000);
	}

	@Then("el sistema muestra solo los productos de esa medida específica")
	public void el_sistema_muestra_solo_los_productos_de_esa_medida_especifica() {
		List<WebElement> largos = driver.findElements(By.cssSelector("#catalog-grid .product-card .meta div:nth-child(2)"));
		int fuera = 0;
		for (WebElement l : largos) {
			String valor = l.getText().replace(',', '.');
			if (!valor.contains(medidaEsperada)) {
				fuera++;
			}
		}
		if (!largos.isEmpty() && fuera == 0) {
			Utility.resultado("PASA - Medida " + medidaEsperada + " mts: los " + largos.size() + " productos mostrados tienen esa medida");
		} else {
			Utility.resultado("NO PASA - Medida " + medidaEsperada + " mts: productos mostrados = " + largos.size()
					+ ", con otra medida = " + fuera);
		}
	}

	@When("navega a la categoría {string} y revisa el listado de productos desplegado")
	public void navega_a_la_categoria_y_revisa_el_listado(String categoria) {
		tipoEsperado = categoria.replace("Pino Oregón ", "");
		Sitio.clic(driver, enlaceDelMenuLateral(categoria));
		Sitio.pausa(2000);
	}

	@Then("todos los productos mostrados pertenecen exclusivamente a la categoría seleccionada")
	public void todos_los_productos_mostrados_pertenecen_a_la_categoria() {
		List<WebElement> tipos = driver.findElements(By.cssSelector("#catalog-grid .product-card .meta div:first-child"));
		int otros = 0;
		for (WebElement t : tipos) {
			if (!t.getText().contains(tipoEsperado)) {
				otros++;
			}
		}
		if (!tipos.isEmpty() && otros == 0) {
			Utility.resultado("PASA - Los " + tipos.size() + " productos de la pagina 1 son de tipo \"" + tipoEsperado + "\"");
		} else {
			Utility.resultado("NO PASA - Productos mostrados = " + tipos.size() + ", de otro tipo = " + otros);
		}
	}

	// ---- CP24: categoria sin productos
	@When("accede a una categoría sin productos disponibles")
	public void accede_a_una_categoria_sin_productos_disponibles() {
		String[] candidatas = { "Polines", "Treillage", "Cielo Elaborado", "Planchas Osb", "Vitrificante de pisos", "Base metalica" };
		for (String nombre : candidatas) {
			Sitio.clic(driver, enlaceDelMenuLateral(nombre));
			Sitio.pausa(2000);
			if (Sitio.tarjetas(driver) == 0) {
				tipoEsperado = nombre;
				return;
			}
		}
		Utility.noEjecutado("ninguna de las categorias simples probadas esta sin productos en este momento");
	}

	@Then("el sistema muestra un mensaje indicando que no hay productos disponibles")
	public void el_sistema_muestra_un_mensaje_indicando_que_no_hay_productos() {
		String grilla = Sitio.texto(driver, "#catalog-grid");
		if (grilla.toLowerCase().contains("no hay productos")) {
			Utility.resultado("PASA - Categoria \"" + tipoEsperado + "\" sin productos: el sitio muestra \"" + grilla + "\"");
		} else {
			Utility.resultado("NO PASA - Categoria \"" + tipoEsperado + "\" sin productos: mensaje obtenido \"" + grilla + "\"");
		}
	}
}
