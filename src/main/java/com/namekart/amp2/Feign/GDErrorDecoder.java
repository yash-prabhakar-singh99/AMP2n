package com.namekart.amp2.Feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.namekart.amp2.GoDaddyEntities.CheckError;
import com.namekart.amp2.GoDaddyEntities.ErrorGDV429;
import feign.Response;
import feign.RetryableException;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

@Component

public class GDErrorDecoder implements ErrorDecoder {
    @Autowired
    Decoder feignDecoder;
    Logger logger = Logger.getLogger("ErrorDecoder");
    private final ErrorDecoder defaultErrorDecoder = new ErrorDecoder.Default();

    @Override
    public Exception decode(String s, Response response) {

/* CheckError checkError=null;
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            checkError = mapper.readValue(bodyIs, CheckError.class);
        } catch (IOException e) {
            return new Exception(e.getMessage());
        }
        logger.info(checkError.getMessage()+" "+checkError.getRetryAfterSec());
        if(checkError.getFields()!=null)
        {
           logger.info(checkError.getFields().get(0).getMessage());
        }*/

        Exception exception = defaultErrorDecoder.decode(s, response);
        logger.info(response.reason()+" "+s);

       if(exception instanceof RetryableException){
           return exception;
        }

       else if(response.status() == 429){
          // Date date = checkError.getRetryAfterSec()==null||checkError.getRetryAfterSec()==0?null: new Date(System.currentTimeMillis()+checkError.getRetryAfterSec()*1000);
            RetryableException e= new RetryableException(429, response.reason(), response.request().httpMethod(),null,response.request());
//LinkedHashMap map=(LinkedHashMap) response.body();
logger.info("2");

/*  try {
                // LinkedHashMap error=(LinkedHashMap) response.body();
                ErrorGDV429 error= (ErrorGDV429) feignDecoder.decode(response,ErrorGDV429.class);
                logger.info(""+error.getMessage());
            }
            catch(Exception io)
            {
                logger.info(io.getMessage());
            }*//*

            return e;
            // return new RetryableException()
        }
       else if(response.status() == 522){
           RetryableException e= new RetryableException(522, response.reason(), response.request().httpMethod(),null,response.request());

           logger.info(s+" "+response.reason());
           //LinkedHashMap map=(LinkedHashMap) response.body();

          */
/* try {
               // LinkedHashMap error=(LinkedHashMap) response.body();
               ErrorGDV429 error= (ErrorGDV429) feignDecoder.decode(response,ErrorGDV429.class);
               logger.info(""+error.getMessage());
           }
           catch(Exception io)
           {
               logger.info(io.getMessage());
           }*/

           return e;
           // return new RetryableException()
       }



        return exception;
    }
}

