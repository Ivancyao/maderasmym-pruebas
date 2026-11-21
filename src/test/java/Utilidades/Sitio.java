package Utilidades;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.UnhandledAlertException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Funciones de apoyo para trabajar con el sitio www.maderasmym.cl.
 *
 * Los localizadores de esta clase fueron tomados del HTML REAL del sitio (exploracion del
 * 20-11-2026 guardada en las carpetas "explorar" y "explorar2" del repositorio), no son supuestos:
 *   buscador  #search-input / .search-btn          catalogo  #catalog-grid .product-card
 *   boton agregar .add-btn                         contador  .cart-count
 *   carrito   #cart-link, #cartModal               total     #cart-total
 *   entrega   #delivery-shipping / #delivery-pickup, comentarios #order-comments
 */
public final class Sitio {

	public static final String BASE = "https://www.maderasmym.cl";

	/** Texto del ultimo alert()/confirm() del sitio que fue leido y cerrado (null si no hubo). */
	public static String ultimaAlerta = null;

	private Sitio() {
	}

	// ------------------------------------------------------------------ utilidades generales

	public static Object js(WebDriver d, String script, Object... args) {
		return ((JavascriptExecutor) d).executeScript(script, args);
	}

	public static void pausa(long milisegundos) {
		try {
			Thread.sleep(milisegundos);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/** Espera (sondeando cada 250 ms) hasta que la condicion se cumpla o pase el tiempo. */
	public static boolean esperarHasta(BooleanSupplier condicion, int segundos) {
		long limite = System.currentTimeMillis() + segundos * 1000L;
		while (System.currentTimeMillis() < limite) {
			try {
				if (condicion.getAsBoolean()) {
					return true;
				}
			} catch (RuntimeException e) {
				// el DOM puede estar cambiando: se reintenta
			}
			pausa(250);
		}
		try {
			return condicion.getAsBoolean();
		} catch (RuntimeException e) {
			return false;
		}
	}

	/** Deja solo los digitos de un texto ("$24.526" -> 24526). */
	public static long numero(String texto) {
		String digitos = texto == null ? "" : texto.replaceAll("[^0-9]", "");
		return digitos.isEmpty() ? 0L : Long.parseLong(digitos);
	}

	public static String texto(WebDriver d, String css) {
		Object r = js(d, "var e=document.querySelector(arguments[0]);return e?(e.textContent||'').replace(/\\s+/g,' ').trim():'';", css);
		return r == null ? "" : r.toString();
	}

	public static boolean visible(WebDriver d, String css) {
		Object r = js(d, "var e=document.querySelector(arguments[0]);return !!(e&&(e.offsetWidth||e.offsetHeight||e.getClientRects().length));", css);
		return Boolean.TRUE.equals(r);
	}

	/** Cierra la pestana/ventana adicional (si la hay) y vuelve a la original. */
	public static void volverALaOriginal(WebDriver d, String original) {
		for (String h : new ArrayList<String>(d.getWindowHandles())) {
			if (!h.equals(original)) {
				d.switchTo().window(h);
				d.close();
			}
		}
		d.switchTo().window(original);
	}

	// ------------------------------------------------------------------ pagina de inicio

	public static void abrirInicio(WebDriver d) {
		d.get(BASE + "/");
		esperarHasta(() -> !d.findElements(By.cssSelector("#catalog-grid .product-card, #catalog-grid p")).isEmpty(), 25);
		cerrarAviso(d);
	}

	/** Cierra el aviso emergente de feriados si estuviera visible (hoy esta oculto). */
	public static void cerrarAviso(WebDriver d) {
		if (visible(d, "#holiday-ok")) {
			js(d, "document.getElementById('holiday-ok').click();");
			pausa(500);
		}
	}

	public static int tarjetas(WebDriver d) {
		return d.findElements(By.cssSelector("#catalog-grid .product-card")).size();
	}

	public static void clic(WebDriver d, WebElement e) {
		js(d, "arguments[0].scrollIntoView({block:'center'});", e);
		pausa(200);
		e.click();
	}

	/** Clic mediante JavaScript, recogiendo un alert() si el sitio lo muestra. */
	public static void clicJs(WebDriver d, WebElement e) {
		try {
			js(d, "arguments[0].click();", e);
		} catch (UnhandledAlertException ex) {
			leerAlerta(d, 2);
		}
	}

	// ------------------------------------------------------------------ alertas

	/** Espera un alert()/confirm() del sitio; devuelve su texto (y lo cierra) o null si no aparece. */
	public static String leerAlerta(WebDriver d, int segundos) {
		long limite = System.currentTimeMillis() + segundos * 1000L;
		while (System.currentTimeMillis() < limite) {
			try {
				Alert a = d.switchTo().alert();
				String t = a.getText();
				a.dismiss();
				ultimaAlerta = t;
				return t;
			} catch (NoAlertPresentException e) {
				pausa(200);
			}
		}
		return null;
	}

	// ------------------------------------------------------------------ carrito

	public static int contadorCarrito(WebDriver d) {
		return (int) numero(texto(d, ".cart-count"));
	}

	/** Agrega "n" productos distintos desde el listado (primer boton "Agregar al carrito" disponible). */
	public static void agregarProductos(WebDriver d, int n) {
		for (int i = 0; i < n; i++) {
			int antes = contadorCarrito(d);
			List<WebElement> botones = d.findElements(By.cssSelector("#catalog-grid .product-card .add-btn"));
			if (botones.isEmpty()) {
				throw new AssertionError("NO PASA - No hay botones \"Agregar al carrito\" disponibles en el listado");
			}
			clic(d, botones.get(0));
			final int esperado = antes + 1;
			esperarHasta(() -> contadorCarrito(d) >= esperado, 8);
		}
	}

	public static void abrirCarrito(WebDriver d) {
		clic(d, d.findElement(By.id("cart-link")));
		esperarHasta(() -> visible(d, "#cartModal .modal-content"), 8);
	}

	public static long totalCarrito(WebDriver d) {
		return numero(texto(d, "#cart-total"));
	}

	public static int lineasCarrito(WebDriver d) {
		return d.findElements(By.cssSelector("#cartModal .cart-item")).size();
	}

	/** Total esperado = suma de (precio x cantidad) de las lineas que muestra el carrito. */
	public static long totalCalculado(WebDriver d) {
		long suma = 0;
		for (WebElement linea : d.findElements(By.cssSelector("#cartModal .cart-item"))) {
			long precio = numero(linea.findElement(By.cssSelector(".cart-item-info p")).getText());
			long cantidad = numero(linea.findElement(By.cssSelector(".cart-quantity span")).getText());
			suma += precio * cantidad;
		}
		return suma;
	}

	public static long cantidadDeLinea(WebDriver d, int indice) {
		List<WebElement> lineas = d.findElements(By.cssSelector("#cartModal .cart-item"));
		return numero(lineas.get(indice).findElement(By.cssSelector(".cart-quantity span")).getText());
	}

	/** Pulsa "+" de una linea del carrito hasta llegar a la cantidad pedida. */
	public static void ponerCantidadEnCarrito(WebDriver d, int indice, int cantidad) {
		int intentos = 0;
		while (cantidadDeLinea(d, indice) < cantidad && intentos++ < cantidad + 5) {
			List<WebElement> lineas = d.findElements(By.cssSelector("#cartModal .cart-item"));
			clic(d, lineas.get(indice).findElement(By.cssSelector(".cart-quantity button[data-act='plus']")));
			pausa(300);
		}
	}

	// ------------------------------------------------------------------ red (comprobacion de enlaces)

	/** Pide la URL desde el navegador y devuelve "codigo tipo-de-contenido" (o "ERROR ..."). */
	public static String estadoHttp(WebDriver d, String url) {
		d.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
		Object r = ((JavascriptExecutor) d).executeAsyncScript(
				"var cb=arguments[arguments.length-1];"
						+ "fetch(arguments[0]).then(function(r){cb(r.status+' '+r.headers.get('content-type'));})"
						+ ".catch(function(e){cb('ERROR '+e);});",
				url);
		return String.valueOf(r);
	}
}
