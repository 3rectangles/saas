package com.barraiser.onboarding.cms;

import com.buttercms.IButterCMSClient;
import com.buttercms.model.Page;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@Component
@AllArgsConstructor
public class CMSManager {
    private final IButterCMSClient butterCMSClient;

    public <T> Page<T> getPage(final String pageName, Class<T> pageType) {
        Map<String,String> parameters = new HashMap<>();
        parameters.put("locale", "en");

        return butterCMSClient.getPage("*", pageName, parameters, pageType).getData();
    }

    public <T> Page<T> getPage(final String pageName, Class<T> pageType, final Boolean preview) {
        Map<String,String> parameters = new HashMap<>();
        parameters.put("locale", "en");
        parameters.put("preview", "1");

        return butterCMSClient.getPage("*", pageName, parameters, pageType).getData();
    }
}
