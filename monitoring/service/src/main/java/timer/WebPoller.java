/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import processing.Selenium;

/**
 *
 * @author leite
 * Execution command through one jar file in assembly - java -jar assembly-onejar.jar D:\Downloads\geckodriver.exe
 *
 */
public class WebPoller {

    private ScheduledExecutorService executor = null;

    public synchronized void start(String[] args) {
        System.out.println("INFO: System.out.println - main method starting in " + WebPoller.class);
        executor = Executors.newScheduledThreadPool(1);

        System.out.println("ITERATION --- ");
        Selenium task = new Selenium(args);
        executor.scheduleAtFixedRate(task, 0, 1, TimeUnit.MINUTES);

    }

    public synchronized void stop() {
        if (executor != null) {
            executor.shutdown();
        }

    }

    public static void main(String[] args) {
        WebPoller poller = new WebPoller();
        poller.start(args);

    }
}
