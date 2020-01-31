package com.example.statemachine2.subflow;

import com.example.statemachine2.dto.spcm.SubscriberDto;
import com.example.statemachine2.util.http.client.HttpClientPool;
import com.example.statemachine2.util.http.client.HttpClientReadUtil;
import com.example.statemachine2.util.http.client.HttpClientResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GetSubscriberProfileStage extends BaseChainProcessor {

    private static Logger LOGGER = LoggerFactory.getLogger(GetSubscriberProfileStage.class);

    private CloseableHttpClient httpClient;

    public GetSubscriberProfileStage(CloseableHttpClient client) {
        this.httpClient = client;
    }

    public GetSubscriberProfileStage() {

        LOGGER.info("new Instance of GetSubscriberProfileStage()");
        HttpClientPool httpClientPool = HttpClientPool.Builder.newInstance()
                .setMaxConnections(10)
                .setDefaultMaxPerRoute(2)
                .setMaxConnPerRoute(10)
                .build();
        this.httpClient = httpClientPool.getHttpClient();
    }

    @Override
    public boolean handleRequest(DataFlowObject data){

        String fooResourceUrl
                = "http://iel-dev-dkr-vm3:8091/spcm-rest-ws/pcc/spcm/subscribers/" + data.getMsisdn();

        HttpGet get = new HttpGet();
        get.setURI(URI.create(fooResourceUrl));

        get.addHeader("Accept", "application/json");
        get.addHeader("tenant", "kcell");
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials("super_pmi", "tmp12345");
        try {
            get.addHeader(new BasicScheme().authenticate(credentials, get, null));
        } catch (AuthenticationException ae) {
            throw new RuntimeException(ae.getMessage());
        }

        try{
            HttpClientResponse response=HttpClientReadUtil.read(httpClient,get);
            if(HttpStatus.SC_OK ==response.getStatusLine().getStatusCode()){

                try{
                    ObjectMapper mapper = new ObjectMapper()
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SubscriberDto subscriberDto = mapper.readValue(response.getBytes(), SubscriberDto.class);
                    data.setSubscriberType(subscriberDto.getPaymentType());
                    LOGGER.info("[{}] GetSubscriberProfileStage[{}][{}]", data.getMsisdn(), HttpStatus.SC_OK, new String(response.getBytes()));
                }catch (Exception e){
                    LOGGER.error("[{}] No subscriber profile result [{}]",data.getMsisdn(),response.getStatusLine());
                }
            }else {
                LOGGER.error("[{}] No subscriber profile result [{}]",data.getMsisdn(),response.getStatusLine());
                throw new RuntimeException("No subscriber profile");
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
            throw new RuntimeException(ioe);
        }
        return true;

    }
}
