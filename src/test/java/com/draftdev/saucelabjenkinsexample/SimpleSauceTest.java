package com.draftdev.saucelabjenkinsexample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Platform;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.openqa.selenium.remote.CapabilityType;

/**
 * Demo tests with Selenium.
 */
public class SimpleSauceTest {

    public RemoteWebDriver driver;

    /**
     * A Test Watcher is needed to be able to get the results of a Test so that
     * it can be sent to Sauce Labs. Note that the name is never actually used
     */
    @RegisterExtension
    public SauceTestWatcher watcher = new SauceTestWatcher();

    @BeforeEach
    public void setup(TestInfo testInfo) throws MalformedURLException {
        
        
        /*
        *Set the browser and platform to be tested on using the capabilities class
        */
        DesiredCapabilities desiredCap = new DesiredCapabilities();
        desiredCap.setBrowserName(System.getenv("SELENIUM_BROWSER"));        
        desiredCap.setCapability(CapabilityType.PLATFORM, System.getenv("SELENIUM_PLATFORM")); 
        //These environmental variables are set using Jenkins and the OnDemand Driver                                
                                  
   
        
        /*
        *Add your username and access key found under User Settings
        */
        Map<String, Object> sauceOptions = new HashMap<>();
        sauceOptions.put("username", System.getenv("SAUCE_USERNAME"));
        sauceOptions.put("accessKey", System.getenv("SAUCE_ACCESS_KEY"));
        sauceOptions.put("name", testInfo.getDisplayName());

        desiredCap.setCapability("sauce:options", sauceOptions);
        
        /*
        * OnDemand URL found under User Settings in Account Settings
        * The following is just an example, add your own URL
        */
        
        //URL url = new URL("*****:45*****3c54-4341-9bd3-f128dbb12256@ondemand.eu-central-1.saucelabs.com:443/wd/hub");
        URL url = new URL("https://oauth-2da9thpwr-65cdf:456b155a-3c54-4341-9bd3-f128dbb12256@ondemand.eu-central-1.saucelabs.com:443/wd/hub"); //Add your onDemand URL here or the test will not work 
        
        driver = new RemoteWebDriver(url, desiredCap);
    }

    @DisplayName("Selenium Navigation Test")        //The name of the test
    @Test
    public void navigateAndClose() {
        driver.navigate().to("https://www.saucedemo.com"); //Automatically navigates to this website         
        Assertions.assertEquals("Swag Labs", driver.getTitle()); //Checks the title
    }
    
    /**
     * Custom TestWatcher for Sauce Labs projects.
     */
    public class SauceTestWatcher implements TestWatcher {

        @Override
        public void testSuccessful(ExtensionContext context) {
            driver.executeScript("sauce:job-result=passed");
            driver.quit();
        }

        @Override
        public void testFailed(ExtensionContext context, Throwable cause) {
            driver.executeScript("sauce:job-result=failed");
            driver.quit();
        }
    }
}
