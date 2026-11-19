package Ejecutador;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Ejecutador de la funcionalidad 02_InicioSesion.
 * Cada funcionalidad tiene su propio paquete de definicion de pasos (glue), de modo que
 * pasos con el mismo texto en distintas funcionalidades no entren en conflicto.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"./src/test/resources/Features/02_InicioSesion.feature"},
		glue = {"DefinicionPasos.iniciosesion"},
		plugin = {"pretty",
				"html:target/cucumber-reports/02_InicioSesion.html",
				"json:target/cucumber-json/02_InicioSesion.json",
				"junit:target/cucumber-results/02_InicioSesion.xml"},
		monochrome = true)
public class Runner02InicioSesion {

}
