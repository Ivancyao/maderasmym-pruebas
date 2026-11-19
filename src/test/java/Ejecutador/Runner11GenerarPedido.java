package Ejecutador;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Ejecutador de la funcionalidad 11_GenerarPedido.
 * Cada funcionalidad tiene su propio paquete de definicion de pasos (glue), de modo que
 * pasos con el mismo texto en distintas funcionalidades no entren en conflicto.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"./src/test/resources/Features/11_GenerarPedido.feature"},
		glue = {"DefinicionPasos.pedido"},
		plugin = {"pretty",
				"html:target/cucumber-reports/11_GenerarPedido.html",
				"json:target/cucumber-json/11_GenerarPedido.json",
				"junit:target/cucumber-results/11_GenerarPedido.xml"},
		monochrome = true)
public class Runner11GenerarPedido {

}
