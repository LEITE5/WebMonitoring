/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import processing.Selenium;

/**
 *
 * @author leite
 */
public class SeleniumTest {
    
     
     public void hello() {
         FileOutputStream fos = null;
        BrowserMobProxy proxy = null;
        WebDriver driver = null;

        try {
            
            System.setProperty("webdriver.gecko.driver", "./src/main/resources/geckodriver.exe");

            File f = new File("target/newSolTest.har");
            f.delete();
            System.out.println("**************** har file: " + f.getAbsolutePath());

            proxy = new BrowserMobProxyServer();
            proxy.setHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_HEADERS);

            proxy.start(0);
            System.out.println("***************** BrowserMobProxyServer started: ");

            Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
            capabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

            driver = new FirefoxDriver(capabilities);

            driver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
            driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);

            proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);

            //proxy.newHar("www.bbc.co.uk");
            proxy.newHar("youtube.com");

            System.out.println("***************** driver configured - getting site: ");

            //            driver.get("http://192.168.1.1/");
            driver.get("https://www.youtube.com/");

            System.out.println("***************** driver get complete - writing har ");

            // get the HAR data
            Har har = proxy.getHar();
            proxy.endHar();
            System.out.println("***************** waiting 30 seconds");
            try {
                Thread.sleep(30000); // wait 30 secs
                fos = new FileOutputStream(f);
                har.writeTo(fos);
            } catch (InterruptedException e) {

            }

        } catch (Exception ex) {
            System.out.println("***************** ERROR INITIALISING");
            Logger.getLogger(Selenium.class.getName()).log(Level.SEVERE, null, ex);
            Logger.getLogger(Selenium.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        } finally {
            if ((proxy != null) && proxy.isStarted()) {
                try {
                    System.out.println("***************** shutting down server");
                    proxy.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (driver != null) {
                System.out.println("***************** shutting down driver");
                driver.quit();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            
        }
        System.out.println("***************** TEST COMPLETE - File saved");
       
    }
     }
