package cat.albirar.daw.cocktails;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import cat.albirar.daw.cocktails.controllers.MainController;
import cat.albirar.daw.cocktails.service.impl.CocktailsServiceImpl;

@SpringBootApplication
@ComponentScan(basePackageClasses = {MainController.class, CocktailsServiceImpl.class})
public class CocktailsApplication {

	public static void main(String[] args) {
		SpringApplication.run(CocktailsApplication.class, args);
	}

}
