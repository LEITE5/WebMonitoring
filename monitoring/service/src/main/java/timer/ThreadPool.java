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
 */
public class ThreadPool {
     public static void main(String[] args) 
    {      
        
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

            System.out.println("ITERATION --- ");
            Selenium task = new Selenium();
            exec.scheduleAtFixedRate(task , 0, 3, TimeUnit.MINUTES);

    }
}
