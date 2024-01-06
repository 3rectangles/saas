package com.barraiser.onboarding.endpoint;

import com.barraiser.onboarding.scheduling.ta.TaAllocationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Component
@AllArgsConstructor
public class TaAllocationController {
    private final TaAllocationService taAllocationService;

    @PostMapping("/triggerTaAllocation/{interviewId}")
    public ResponseEntity triggerTaAllocation(@PathVariable("interviewId") final String interviewId) {
        Boolean result = this.taAllocationService.triggerTaAllocation(interviewId);
        return ResponseEntity.ok(result);
    }
}

