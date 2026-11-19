package Ejecutador;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Ejecutador de la funcionalidad 09_CarritoGestion.
 * Cada funcionalidad tiene su propio paquete de definicion de pasos (glue), de modo que
 * pasos con el mismo texto en distintas funcionalidades no entren en conflicto.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"./src/test/resources/Features/09_CarritoGestion.feature"},
		glue = {"DefinicionPasos.carritogestion"},
		plugin = {"pretty",
				"html:target/cucumber-reports/09_CarritoGestion.html",
				"json:target/cucumber-json/09_CarritoGestion.json",
				"junit:target/cucumber-results/09_CarritoGestion.xml"},
		monochrome = true)
public class Runner09CarritoGestion {

}
