package Ejecutador;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Ejecutador de la funcionalidad 06_NavegacionCategorias.
 * Cada funcionalidad tiene su propio paquete de definicion de pasos (glue), de modo que
 * pasos con el mismo texto en distintas funcionalidades no entren en conflicto.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"./src/test/resources/Features/06_NavegacionCategorias.feature"},
		glue = {"DefinicionPasos.categorias"},
		plugin = {"pretty",
				"html:target/cucumber-reports/06_NavegacionCategorias.html",
				"json:target/cucumber-json/06_NavegacionCategorias.json",
				"junit:target/cucumber-results/06_NavegacionCategorias.xml"},
		monochrome = true)
public class Runner06NavegacionCategorias {

}
