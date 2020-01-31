package com.example.statemachine2.subflow.purchaseplan;


import com.example.statemachine2.subflow.BaseChainProcessor;
import com.example.statemachine2.subflow.DataFlowObject;
import com.example.statemachine2.util.http.client.HttpClientReadUtil;
import com.example.statemachine2.util.http.client.HttpClientResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SpcmPurchasePlan extends BaseChainProcessor {

    private static Logger LOGGER = LoggerFactory.getLogger(SpcmPurchasePlan.class);
    private CloseableHttpClient httpClient;

    public SpcmPurchasePlan(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public boolean handleRequest(DataFlowObject data)  {
        boolean success;
        String fooResourceUrl
                = "http://iel-dev-dkr-vm3:8091/spcm-rest-ws/pcc/spcm/subscribers/"+data.getMsisdn()+"/plans";
        HttpPost post=new HttpPost(fooResourceUrl);

        post.addHeader("Accept", "application/json");
        post.addHeader("Content-Type", "application/json");
        post.addHeader("tenant", "kcell");
        UsernamePasswordCredentials credentials
                = new UsernamePasswordCredentials("super_pmi", "tmp12345");
        try {
            post.addHeader(new BasicScheme().authenticate(credentials, post, null));
        } catch (AuthenticationException ae) {
            LOGGER.error("[{}] unable to create authentication credentials [{}]",data.getMsisdn(),ae.getMessage());
            LOGGER.error("Exception",ae);
            throw new RuntimeException(ae.getMessage());
        }
        String planName=data.getAllowedPlans().get(data.getSelectedIdx()).getName();

        StringBuilder body=new StringBuilder();
        body.append("{\"planDefinition\":{\"name\":\""+planName+"\"}");

        body.append(",\"purchaseSource\":\"customerCare\"");
        if((false==data.isActivateRecurringPlan())&&(data.getAllowedPlans().get(data.getSelectedIdx()).isRecurring())) {
            body.append(",\"maxOccurrenceCount\":1");
        }
        if(data.isDeferredPlanActivation()) {
            body.append(",\"activationTimestamp\":\""+data.getDeferredActivationDate()+"\"");
        }

        body.append("}");
        LOGGER.debug("[{}] Request URI [{}] RequestBody [{}]",data.getMsisdn(),fooResourceUrl,body.toString());
        try{
            StringEntity entity = new StringEntity(body.toString());
            post.setEntity(entity);
        }catch (UnsupportedEncodingException uee){
            LOGGER.error("[{}] Error encoding body [{}]",data.getMsisdn(),body);
            throw new RuntimeException(uee);
        }

        try{
            HttpClientResponse response= HttpClientReadUtil.read(httpClient,post);
            if(HttpStatus.SC_OK ==response.getStatusLine().getStatusCode()||HttpStatus.SC_ACCEPTED ==response.getStatusLine().getStatusCode()){
                LOGGER.debug("[{}] plan purchase success. Result [{}]",data.getMsisdn(),response.getStatusLine());
                success=true;
            }else {
                LOGGER.error("[{}] plan purchase fail result [{}]",data.getMsisdn(),response.getStatusLine());
                throw new RuntimeException("Plan Purchase failed");
            }
        }catch (IOException ioe){
            LOGGER.error("Exception",ioe);
            throw new RuntimeException(ioe);
        }

        return success;
    }


}
