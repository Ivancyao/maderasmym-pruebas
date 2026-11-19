package Ejecutador;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Ejecutador de la funcionalidad 14_SobreNosotros.
 * Cada funcionalidad tiene su propio paquete de definicion de pasos (glue), de modo que
 * pasos con el mismo texto en distintas funcionalidades no entren en conflicto.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"./src/test/resources/Features/14_SobreNosotros.feature"},
		glue = {"DefinicionPasos.sobrenosotros"},
		plugin = {"pretty",
				"html:target/cucumber-reports/14_SobreNosotros.html",
				"json:target/cucumber-json/14_SobreNosotros.json",
				"junit:target/cucumber-results/14_SobreNosotros.xml"},
		monochrome = true)
public class Runner14SobreNosotros {

}
