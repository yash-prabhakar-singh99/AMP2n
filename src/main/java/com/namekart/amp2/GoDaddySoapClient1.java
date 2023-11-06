package com.namekart.amp2;

import com.namekart.amp2.GoDaddyEntities.AuctionList;
import com.namekart.amp2.GoDaddyEntities.MyDidNotWinSummary;
import com.namekart.amp2.GoDaddyEntities.MyWonSummary;
import com.namekart.amp2.stub1.GetMyDidNotWinSummary;
import com.namekart.amp2.stub1.GetMyDidNotWinSummaryResponse;
import com.namekart.amp2.stub1.GetMyWonSummary;
import com.namekart.amp2.stub1.GetMyWonSummaryResponse;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.logging.Logger;

public class GoDaddySoapClient1 extends WebServiceGatewaySupport {
    Logger logger = Logger.getLogger("GoDaddy Soap Client 1");

    public MyWonSummary getWinnings()
    {
        GetMyWonSummary request= new GetMyWonSummary();
        GetMyWonSummaryResponse response= (GetMyWonSummaryResponse) getWebServiceTemplate().marshalSendAndReceive(request, new SoapHeaderManager("GdAuctionsMemberWS/GetMyWonSummary"));
        String xmlString= response.getGetMyWonSummaryResult();
        logger.info(xmlString);
        JAXBContext jaxbContext;
        MyWonSummary res=null;
        try
        {
            jaxbContext = JAXBContext.newInstance(MyWonSummary.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            res = (MyWonSummary) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            logger.info(res.getIsValid());
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        return res;
    }

    public MyDidNotWinSummary getLosings()
    {
        GetMyDidNotWinSummary request= new GetMyDidNotWinSummary();
        GetMyDidNotWinSummaryResponse response= (GetMyDidNotWinSummaryResponse) getWebServiceTemplate().marshalSendAndReceive(request, new SoapHeaderManager("GdAuctionsMemberWS/GetMyDidNotWinSummary"));
        String xmlString= response.getGetMyDidNotWinSummaryResult();
        logger.info(xmlString);
        JAXBContext jaxbContext;
        MyDidNotWinSummary res=null;
        try
        {
            jaxbContext = JAXBContext.newInstance(MyDidNotWinSummary.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            res = (MyDidNotWinSummary) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
           logger.info(res.getIsValid());
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        return res;
    }

}
