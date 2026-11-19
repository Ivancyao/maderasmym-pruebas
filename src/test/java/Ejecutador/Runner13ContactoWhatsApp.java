package Ejecutador;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

/**
 * Ejecutador de la funcionalidad 13_ContactoWhatsApp.
 * Cada funcionalidad tiene su propio paquete de definicion de pasos (glue), de modo que
 * pasos con el mismo texto en distintas funcionalidades no entren en conflicto.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
		features = {"./src/test/resources/Features/13_ContactoWhatsApp.feature"},
		glue = {"DefinicionPasos.whatsapp"},
		plugin = {"pretty",
				"html:target/cucumber-reports/13_ContactoWhatsApp.html",
				"json:target/cucumber-json/13_ContactoWhatsApp.json",
				"junit:target/cucumber-results/13_ContactoWhatsApp.xml"},
		monochrome = true)
public class Runner13ContactoWhatsApp {

}
