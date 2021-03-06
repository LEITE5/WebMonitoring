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
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);        
        
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);

         

            System.out.println("ITERATION --- ");
            Selenium task = new Selenium();
//            System.out.println("Created : " + task.getName());
            exec.scheduleAtFixedRate(task , 0, 5, TimeUnit.MINUTES);
            executor.execute(task);

    }
}
