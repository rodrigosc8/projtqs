package pt.rodrigo.hw1_tqs.bdd.pages;

import com.microsoft.playwright.Page;

public class SearchPage {
    private final Page page;

    public SearchPage(Page page) {
        this.page = page;
    }

    public void enterToken(String token) {
        page.fill("input[name='token']", token);
    }

    public void clickSearchButton() {
        page.click("button[type='submit']");
        page.waitForTimeout(1500);
    }

    public boolean isBookingDisplayed() {
        return page.isVisible(".booking-result");
    }

    public String getBookingInfo() {
        if (isBookingDisplayed()) {
            return page.textContent(".booking-result");
        }
        return "";
    }

    public boolean containsText(String text) {
        String content = page.textContent("body");
        return content != null && content.contains(text);
    }

    public boolean hasError() {
        return page.isVisible("div[style*='color: red'], .error-message");
    }
}
