package com.barraiser.onboarding.endpoint;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UploadDataController {
    @PostMapping("/copy/data/interviewer/")

    public void copyDataFromDynamoToAlgolia() {

    }
}
