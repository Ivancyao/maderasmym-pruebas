package Ejecutador;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Ejecutador de la funcionalidad 15_MenuResponsivo.
 * Cada funcionalidad tiene su propio paquete de definicion de pasos (glue), de modo que
 * pasos con el mismo texto en distintas funcionalidades no entren en conflicto.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"./src/test/resources/Features/15_MenuResponsivo.feature"},
		glue = {"DefinicionPasos.menumovil"},
		plugin = {"pretty",
				"html:target/cucumber-reports/15_MenuResponsivo.html",
				"json:target/cucumber-json/15_MenuResponsivo.json",
				"junit:target/cucumber-results/15_MenuResponsivo.xml"},
		monochrome = true)
public class Runner15MenuResponsivo {

}
