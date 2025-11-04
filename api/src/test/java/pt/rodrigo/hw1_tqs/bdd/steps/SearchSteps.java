package pt.rodrigo.hw1_tqs.bdd.steps;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.RequestOptions;
import io.cucumber.java.en.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.rodrigo.hw1_tqs.bdd.config.PlaywrightContext;
import pt.rodrigo.hw1_tqs.bdd.pages.HomePage;
import pt.rodrigo.hw1_tqs.bdd.pages.SearchPage;

import static org.junit.jupiter.api.Assertions.*;

public class SearchSteps {
    private static final Logger log = LoggerFactory.getLogger(SearchSteps.class);
    private Page page;
    private HomePage homePage;
    private SearchPage searchPage;
    private String createdToken; 

    @Given("a booking exists with token {string}")
    public void bookingExistsWithToken(String requestedToken) {
        page = PlaywrightContext.getPage();
        
        try {
            String jsonBody = "{\"municipality\":\"Aveiro\",\"date\":\"2026-12-12\",\"timeslot\":\"Manhã\",\"description\":\"Test booking\"}";
            
            String response = page.request().post("http://localhost:8080/api/bookings",
                RequestOptions.create()
                    .setHeader("Content-Type", "application/json")
                    .setData(jsonBody)
            ).text();
            
            // Extrai token da resposta
            createdToken = response.split("\"token\":\"")[1].split("\"")[0];
            log.info("Created booking with token: {}", createdToken);
            
        } catch (Exception e) {
            log.error("Failed to create booking: {}", e.getMessage());
            throw new RuntimeException("Failed to setup test booking", e);
        }
    }

    @Given("a user is on the homepage")
    public void userOnHomepage() {
        page = PlaywrightContext.getPage();
        homePage = new HomePage(page);
        homePage.navigate();
        log.info("User on homepage");
    }

    @When("the user navigates to the search page")
    public void navigateToSearchPage() {
        page.navigate("http://localhost:5173/search");
        searchPage = new SearchPage(page);
        assertTrue(page.url().contains("/search"));
        log.info("Navigated to search page");
    }

    @And("the user enters token {string}")
    public void enterToken(String token) {
        String actualToken = "ABC123".equals(token) ? createdToken : token;
        searchPage.enterToken(actualToken);
        log.info("Entered token: {}", actualToken);
    }

    @When("the user clicks the search button")
    public void clickSearchButton() {
        searchPage.clickSearchButton();
        log.info("Clicked search button");
    }

    @Then("the booking details are displayed")
    public void bookingDetailsDisplayed() {
        assertTrue(searchPage.isBookingDisplayed(), "Booking details should be displayed");
        log.info("Booking details displayed");
    }

    @And("the details contain {string}")
    public void detailsContain(String text) {
        String actualText = "ABC123".equals(text) ? createdToken : text;
        assertTrue(searchPage.containsText(actualText), "Details should contain: " + actualText);
        log.info("Details contain: {}", actualText);
    }

    @Then("an error message is shown")
    public void errorMessageShown() {
        assertTrue(searchPage.hasError(), "Error message should be displayed");
        log.info("Error message displayed");
    }
}
