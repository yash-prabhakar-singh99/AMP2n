package com.namekart.amp2.Feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.RetryableException;
import feign.codec.Decoder;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
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
        String message = null;
        InputStream responseBodyIs = null;
        try {
            responseBodyIs = response.body().asInputStream();
            ObjectMapper mapper = new ObjectMapper();
            ExceptionMessage exceptionMessage = mapper.readValue(responseBodyIs, ExceptionMessage.class);

            message = exceptionMessage.message;
            logger.info(exceptionMessage.toString());

        } catch (IOException e) {

            e.printStackTrace();
            // you could also return an exception
           // return new errorMessageFormatException(e.getMessage());
        }finally {

            //It is the responsibility of the caller to close the stream.
            try {
                if (responseBodyIs != null)
                    responseBodyIs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
       // logger.info(response.request().requestTemplate().queries().get("orderBy").toArray()[0]+"");
            logger.info(response.reason());
            RetryableException e = new RetryableException(response.status(), response.reason(), response.request().httpMethod(), null, response.request());
            return e;

    }

    public static class ExceptionMessage{

        private String timestamp;
        private int status;
        private String error;
        private String message;
        private String path;

        public ExceptionMessage() {
        }

        public String getTimestamp() {
            return timestamp;
        }

        @Override
        public String toString() {
            return "ExceptionMessage{" +
                    "timestamp='" + timestamp + '\'' +
                    ", status=" + status +
                    ", error='" + error + '\'' +
                    ", message='" + message + '\'' +
                    ", path='" + path + '\'' +
                    '}';
        }

        public ExceptionMessage(String timestamp, int status, String error, String message, String path) {
            this.timestamp = timestamp;
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}

