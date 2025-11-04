package pt.rodrigo.hw1_tqs.bdd.pages;

import com.microsoft.playwright.Page;

public class HomePage {
    private final Page page;

    public HomePage(Page page) {
        this.page = page;
    }

    public void navigate() {
        page.navigate("http://localhost:5173/");
    }

    public void clickCitizenLink() {
        page.click("a[href='/citizen']");
    }

    public void clickStaffLink() {
        page.click("a[href='/staff']");
    }

    public void clickSearchLink() {
        page.click("a[href='/search']");
    }

    public String getCurrentUrl() {
        return page.url();
    }
}
