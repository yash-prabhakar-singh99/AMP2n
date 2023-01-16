package com.namekart.amp2;

import com.namekart.amp2.GoDaddyEntities.AuctionList;
import com.namekart.amp2.GoDaddyEntities.GetAuctionsDetailRes;
import com.namekart.amp2.GoDaddyEntities.PlaceBid;
import com.namekart.amp2.stub.*;
//import com.namekart.amp2soap.stub.*;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.util.logging.Logger;


public class GoDaddySoapClient extends WebServiceGatewaySupport {
    Logger logger = Logger.getLogger("GoDaddy Soap Client");

public GetAuctionDetailsResponse getAuctionDetails(String domain)
{

    GetAuctionDetails getAuctionDetails = new GetAuctionDetails();

    getAuctionDetails.setDomainName(domain);
    GetAuctionDetailsResponse response = (GetAuctionDetailsResponse) getWebServiceTemplate().marshalSendAndReceive(getAuctionDetails, new SoapHeaderManager("GdAuctionsBiddingWSAPI_v2/GetAuctionDetails")/*,
            new WebServiceMessageCallback() {

                @Override
                public void doWithMessage(WebServiceMessage message) {
                    new SoapActionCallback("GdAuctionsBiddingWSAPI_v2/GetAuctionDetails");
                    TransportContext context = TransportContextHolder.getTransportContext();
                    HttpUrlConnection connection = (HttpUrlConnection) context.getConnection();
                    try {
                        connection.addRequestHeader("Authorization", "sso-key 9jbXdb1mjhS_QcMKNez5VGsuKqjy8zwFe7:KfM7V6dqvRfgYf7KkSPuin");
                    }
                catch(IOException i)
                {
                   logger.info(i.getMessage());
                }

                    logger.info("yes");
                }}*/);
    logger.info("yes1");

    return response;
}

public GetAuctionsDetailRes getAuctionDetail(String domain)
    {

        GetAuctionDetails getAuctionDetails = new GetAuctionDetails();

        getAuctionDetails.setDomainName(domain);
        GetAuctionDetailsResponse response = (GetAuctionDetailsResponse) getWebServiceTemplate().marshalSendAndReceive(getAuctionDetails, new SoapHeaderManager("GdAuctionsBiddingWSAPI_v2/GetAuctionDetails"));
        logger.info("yes1");
        String xmlString= response.getGetAuctionDetailsResult();
        JAXBContext jaxbContext;
        GetAuctionsDetailRes res=null;
        try
        {
            jaxbContext = JAXBContext.newInstance(GetAuctionsDetailRes.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            res = (GetAuctionsDetailRes) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            logger.info(res.getAuctionEndTime());
            //System.out.println(employee);
        }
        catch (JAXBException e)
        {
            logger.info(e.getMessage());
        }
        return res;

    }


public PlaceBid purchase(String domain, String price)
{
    PlaceBidOrPurchase placeBidOrPurchase= new PlaceBidOrPurchase();
    placeBidOrPurchase.setDomainName(domain);
    placeBidOrPurchase.setSBidAmount(price);
    placeBidOrPurchase.setAcceptAMA(true);
    placeBidOrPurchase.setAcceptDNRA(true);
    placeBidOrPurchase.setAcceptUTOS(true);
    placeBidOrPurchase.setUseMyPurchaseProfile(false);
    PlaceBidOrPurchaseResponse placeBidOrPurchaseResponse= (PlaceBidOrPurchaseResponse) getWebServiceTemplate().marshalSendAndReceive(placeBidOrPurchase, new SoapHeaderManager("GdAuctionsBiddingWSAPI_v2/PlaceBidOrPurchase"));

    logger.info(placeBidOrPurchaseResponse.getPlaceBidOrPurchaseResult());
    String xmlString= placeBidOrPurchaseResponse.getPlaceBidOrPurchaseResult();
    JAXBContext jaxbContext;
    PlaceBid res=null;
    try
    {
        jaxbContext = JAXBContext.newInstance(PlaceBid.class);

        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

        res = (PlaceBid) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
        logger.info(res.getIsValid());
        //System.out.println(employee);
    }
    catch (JAXBException e)
    {
        e.printStackTrace();
    }
    return res;
}

    public PlaceBid purchasecloseout(String domain, String price)
    {
        PlaceBidOrPurchase placeBidOrPurchase= new PlaceBidOrPurchase();
        placeBidOrPurchase.setDomainName(domain);
        placeBidOrPurchase.setSBidAmount(price);
        placeBidOrPurchase.setAcceptAMA(true);
        placeBidOrPurchase.setAcceptDNRA(true);
        placeBidOrPurchase.setAcceptUTOS(true);
        placeBidOrPurchase.setUseMyPurchaseProfile(true);
        PlaceBidOrPurchaseResponse placeBidOrPurchaseResponse= (PlaceBidOrPurchaseResponse) getWebServiceTemplate().marshalSendAndReceive(placeBidOrPurchase, new SoapHeaderManager("GdAuctionsBiddingWSAPI_v2/PlaceBidOrPurchase"));

        logger.info(placeBidOrPurchaseResponse.getPlaceBidOrPurchaseResult());
        String xmlString= placeBidOrPurchaseResponse.getPlaceBidOrPurchaseResult();
        JAXBContext jaxbContext;
        PlaceBid res=null;
        try
        {
            jaxbContext = JAXBContext.newInstance(PlaceBid.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            res = (PlaceBid) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            logger.info(res.getIsValid());
            //System.out.println(employee);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        return res;
    }

    public AuctionList getList(int pageNumber)
    {
        GetAuctionListByAuctionTypeAdultBidsCloseDays request = new GetAuctionListByAuctionTypeAdultBidsCloseDays(pageNumber,"3000","","expiring","True","bidsonly",1);
    GetAuctionListByAuctionTypeAdultBidsCloseDaysResponse response= (GetAuctionListByAuctionTypeAdultBidsCloseDaysResponse) getWebServiceTemplate().marshalSendAndReceive(request, new SoapHeaderManager("GdAuctionsBiddingWSAPI_v2/GetAuctionListByAuctionTypeAdultBidsCloseDays"));

        String xmlString= response.getGetAuctionListByAuctionTypeAdultBidsCloseDaysResult();
        JAXBContext jaxbContext;
        AuctionList res=null;
        try
        {
            jaxbContext = JAXBContext.newInstance(AuctionList.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            res = (AuctionList) jaxbUnmarshaller.unmarshal(new StringReader(xmlString));
            logger.info(res.getIsValid());
            //System.out.println(employee);
        }
        catch (JAXBException e)
        {
            e.printStackTrace();
        }
        return res;
    }

}
