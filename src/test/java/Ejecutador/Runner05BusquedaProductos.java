package Ejecutador;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Ejecutador de la funcionalidad 05_BusquedaProductos.
 * Cada funcionalidad tiene su propio paquete de definicion de pasos (glue), de modo que
 * pasos con el mismo texto en distintas funcionalidades no entren en conflicto.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"./src/test/resources/Features/05_BusquedaProductos.feature"},
		glue = {"DefinicionPasos.busqueda"},
		plugin = {"pretty",
				"html:target/cucumber-reports/05_BusquedaProductos.html",
				"json:target/cucumber-json/05_BusquedaProductos.json",
				"junit:target/cucumber-results/05_BusquedaProductos.xml"},
		monochrome = true)
public class Runner05BusquedaProductos {

}
