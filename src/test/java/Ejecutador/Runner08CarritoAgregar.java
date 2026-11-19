package Ejecutador;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Ejecutador de la funcionalidad 08_CarritoAgregar.
 * Cada funcionalidad tiene su propio paquete de definicion de pasos (glue), de modo que
 * pasos con el mismo texto en distintas funcionalidades no entren en conflicto.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"./src/test/resources/Features/08_CarritoAgregar.feature"},
		glue = {"DefinicionPasos.carritoagregar"},
		plugin = {"pretty",
				"html:target/cucumber-reports/08_CarritoAgregar.html",
				"json:target/cucumber-json/08_CarritoAgregar.json",
				"junit:target/cucumber-results/08_CarritoAgregar.xml"},
		monochrome = true)
public class Runner08CarritoAgregar {

}
