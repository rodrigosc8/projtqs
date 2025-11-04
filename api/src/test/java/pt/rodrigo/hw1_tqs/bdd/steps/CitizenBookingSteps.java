package pt.rodrigo.hw1_tqs.bdd.steps;

import com.microsoft.playwright.Page;
import io.cucumber.java.en.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.rodrigo.hw1_tqs.bdd.config.PlaywrightContext;
import pt.rodrigo.hw1_tqs.bdd.pages.CitizenPage;
import pt.rodrigo.hw1_tqs.bdd.pages.HomePage;

import static org.junit.jupiter.api.Assertions.*;

public class CitizenBookingSteps {
    private static final Logger log = LoggerFactory.getLogger(CitizenBookingSteps.class);
    private Page page;
    private HomePage homePage;
    private CitizenPage citizenPage;

    @Given("the citizen is on the homepage")
    public void citizenOnHomepage() {
        page = PlaywrightContext.getPage(); 
        homePage = new HomePage(page);
        homePage.navigate();
        log.info("Citizen on homepage");
    }

    @When("the citizen navigates to the citizen page")
    public void navigateToCitizenPage() {
        homePage.clickCitizenLink();
        citizenPage = new CitizenPage(page);
        assertTrue(page.url().contains("/citizen"));
        log.info("Navigated to citizen page");
    }

    @And("the citizen selects municipality {string}")
    public void selectMunicipality(String municipality) {
        citizenPage.selectMunicipality(municipality);
        log.info("Selected municipality: {}", municipality);
    }

    @And("the citizen enters date {string}")
    public void enterDate(String date) {
        citizenPage.enterDate(date);
        log.info("Entered date: {}", date);
    }

    @And("the citizen selects timeslot {string}")
    public void selectTimeslot(String timeslot) {
        citizenPage.selectTimeslot(timeslot);
        log.info("Selected timeslot: {}", timeslot);
    }

    @And("the citizen enters description {string}")
    public void enterDescription(String description) {
        citizenPage.enterDescription(description);
        log.info("Entered description: {}", description);
    }

    @When("the citizen submits the booking form")
    public void submitForm() {
        citizenPage.submitForm();
        citizenPage.waitForResponse();
        log.info("Form submitted");
    }

    @Then("the booking is created successfully")
    public void bookingCreatedSuccessfully() {
        assertFalse(citizenPage.hasError(), "Should not have error");
        log.info("Booking created successfully");
    }
}
