package Ejecutador;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Ejecutador de la funcionalidad 03_RecuperacionContrasena.
 * Cada funcionalidad tiene su propio paquete de definicion de pasos (glue), de modo que
 * pasos con el mismo texto en distintas funcionalidades no entren en conflicto.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"./src/test/resources/Features/03_RecuperacionContrasena.feature"},
		glue = {"DefinicionPasos.recuperacion"},
		plugin = {"pretty",
				"html:target/cucumber-reports/03_RecuperacionContrasena.html",
				"json:target/cucumber-json/03_RecuperacionContrasena.json",
				"junit:target/cucumber-results/03_RecuperacionContrasena.xml"},
		monochrome = true)
public class Runner03RecuperacionContrasena {

}
