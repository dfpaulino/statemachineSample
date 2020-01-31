package com.example.statemachine2.subflow.purchaseplan;

import com.example.statemachine2.dto.spcm.PlanDefinition;
import com.example.statemachine2.dto.spcm.SubscriberAllowedPlans;
import com.example.statemachine2.dto.spcm.SubscriberDto;
import com.example.statemachine2.subflow.BaseChainProcessor;
import com.example.statemachine2.subflow.DataFlowObject;
import com.example.statemachine2.util.http.client.HttpClientPool;
import com.example.statemachine2.util.http.client.HttpClientReadUtil;
import com.example.statemachine2.util.http.client.HttpClientResponse;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class GetSubscriberAllowedPlans extends BaseChainProcessor {

    private static Logger LOGGER = LoggerFactory.getLogger(GetSubscriberAllowedPlans.class);

    CloseableHttpClient httpClient;

    public GetSubscriberAllowedPlans(CloseableHttpClient client) {
        this.httpClient = client;
    }

    public GetSubscriberAllowedPlans() {

        HttpClientPool httpClientPool = HttpClientPool.Builder.newInstance()
                .setMaxConnections(10)
                .setDefaultMaxPerRoute(2)
                .setMaxConnPerRoute(10)
                .build();
        this.httpClient = httpClientPool.getHttpClient();
    }

    //TODO set some Error codes on the exceptions
    @Override
    public boolean handleRequest(DataFlowObject data) {

        boolean successStep=false;
        HttpGet get=new HttpGet();

        try {
            URIBuilder builder = new URIBuilder("http://iel-dev-dkr-vm3:8091/spcm-rest-ws/pcc/spcm/subscribers/" + data.getMsisdn() + "/plans/allowed");
            builder.setParameter("shareQuota", String.valueOf(data.isSharedQuota()));

            get.setURI(builder.build());

            get.addHeader("Accept", "application/json");
            get.addHeader("tenant", "kcell");
            UsernamePasswordCredentials credentials
                    = new UsernamePasswordCredentials("super_pmi", "tmp12345");

            get.addHeader(new BasicScheme().authenticate(credentials, get, null));
        } catch (AuthenticationException ae) {
            ae.printStackTrace();
            throw new RuntimeException(ae.getMessage());
        } catch (URISyntaxException urie) {
            urie.printStackTrace();
            throw new RuntimeException(urie.getMessage());
        }

        try {
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("SPCM Request [{}] ",get.getURI().toString());
            }
            HttpClientResponse response = HttpClientReadUtil.read(httpClient, get);
            if(LOGGER.isDebugEnabled()){
                LOGGER.debug("SPCM ResponseCode [{}] with content [{}]",response.getStatusLine(),new String(response.getBytes()));
            }
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                try {
                    ObjectMapper mapper = new ObjectMapper()
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    SubscriberAllowedPlans subscriberAllowedPlans = mapper.readValue(response.getBytes(), SubscriberAllowedPlans.class);

                    if (subscriberAllowedPlans.getPlans().size() > 0) {
                        for (PlanDefinition plan : subscriberAllowedPlans.getPlans()) {
                            data.getAllowedPlans().add(plan);
                            successStep=true;
                        }
                    }else {
                        data.setResultCode(response.getStatusLine().getStatusCode());
                    }
                } catch (Exception e) {

                }
            }else{
                LOGGER.error("SPCM request returned Error [{}]",response.getStatusLine());
                data.setResultCode(response.getStatusLine().getStatusCode());
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            throw new RuntimeException(ioe);
        }

        return successStep;

    }

}
