package Ejecutador;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Ejecutador de la funcionalidad 10_SeleccionEntrega.
 * Cada funcionalidad tiene su propio paquete de definicion de pasos (glue), de modo que
 * pasos con el mismo texto en distintas funcionalidades no entren en conflicto.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"./src/test/resources/Features/10_SeleccionEntrega.feature"},
		glue = {"DefinicionPasos.entrega"},
		plugin = {"pretty",
				"html:target/cucumber-reports/10_SeleccionEntrega.html",
				"json:target/cucumber-json/10_SeleccionEntrega.json",
				"junit:target/cucumber-results/10_SeleccionEntrega.xml"},
		monochrome = true)
public class Runner10SeleccionEntrega {

}
