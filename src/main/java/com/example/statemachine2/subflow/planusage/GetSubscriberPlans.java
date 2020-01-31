package com.example.statemachine2.subflow.planusage;


import com.example.statemachine2.dto.spcm.Plan;
import com.example.statemachine2.dto.spcm.SubscriberPlans;
import com.example.statemachine2.subflow.BaseChainProcessor;
import com.example.statemachine2.subflow.DataFlowObject;
import com.example.statemachine2.util.http.client.HttpClientPool;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.nio.charset.Charset;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;

import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.client.RestTemplate;

public class GetSubscriberPlans extends BaseChainProcessor {

    private static Logger LOGGER = LoggerFactory.getLogger(GetSubscriberPlans.class);

    CloseableHttpClient httpClient;

    public GetSubscriberPlans(CloseableHttpClient client) {
        this.httpClient = client;
    }

    public GetSubscriberPlans() {

        HttpClientPool httpClientPool= HttpClientPool.Builder.newInstance()
                .setMaxConnections(10)
                .setDefaultMaxPerRoute(2)
                .setMaxConnPerRoute(10)
                .build();
        this.httpClient=httpClientPool.getHttpClient();
    }

    @Override
    public boolean handleRequest(DataFlowObject data)  {

        LOGGER.info("[{}] GetSubscriberPlans",data.getMsisdn());
        String fooResourceUrl
                = "http://iel-dev-dkr-vm3:8091/spcm-rest-ws/pcc/spcm/subscribers/"+data.getMsisdn()+"/plans";

        HttpGet get=new HttpGet(fooResourceUrl);
        get.setURI(URI.create(fooResourceUrl));

        get.addHeader("Accept", "application/json");
        get.addHeader("tenant", "kcell");
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials("super_pmi", "tmp12345");
        try {
            get.addHeader(new BasicScheme().authenticate(credentials, get, null));
        }catch (AuthenticationException ae) {
            throw new RuntimeException(ae.getMessage());
        }

        /*
        ResponseHandler<String> rh = (response)->{
            StatusLine statusLine = response.getStatusLine();
            HttpEntity entity = response.getEntity();
            if (statusLine.getStatusCode() >= 300) {
                throw new HttpResponseException(
                        statusLine.getStatusCode(),
                        statusLine.getReasonPhrase());
            }
            if (entity == null) {
                throw new ClientProtocolException("Response contains no content");
            }
            ContentType contentType = ContentType.getOrDefault(entity);
            Charset charset = contentType.getCharset();
            Reader reader = new InputStreamReader(entity.getContent(), charset);
            data.setResultCode(statusLine.getStatusCode());
            return reader.toString();
        };

        try {
            httpClient.execute(get,rh);
        }catch (Exception e){

        }
        */

        try {
            CloseableHttpResponse response = httpClient.execute(get);
            try {
                data.setResultCode(response.getStatusLine().getStatusCode());
                HttpEntity entity = response.getEntity();
                String content=EntityUtils.toString(entity);
                LOGGER.info("[{}] GetSubscriberPlans[{}][{}]",data.getMsisdn(),response.getStatusLine().getStatusCode(),content);
                ObjectMapper mapper=new ObjectMapper()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                SubscriberPlans subscriberPlans=mapper.readValue(content, SubscriberPlans.class);
                if(subscriberPlans.getLength()>0) {
                    System.out.println("User has plans");
                    for (Plan plan:subscriberPlans.getPlans()) {
                        System.out.println(">>>>>>>>>>>>>>>>>>>>>Plan" + plan.getPlanDefinition().getName());
                        data.getPlansName().add(plan);
                    }
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException ex) {
            // Handle protocol errors
        } catch (IOException ex) {
            // Handle I/O errors
        } catch (Exception e){
            e.printStackTrace();
        }

        return true;

    }
}
