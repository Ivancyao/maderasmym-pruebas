package Ejecutador;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Ejecutador de la funcionalidad 07_FichaProducto.
 * Cada funcionalidad tiene su propio paquete de definicion de pasos (glue), de modo que
 * pasos con el mismo texto en distintas funcionalidades no entren en conflicto.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"./src/test/resources/Features/07_FichaProducto.feature"},
		glue = {"DefinicionPasos.ficha"},
		plugin = {"pretty",
				"html:target/cucumber-reports/07_FichaProducto.html",
				"json:target/cucumber-json/07_FichaProducto.json",
				"junit:target/cucumber-results/07_FichaProducto.xml"},
		monochrome = true)
public class Runner07FichaProducto {

}
