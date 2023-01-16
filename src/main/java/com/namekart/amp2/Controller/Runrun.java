package com.namekart.amp2.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class Runrun implements Runnable{
    Logger logger= Logger.getLogger("Runrun");

    @Async
    @Override
    public void run()
    {
        logger.info(String.valueOf(System.currentTimeMillis()));
        try{
            Thread.sleep(2000);}
        catch(InterruptedException ie)
        {logger.info(ie.getMessage());}
    }
}
