/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ic.monitoring.assembly;


import java.lang.System.Logger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;
import processing.Selenium;
import timer.WebPoller;


/**
 *
 * @author leite
 */
public class Main {
    
//    final static Logger LOG = LogManager.getLogger(Main.class);
    private static WebPoller webPoller;

    public static void main(String[] args) {
        System.out.println("INFO: System.out.println - main method starting in " + Main.class);
//        LOG.info("main method starting in " + Main.class);
        
     
        String msg=" arguments passed to main: ("+args.length+") ";
        for(int i=0; i<args.length; i++){
            msg=msg+args[i]+" ";
        }
        System.out.println("INFO: System.out.println - "+msg);
//        LOG.info(msg);

        webPoller = new WebPoller();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("main shutdown hook quitting application");
//                LOG.info("main shutdown hook quitting application");
                webPoller.stop();
            }
        });

        webPoller.start(args);

        // waits for shutdown - may never complete
        try {
//            LOG.debug("waiting for interrupt to shut down application");
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            System.out.println("current thread interrupted");
//            LOG.info("current thread  interrupted");
        }
//        LOG.info("stopping application");
        System.out.println("stopping application");

    }

    private void stop() {
         webPoller.stop();
    }

    private  synchronized void start(String[] args) {
        try {
            if (webPoller!=null ){
                 System.out.println("trying to start web poller");
                webPoller = new WebPoller();
                 webPoller.start(args);
            } else {
                System.out.println("trying to start web poller but already started");
            }
           

        } catch (Exception e) {
            System.out.println("Exception " + e);
        }
    }
}
