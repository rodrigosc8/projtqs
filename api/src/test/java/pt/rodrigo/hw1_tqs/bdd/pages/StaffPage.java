package pt.rodrigo.hw1_tqs.bdd.pages;

import com.microsoft.playwright.Page;

public class StaffPage {
    private final Page page;

    public StaffPage(Page page) {
        this.page = page;
    }

    public void selectMunicipalityFilter(String municipality) {
        page.waitForSelector("select[name='municipality']");
        page.waitForTimeout(2000); // Aguarda carregar municípios
        page.selectOption("select[name='municipality']", municipality);
    }

    public void selectStatusFilter(String status) {
        page.selectOption("select[name='status']", status);
    }

    public void enterDateFilter(String date) {
        page.fill("input[name='date']", date);
    }

    public void clickFilterButton() {
        page.click("button:has-text('Filtrar')");
        page.waitForTimeout(1500);
    }

    public boolean isTableVisible() {
        return page.isVisible("table");
    }

    public int getBookingCount() {
        try {
            return page.locator("table tbody tr").count();
        } catch (Exception e) {
            return 0;
        }
    }

    public void clickBookingByIndex(int index) {
        page.locator("table tbody tr").nth(index).click();
    }

    public boolean tableContainsMunicipality(String municipality) {
        String tableText = page.textContent("table");
        return tableText != null && tableText.contains(municipality);
    }

    public boolean tableContainsStatus(String status) {
        String tableText = page.textContent("table");
        return tableText != null && tableText.contains(status);
    }
}
