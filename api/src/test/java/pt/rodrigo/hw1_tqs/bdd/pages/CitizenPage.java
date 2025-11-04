package pt.rodrigo.hw1_tqs.bdd.pages;

import com.microsoft.playwright.Page;

public class CitizenPage {
    private final Page page;

    public CitizenPage(Page page) {
        this.page = page;
    }

    public void selectMunicipality(String municipality) {
        page.waitForSelector("select[name='municipality']");
        page.waitForTimeout(2000); // Aguarda 2s para carregar options
        page.selectOption("select[name='municipality']", municipality);
    }

    public void enterDate(String date) {
        page.fill("input[name='date']", date);
    }

    public void selectTimeslot(String timeslot) {
        page.selectOption("select[name='timeslot']", timeslot);
    }

    public void enterDescription(String description) {
        page.fill("textarea[name='description']", description);
    }

    public void submitForm() {
        page.click("button[type='submit']");
    }

    public boolean hasError() {
        return page.isVisible("div[style*='color: red']");
    }

    public String getErrorMessage() {
        if (hasError()) {
            return page.textContent("div[style*='color: red']");
        }
        return "";
    }

    public void waitForResponse() {
        page.waitForTimeout(2000);
    }
}
