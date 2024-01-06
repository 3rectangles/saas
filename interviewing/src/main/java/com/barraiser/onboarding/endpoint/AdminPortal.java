package com.barraiser.onboarding.endpoint;

import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class AdminPortal {
    private final UserDetailsRepository userDetailsRepository;

    @PostMapping("/user/details")
    public void addInterviewerDetails(@RequestBody final UserDetailsDAO detailsDAO) { // TODO: don't use DAO here.
        this.userDetailsRepository.save(detailsDAO);
    }
}
