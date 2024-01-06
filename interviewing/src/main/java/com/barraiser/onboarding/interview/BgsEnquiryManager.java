package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.cms.CMSManager;
import com.barraiser.onboarding.cms.pages.BgsEnquiryPage;
import com.barraiser.onboarding.dal.BgsEnquiryDAO;
import com.barraiser.onboarding.dal.BgsEnquiryRepository;
import com.barraiser.common.dal.Money;
import com.buttercms.model.Page;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class BgsEnquiryManager {
    private final BgsEnquiryRepository bgsEnquiryRepository;
    private final CMSManager cmsManager;

    public Money getCandidateBgsCost() {
        final Page<BgsEnquiryPage> bgsEnquiryPage = this.cmsManager.getPage("bgs-enquiry", BgsEnquiryPage.class);
        return Money.builder()
            .value(bgsEnquiryPage.getFields().getCost())
            .build();
    }

    public void logBgsEnquiry(final String userId, final Boolean interested) {
        this.bgsEnquiryRepository.save(BgsEnquiryDAO.builder()
            .id(UUID.randomUUID().toString())
            .userId(userId)
            .interested(interested)
            .cost(getCandidateBgsCost().getValue())
            .build()
        );
    }
}
