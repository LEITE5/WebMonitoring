/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ic.monitoring.elastic.ElasticClient;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringWriter;
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
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * @author leite
 */
public class Selenium implements Runnable{
    
    private String[] args;
    public Selenium(String[] args){
        this.args = args;
    }

    @Override
    public void run() {
        FileOutputStream fos = null;
        BrowserMobProxy proxy = null;
        WebDriver driver = null;
        String data = null;
        HarTransformMapper harMapper = null;
        String elasticUrl = "http://localhost:9200";
        String indexName = "webdata";
        String indexType = "weblog";
        ElasticClient elasticClient;
        elasticClient = new ElasticClient(elasticUrl, indexName, indexType);
        
        try {
            String gekolocation= args[0]; // "./src/main/resources/geckodriver.exe"
            System.setProperty("webdriver.gecko.driver", gekolocation );

//            File f = new File("target/newSolTest.har");
//            f.delete();
//            System.out.println("**************** har file: " + f.getAbsolutePath());

            proxy = new BrowserMobProxyServer();
            proxy.setHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_HEADERS);

            proxy.start(0);
            System.out.println("***************** BrowserMobProxyServer started: ");

            Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);
            FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions("--headless");
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);
                firefoxOptions.setCapability(CapabilityType.PROXY, seleniumProxy);
                firefoxOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

            driver = new FirefoxDriver(firefoxOptions);

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
            
             
            StringWriter writer = new StringWriter();
            try {
                            har.writeTo(writer);
                    } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                    }
            data = writer.toString();

//        try {
//            fos = new FileOutputStream(f);
//            har.writeTo(fos);
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(Selenium.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) { 
//            Logger.getLogger(Selenium.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//            System.out.println("***************** waiting 30 seconds");
//            try {
//                Thread.sleep(30000); // wait 30 secs
//                fos = new FileOutputStream(f);
//                har.writeTo(fos);
//            } catch (InterruptedException e) {
//
//            }

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
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }
            
        }
        System.out.println("***************** TEST COMPLETE - File saved");

        try {
        System.out.println("***************** reading data  :" + data.length());
        
        ObjectMapper mapper = new ObjectMapper();
        System.out.println("JSON NODE BREAKING 1");
        harMapper = new HarTransformMapper();
        System.out.println("JSON NODE BREAKING 2");

        //To define metaData
        OnmsHarPollMetaData metaData = new OnmsHarPollMetaData();
        System.out.println("JSON NODE BREAKING 3");
        
        
        JsonNode input = mapper.readTree(data);
        System.out.println("***************** Json Input" + input);

        ArrayNode jsonArrayData = harMapper.transform(input, metaData);
        if (jsonArrayData != null){
            System.out.println("***************** Json Array" + jsonArrayData.toPrettyString());
        }
        else {
            System.out.println("***************** Array null");
        }

        System.out.println("transformed har into array of " + jsonArrayData.size() + " objects :");

        elasticClient.sendBulkJsonArray(jsonArrayData);
        } catch (IOException ex) 
        {
            System.out.println("***************** ERROR - " + ex.toString());
        }
    }
}
