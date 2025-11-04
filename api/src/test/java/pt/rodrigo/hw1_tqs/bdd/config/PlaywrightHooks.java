package pt.rodrigo.hw1_tqs.bdd.config;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaywrightHooks {
    private static final Logger log = LoggerFactory.getLogger(PlaywrightHooks.class);

    @Before
    public void setUp() {
        log.info("Initializing Playwright browser");
        PlaywrightContext.initialize();
    }

    @After
    public void tearDown() {
        log.info("Closing Playwright browser context");
        PlaywrightContext.close();
    }

    @AfterAll
    public static void tearDownAll() {
        log.info("Closing all Playwright resources");
        PlaywrightContext.closeAll();
    }
}
