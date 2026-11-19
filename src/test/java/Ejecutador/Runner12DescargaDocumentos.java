package Ejecutador;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Ejecutador de la funcionalidad 12_DescargaDocumentos.
 * Cada funcionalidad tiene su propio paquete de definicion de pasos (glue), de modo que
 * pasos con el mismo texto en distintas funcionalidades no entren en conflicto.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"./src/test/resources/Features/12_DescargaDocumentos.feature"},
		glue = {"DefinicionPasos.descargas"},
		plugin = {"pretty",
				"html:target/cucumber-reports/12_DescargaDocumentos.html",
				"json:target/cucumber-json/12_DescargaDocumentos.json",
				"junit:target/cucumber-results/12_DescargaDocumentos.xml"},
		monochrome = true)
public class Runner12DescargaDocumentos {

}
