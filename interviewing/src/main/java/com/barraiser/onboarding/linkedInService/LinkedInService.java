package com.barraiser.onboarding.linkedInService;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.linkedInService.dto.*;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.json.JSONObject;
import java.util.Date;


@Log4j2
@Component
@AllArgsConstructor
public class LinkedInService {
    private LinkedinClient linkedinClient;
    private StaticAppConfigValues staticAppConfigValues;
    private AWSSecretsManager awsSecretsManager;

    public  LinkedinShareResponseDTO accessToken(final String code){

        return this.linkedinClient.getAccessToken(buildLinkedinShareRequest(code));
    }

    private LinkedinShareRequestDTO buildLinkedinShareRequest(final String code){
        final String credential = this.awsSecretsManager
            .getSecretValue(new GetSecretValueRequest().withSecretId("LinkedInSecret"))
            .getSecretString();
        final JSONObject obj = new JSONObject(credential);
        final String clientId = obj.getString("id");
        final  String clientSecret = obj.getString("secret");
        final String redirectUrl = this.staticAppConfigValues.getRedirectUri() +"/LinkedinRedirectOauth";
        return LinkedinShareRequestDTO.builder()
            .grant_type("authorization_code")
            .code(code)
            .redirect_uri(redirectUrl)
            .client_id(clientId)
            .client_secret(clientSecret)
            .build();
    }
    public LinkedinPostResponseDTO sharePost(final String accessToken, final String candidateUrn, final String imageUrl, final String certificateId){
        final String finalAccessToken = "Bearer" + " " + accessToken;
        final String entityLocation = this.staticAppConfigValues.getRedirectUri() +"/Certificate/" + certificateId;
        final String currentTime ="?" + new Date();
        final String newImageUrl = imageUrl +currentTime;
        final String content = String.format("{\"content\":{\"contentEntities\": [{\"entityLocation\":\"%s\", \"thumbnails\": [{\"resolvedUrl\":\"%s\"}]}],\"title\":\"Barraiser Certificate\"},\"distribution\":{\"linkedInDistributionTarget\":{}},\"owner\":\"urn:li:person:%s\",\"subject\":\"Certificate Of Achievement\",\"text\":{\"annotations\": [{\"entity\": \"urn:li:organization:31289698\",\"start\": 10,\"length\": 9}],\"text\": \"Thank you BarRaiser. View my certificate of achievement #achievement #barraiser #interviewing\"}}",entityLocation, newImageUrl,candidateUrn);
        LinkedinPostResponseDTO linkedinPostResponseDTO = this.linkedinClient.sharePost(finalAccessToken,content);
        log.info("Certificate has been shared with certificateId: {}", certificateId);
        return linkedinPostResponseDTO;
    }


    public LinkedinUrnResponseDTO getUrn(final String accessToken){
        final String finalAccesToken = "Bearer" + " " + accessToken;
        return this.linkedinClient.getUrn(finalAccesToken);
    }



}
