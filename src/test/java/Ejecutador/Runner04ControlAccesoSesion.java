package Ejecutador;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Ejecutador de la funcionalidad 04_ControlAccesoSesion.
 * Cada funcionalidad tiene su propio paquete de definicion de pasos (glue), de modo que
 * pasos con el mismo texto en distintas funcionalidades no entren en conflicto.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"./src/test/resources/Features/04_ControlAccesoSesion.feature"},
		glue = {"DefinicionPasos.controlacceso"},
		plugin = {"pretty",
				"html:target/cucumber-reports/04_ControlAccesoSesion.html",
				"json:target/cucumber-json/04_ControlAccesoSesion.json",
				"junit:target/cucumber-results/04_ControlAccesoSesion.xml"},
		monochrome = true)
public class Runner04ControlAccesoSesion {

}
