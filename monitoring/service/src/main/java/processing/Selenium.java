/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package processing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ic.monitoring.elastic.ElasticClient;
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
import org.slf4j.LoggerFactory;

/**
 *
 * @author leite
 */
public class Selenium implements Runnable {

    static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Selenium.class);
    private String[] args;

    public Selenium(String[] args) {
        this.args = args;
    }

    @Override
    public void run() {
        try {
            LOG.debug("****** LATEST VERSION");
            BrowserMobProxy proxy = null;
            WebDriver driver = null;
            String data = null;
            StringWriter writer = null;
            HarTransformMapper harMapper = null;
            String elasticUrl = "http://es01:9200";
            String indexName = "webdata";
            String indexType = "weblog";
            ElasticClient elasticClient;
            elasticClient = new ElasticClient(elasticUrl, indexName, indexType);

            try {
                String gekolocation = args[0]; // "./src/main/resources/geckodriver.exe"
                System.setProperty("webdriver.gecko.driver", gekolocation);
                LOG.debug("****** GeckoDriver Location : " + gekolocation);

                proxy = new BrowserMobProxyServer();
                proxy.setHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_HEADERS);

                proxy.start(0);
                LOG.debug("****** BrowserMobProxyServer started: ");

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
                proxy.newHar("leite5.github.io");

                LOG.debug("****** driver configured - getting site");
                                
                driver.get("https://leite5.github.io/");

                LOG.debug("****** driver get complete - writing har");

                // get the HAR data
                Har har = proxy.getHar();
                proxy.endHar();

                writer = new StringWriter();
                try {
                    har.writeTo(writer);
                    data = writer.toString();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } catch (Exception ex) {
                LOG.error("****** ERROR INITIALISING");                
                Logger.getLogger(Selenium.class.getName()).log(Level.SEVERE, null, ex);
                Logger.getLogger(Selenium.class.getName()).log(Level.SEVERE, null, ex);
                ex.printStackTrace();
            } finally {
                if ((proxy != null) && proxy.isStarted()) {
                    try {
                        LOG.debug("****** Shutting Down Server");                        
                        proxy.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (driver != null) {
                    LOG.debug("****** Shutting Down Driver");                    
                    driver.quit();
                }
            }
            LOG.debug("****** Test Complete - File Saved");

            try {
                LOG.debug("****** reading data lenght: " + data.length());

                ObjectMapper mapper = new ObjectMapper();
                harMapper = new HarTransformMapper();

                //To define metaData
                OnmsHarPollMetaData metaData = new OnmsHarPollMetaData();

                JsonNode input = mapper.readTree(data);                
                LOG.debug("****** JsonInput : " + input);

                ArrayNode jsonArrayData = harMapper.transform(input, metaData);
                if (jsonArrayData != null) {
                    LOG.debug("****** Json Array" + jsonArrayData.toPrettyString());                    
                } else {                    
                    LOG.debug("****** Array null"); 
                    
                }

                LOG.debug("****** Transformed HAR into array of " + jsonArrayData.size() + " objects.");                 

                elasticClient.sendBulkJsonArray(jsonArrayData);
            } catch (IOException ex) {
                LOG.error("***************** ERROR - " + ex.toString());                
            }

        } catch (Exception ex) {
            LOG.error("***************** ERROR - " + ex.toString());  
            
        }
    }
}
