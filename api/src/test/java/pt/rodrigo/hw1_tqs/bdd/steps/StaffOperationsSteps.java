package pt.rodrigo.hw1_tqs.bdd.steps;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.RequestOptions;

import io.cucumber.java.en.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.rodrigo.hw1_tqs.bdd.config.PlaywrightContext;
import pt.rodrigo.hw1_tqs.bdd.pages.HomePage;
import pt.rodrigo.hw1_tqs.bdd.pages.StaffPage;

import static org.junit.jupiter.api.Assertions.*;

public class StaffOperationsSteps {
    private static final Logger log = LoggerFactory.getLogger(StaffOperationsSteps.class);
    private Page page;
    private HomePage homePage;
    private StaffPage staffPage;

    @Given("test bookings exist for filtering")
    public void testBookingsExistForFiltering() {
        page = PlaywrightContext.getPage();
        
        try {
            // Cria 2 bookings com status RECEIVED (é o que o backend usa)
            String jsonBody1 = "{\"municipality\":\"Aveiro\",\"date\":\"2026-12-12\",\"timeslot\":\"Manhã\",\"description\":\"Test Aveiro\"}";
            String response1 = page.request().post("http://localhost:8080/api/bookings",
                RequestOptions.create()
                    .setHeader("Content-Type", "application/json")
                    .setData(jsonBody1)
            ).text();
            log.info("Created test booking 1: Aveiro");
            
            String jsonBody2 = "{\"municipality\":\"Porto\",\"date\":\"2026-12-13\",\"timeslot\":\"Tarde\",\"description\":\"Test Porto\"}";
            String response2 = page.request().post("http://localhost:8080/api/bookings",
                RequestOptions.create()
                    .setHeader("Content-Type", "application/json")
                    .setData(jsonBody2)
            ).text();
            log.info("Created test booking 2: Porto");
            
        } catch (Exception e) {
            log.error("Failed to create test bookings: {}", e.getMessage());
            throw new RuntimeException("Failed to setup test bookings", e);
        }
    }

    @Given("a staff member is on the homepage")
    public void staffMemberOnHomepage() {
        page = PlaywrightContext.getPage();  
        homePage = new HomePage(page);
        homePage.navigate();
        log.info("Staff member on homepage");
    }

    @When("the staff navigates to the staff page")
    public void navigateToStaffPage() {
        homePage.clickStaffLink();
        staffPage = new StaffPage(page);
        assertTrue(page.url().contains("/staff"));
        log.info("Navigated to staff page");
    }

    @And("the staff selects municipality filter {string}")
    public void selectMunicipalityFilter(String municipality) {
        staffPage.selectMunicipalityFilter(municipality);
        log.info("Selected municipality filter: {}", municipality);
    }

    @And("the staff clicks the filter button")
    public void clickFilterButton() {
        staffPage.clickFilterButton();
        log.info("Clicked filter button");
    }

    @Then("only bookings from {string} are displayed")
    public void onlyBookingsFromMunicipalityDisplayed(String municipality) {
        assertTrue(staffPage.isTableVisible(), "Table should be visible");
        assertTrue(staffPage.tableContainsMunicipality(municipality));
        log.info("Only bookings from {} displayed", municipality);
    }

    @And("the staff selects status filter {string}")
    public void selectStatusFilter(String status) {
        staffPage.selectStatusFilter(status);
        log.info("Selected status filter: {}", status);
    }

    @Then("only bookings with status {string} are displayed")
    public void onlyBookingsWithStatusDisplayed(String status) {
        assertTrue(staffPage.isTableVisible(), "Table should be visible");
        assertTrue(staffPage.tableContainsStatus(status));
        log.info("Only bookings with status {} displayed", status);
    }
}
