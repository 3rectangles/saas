package com.barraiser.onboarding.resume;

import com.barraiser.onboarding.document.DocumentRepository;
import com.barraiser.onboarding.resume.dto.ParsedResumeDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@AllArgsConstructor
public class ResumeParserManager {
    private final DocumentRepository documentRepository;
    private final ResumeParser resumeParser;
    private final ParsedResumeStorage parsedResumeStorage;

    public void parseAndStoreResume(final String documentId) {
        final String resumeURL = this.documentRepository.findById(documentId).get().getFileUrl();
        final String parsedResumeJsonString = this.resumeParser.parseResumeToJSONString(resumeURL);
        final ParsedResumeDTO parsedResume = this.resumeParser.parseResume(resumeURL);
        log.info("resume parsed: {}", parsedResume.getName());
        try {
            this.parsedResumeStorage.saveParsedResume(documentId, parsedResume, parsedResumeJsonString);
        } catch (Exception e) {
            log.error("Unable to store parsed resume");
        }
    }
}
