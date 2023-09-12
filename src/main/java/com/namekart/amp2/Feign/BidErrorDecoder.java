package com.namekart.amp2.Feign;

import feign.Response;
import feign.RetryableException;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component

public class BidErrorDecoder implements ErrorDecoder {
    @Autowired
    Decoder feignDecoder;
    Logger logger = Logger.getLogger("BidErrorDecoder");
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String s, Response response) {
        Exception exception = defaultErrorDecoder.decode(s, response);
        if(exception instanceof RetryableException){
            return exception;
        }
       // logger.info(response.request().requestTemplate().queries().get("orderBy").toArray()[0]+"");
            logger.info(response.reason());
            RetryableException e = new RetryableException(response.status(), response.reason(), response.request().httpMethod(), null, response.request());
            return e;

    }
}

