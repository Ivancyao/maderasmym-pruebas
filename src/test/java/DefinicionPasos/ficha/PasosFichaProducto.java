package DefinicionPasos.ficha;

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
 * Definicion de pasos - Funcionalidad: Visualizacion de ficha de producto
 * Requerimiento: RQ-02 Operaciones del negocio (catalogo, carrito y pedido)
 * Escenarios cubiertos (Matriz de Trazabilidad): CP25, CP26, CP27, CP28
 *
 * Los localizadores y los mensajes esperados fueron verificados contra el HTML y el comportamiento
 * REAL de www.maderasmym.cl (carpetas "explorar" y "explorar2" del repositorio). Los resultados
 * se registran con Utility.resultado(...) (PASA / NO PASA) o Utility.noEjecutado(...) cuando el
 * escenario no se puede ejecutar de forma responsable (ver el motivo en cada caso).
 */
public class PasosFichaProducto {

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


	private String tituloSeleccionado = "";

	private void abrirPrimeraFicha() {
		Sitio.abrirInicio(driver);
		WebElement titulo = driver.findElement(By.cssSelector("#catalog-grid .product-card .title"));
		tituloSeleccionado = titulo.getText().trim();
		Sitio.clic(driver, titulo);
		Sitio.esperarHasta(() -> Sitio.visible(driver, "#product-detail-modal .product-modal-content"), 8);
	}

	private boolean fichaAbierta() {
		return Sitio.esperarHasta(() -> Sitio.visible(driver, "#product-detail-modal .product-modal-content"), 8);
	}

	@Given("que un usuario selecciona un producto desde el listado")
	public void que_un_usuario_selecciona_un_producto_desde_el_listado() {
		Sitio.abrirInicio(driver);
		WebElement titulo = driver.findElement(By.cssSelector("#catalog-grid .product-card .title"));
		tituloSeleccionado = titulo.getText().trim();
		Sitio.clic(driver, titulo);
	}

	@When("accede a su ficha")
	public void accede_a_su_ficha() {
		fichaAbierta();
	}

	@Then("el sistema muestra tipo, medidas, dimensiones y disponibilidad del producto")
	public void el_sistema_muestra_tipo_medidas_dimensiones_y_disponibilidad() {
		String tipo = Sitio.texto(driver, "#modal-product-type");
		String medidas = Sitio.texto(driver, "#modal-product-measures");
		String dims = Sitio.texto(driver, "#modal-product-dims");
		String stock = Sitio.texto(driver, "#modal-product-stock");
		if (!tipo.isEmpty() && !medidas.isEmpty() && !dims.isEmpty() && !stock.isEmpty()) {
			Utility.resultado("PASA - Ficha \"" + tituloSeleccionado + "\": tipo=" + tipo + ", medidas=" + medidas
					+ ", dimensiones=" + dims + ", disponibilidad=" + stock);
		} else {
			Utility.resultado("NO PASA - Ficha incompleta: tipo=\"" + tipo + "\", medidas=\"" + medidas + "\", dimensiones=\""
					+ dims + "\", disponibilidad=\"" + stock + "\"");
		}
	}

	// ---- CP26: producto sin stock (se busca en el catalogo uno con disponibilidad 0)
	@Given("que un usuario accede a la ficha de un producto sin existencias")
	public void que_un_usuario_accede_a_la_ficha_de_un_producto_sin_existencias() {
		Sitio.abrirInicio(driver);
		driver.manage().timeouts().setScriptTimeout(60, TimeUnit.SECONDS);
		Object id = ((org.openqa.selenium.JavascriptExecutor) driver).executeAsyncScript(
				"var cb=arguments[arguments.length-1];"
				+ "function pag(n){return fetch('/productos?limit=100&page='+n).then(function(r){return r.json();});}"
				+ "pag(1).then(function(d){var tp=Math.min(Number(d.totalPages)||1,50);var ps=[Promise.resolve(d)];"
				+ "for(var i=2;i<=tp;i++){ps.push(pag(i));}return Promise.all(ps);})"
				+ ".then(function(arr){var l=[];arr.forEach(function(d){l=l.concat(Array.isArray(d)?d:(d.productos||[]));});"
				+ "var p=l.filter(function(x){return (x.visible===1||x.visible===true||x.visible==='1')&&Number(x.disponibilidad)===0;})[0];"
				+ "cb(p?String(p.id_producto):'');}).catch(function(e){cb('ERROR '+e);});");
		String idProducto = String.valueOf(id);
		if (idProducto.isEmpty() || idProducto.startsWith("ERROR") || "null".equals(idProducto)) {
			Utility.noEjecutado("el catalogo publicado no tiene en este momento ningun producto con disponibilidad 0 (" + idProducto + ")");
		}
		Sitio.js(driver, "openProductModal(" + Long.parseLong(idProducto) + ");");
		fichaAbierta();
	}

	@When("revisa el campo {string}")
	public void revisa_el_campo(String campo) {
		Sitio.pausa(500);
	}

	@Then("el sistema indica claramente que el producto no tiene stock")
	public void el_sistema_indica_claramente_que_el_producto_no_tiene_stock() {
		String stock = Sitio.texto(driver, "#modal-product-stock");
		String lower = stock.toLowerCase();
		if (lower.contains("sin stock") || lower.contains("agotado") || lower.contains("no disponible") || lower.contains("sin existencias")) {
			Utility.resultado("PASA - Producto sin stock: disponibilidad = \"" + stock + "\"");
		} else {
			Utility.resultado("NO PASA - Producto sin stock: el campo Disponibilidad muestra \"" + stock
					+ "\" y no una leyenda clara (p. ej. \"Sin stock\")");
		}
	}

	// ---- CP27: desde la busqueda
	@Given("que un usuario realizó una búsqueda exitosa de {string}")
	public void que_un_usuario_realizo_una_busqueda_exitosa_de(String texto) {
		Sitio.abrirInicio(driver);
		WebElement campo = driver.findElement(By.id("search-input"));
		campo.clear();
		campo.sendKeys(texto);
		Sitio.clic(driver, driver.findElement(By.cssSelector("#search-form .search-btn")));
		Sitio.pausa(2500);
	}

	@When("selecciona un producto del listado de resultados")
	public void selecciona_un_producto_del_listado_de_resultados() {
		WebElement titulo = driver.findElement(By.cssSelector("#catalog-grid .product-card .title"));
		tituloSeleccionado = titulo.getText().trim();
		Sitio.clic(driver, titulo);
	}

	@Then("el sistema lo redirige correctamente a la ficha del producto seleccionado")
	public void el_sistema_lo_redirige_correctamente_a_la_ficha() {
		boolean abierta = fichaAbierta();
		String titulo = Sitio.texto(driver, "#modal-product-title");
		if (abierta && titulo.equalsIgnoreCase(tituloSeleccionado)) {
			Utility.resultado("PASA - La ficha (ventana de detalle) corresponde al producto seleccionado: \"" + titulo + "\"");
		} else {
			Utility.resultado("NO PASA - Ficha abierta = " + abierta + ", titulo de la ficha = \"" + titulo
					+ "\", producto seleccionado = \"" + tituloSeleccionado + "\"");
		}
	}

	// ---- CP28: imagenes
	@Given("que un usuario está en la ficha de un producto")
	public void que_un_usuario_esta_en_la_ficha_de_un_producto() {
		abrirPrimeraFicha();
	}

	@When("revisa la sección de imágenes")
	public void revisa_la_seccion_de_imagenes() {
		Sitio.esperarHasta(() -> Boolean.TRUE.equals(Sitio.js(driver,
				"var i=document.getElementById('modal-product-img');return !!(i&&i.complete&&i.naturalWidth>0);")), 10);
	}

	@Then("el sistema despliega correctamente las imágenes del producto sin errores de carga")
	public void el_sistema_despliega_correctamente_las_imagenes() {
		boolean cargada = Boolean.TRUE.equals(Sitio.js(driver,
				"var i=document.getElementById('modal-product-img');return !!(i&&i.complete&&i.naturalWidth>0);"));
		String src = String.valueOf(Sitio.js(driver, "return document.getElementById('modal-product-img').getAttribute('src');"));
		if (cargada && !src.contains("placeholder")) {
			Utility.resultado("PASA - La imagen de la ficha se cargo correctamente (" + src + ")");
		} else {
			Utility.resultado("NO PASA - La imagen de la ficha no se cargo (cargada = " + cargada + ", src = " + src + ")");
		}
	}
}
