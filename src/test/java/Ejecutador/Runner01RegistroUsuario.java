package Ejecutador;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Ejecutador de la funcionalidad 01_RegistroUsuario.
 * Cada funcionalidad tiene su propio paquete de definicion de pasos (glue), de modo que
 * pasos con el mismo texto en distintas funcionalidades no entren en conflicto.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"./src/test/resources/Features/01_RegistroUsuario.feature"},
		glue = {"DefinicionPasos.registro"},
		plugin = {"pretty",
				"html:target/cucumber-reports/01_RegistroUsuario.html",
				"json:target/cucumber-json/01_RegistroUsuario.json",
				"junit:target/cucumber-results/01_RegistroUsuario.xml"},
		monochrome = true)
public class Runner01RegistroUsuario {

}
