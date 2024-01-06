/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;

public interface ExpertDeAllocationProcessor {
	void process(ExpertDeAllocatorData data) throws Exception;
}
