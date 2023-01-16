package com.namekart.amp2;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.http.HttpUrlConnection;

import java.io.IOException;
import java.util.logging.Logger;

public class SoapHeaderManager implements WebServiceMessageCallback {
    private String soapAction;
Logger logger = Logger.getLogger("SoapHeaderManager");
    public SoapHeaderManager(String soapAction)
    {
        this.soapAction= soapAction;
    }

    @Override
    public void doWithMessage(WebServiceMessage message)
    {
        SoapMessage soapMessage = (SoapMessage) message;
        soapMessage.setSoapAction(soapAction);
        TransportContext context = TransportContextHolder.getTransportContext();
        HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
        try {
            connection.addRequestHeader("Authorization", "sso-key 9jbXdb1mjhS_QcMKNez5VGsuKqjy8zwFe7:KfM7V6dqvRfgYf7KkSPuin");

        }
        catch(IOException io)
        {
            logger.info(io.getMessage());
        }

    }
}
