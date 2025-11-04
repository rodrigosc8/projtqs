package pt.rodrigo.hw1_tqs.bdd.config;

import com.microsoft.playwright.*;

public class PlaywrightContext {
    private static Playwright playwright;
    private static Browser browser;
    private static BrowserContext context;
    private static Page page;

    public static void initialize() {
        if (playwright == null) {
            playwright = Playwright.create();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                    .setHeadless(true));
        }
        context = browser.newContext();
        page = context.newPage();
    }

    public static Page getPage() {
        return page;
    }

    public static void close() {
        if (context != null) {
            context.close();
        }
    }

    public static void closeAll() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
