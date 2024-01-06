package com.barraiser.onboarding.communication;

import com.barraiser.onboarding.cms.CMSManager;
import com.barraiser.onboarding.cms.pages.FeedbackInconsistencyEmailPage;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.buttercms.model.Page;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@AllArgsConstructor
public class FeedbackInconsistencyCommunicationService {
    private final CMSManager cmsManager;
    private final EmailService emailService;
    private final InterViewRepository interViewRepository;
    private final UserDetailsRepository userDetailsRepository;
    private final StaticAppConfigValues staticAppConfigValues;
    private static final String EMAIL_TEMPLATE = "generic_template";

    public void sendEmailToExpert(final String interviewId) {
        final Map<String, Object> emailContent = this.constructEmailData(interviewId);
        final String subject = emailContent.get("subject").toString();
        final List<String> toEmail = new ArrayList<>();
        toEmail.add(emailContent.get("email").toString());
        final List<String> ccEmail = new ArrayList<>();
        ccEmail.add(this.staticAppConfigValues.getInterviewLifecycleInformationEmail());
        final String fromEmail = this.staticAppConfigValues.getInterviewLifecycleInformationEmail();

        try {
            this.emailService.sendEmailForObjectData(fromEmail, subject, EMAIL_TEMPLATE, toEmail, ccEmail, emailContent, null);
        } catch (final IOException io) {
            log.error(String.format("Error in sending feedback inconsistency mail for interview id- %s", interviewId));
        }
    }

    private Map<String, Object> constructEmailData(final String interviewId) {
        final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("Interview does not exist for interview id %s", interviewId)));

        final UserDetailsDAO userDetailsDAO = this.userDetailsRepository.findById(interviewDAO.getInterviewerId())
            .orElseThrow(() -> new IllegalArgumentException(String.format("Expert does not exist for expert id %s", interviewDAO.getInterviewerId())));

        final Page<FeedbackInconsistencyEmailPage> pageContent = this.cmsManager
            .getPage("feedback-correction-email", FeedbackInconsistencyEmailPage.class, true);

        final Map<String, Object> emailContent = new HashMap();
        emailContent.put("body", pageContent.getFields().getBody());
        emailContent.put("subject", pageContent.getFields().getSubject());
        emailContent.put("title", pageContent.getFields().getTitle());
        emailContent.put("email", userDetailsDAO.getEmail());
        emailContent.put("expertName", userDetailsDAO.getFirstName());
        emailContent.put("interviewId", interviewId);

        return emailContent;
    }
}
