package pt.rodrigo.hw1_tqs.bdd.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ComponentScan("pt.rodrigo.hw1_tqs.bdd") 
public class CucumberSpringConfiguration {
}
