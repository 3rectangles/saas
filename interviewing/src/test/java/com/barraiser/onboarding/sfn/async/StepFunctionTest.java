/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.sfn.async;

import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.config.AppConfig;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.sfn_activities.ExpertAllocatorSfnActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.services.sfn.SfnAsyncClient;
import software.amazon.awssdk.services.sfn.model.GetActivityTaskRequest;
import software.amazon.awssdk.services.sfn.model.GetActivityTaskResponse;
import software.amazon.awssdk.services.sfn.model.SendTaskSuccessRequest;
import software.amazon.awssdk.services.sfn.model.SendTaskSuccessResponse;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StepFunctionTest {
	@Mock
	private SfnAsyncClient sfnAsyncClient;
	@Mock
	private Environment environment;

	private TestingUtil testingUtil;
	private StepFunctionTestC stepFunctionTestC;

	@Before
	public void setup() {
		final ObjectMapper objectMapper = new AppConfig().getObjectMapper();
		this.testingUtil = new TestingUtil(objectMapper);

		final List<ExpertAllocatorSfnActivity> activities = List.of(new TestExpertAllocatorSfnActivity(objectMapper));
		this.stepFunctionTestC = new StepFunctionTestC(activities, this.sfnAsyncClient, objectMapper, this.environment);
	}

	// this test will trigger an infinite loop, only meant for testing in local
	// @Test
	public void testDeserializationOfdata() throws IOException, InterruptedException {
		final String expertAllocatorJson = this.testingUtil
				.getJsonFromFile("src/test/resources/json_data_files/expert-allocator-data.json");
		when(this.environment.getActiveProfiles()).thenReturn(new String[] { "local" });
		when(this.sfnAsyncClient.getActivityTask(any(GetActivityTaskRequest.class)))
				.thenReturn(CompletableFuture.supplyAsync(() -> {
					return GetActivityTaskResponse.builder()
							.input(expertAllocatorJson)
							.taskToken("a task token")
							.build();
				}));
		when(this.sfnAsyncClient.sendTaskSuccess(any(SendTaskSuccessRequest.class)))
				.thenReturn(CompletableFuture.supplyAsync(() -> {
					return SendTaskSuccessResponse.builder().build();
				}));
		this.stepFunctionTestC.init();
		Thread.sleep(100);
		verify(this.sfnAsyncClient, times(1)).sendTaskSuccess(any(SendTaskSuccessRequest.class));

	}

	private static class StepFunctionTestC extends StepFunction<ExpertAllocatorData> {

		public StepFunctionTestC(List<? extends StepFunctionActivity<ExpertAllocatorData>> stepFunctionActivities,
				SfnAsyncClient sfnAsyncClient, ObjectMapper objectMapper, Environment environment) {
			super(stepFunctionActivities, sfnAsyncClient, objectMapper, environment);
		}
	}

	private static class TestExpertAllocatorSfnActivity implements ExpertAllocatorSfnActivity {
		private final ObjectMapper objectMapper;

		private TestExpertAllocatorSfnActivity(final ObjectMapper objectMapper) {
			this.objectMapper = objectMapper;
		}

		@Override
		public String name() {
			return "a name";
		}

		@Override
		public ExpertAllocatorData process(String input) throws Exception {
			final ExpertAllocatorData expertAllocatorData = this.objectMapper.readValue(input,
					ExpertAllocatorData.class);
			System.out.println(expertAllocatorData.getIsExpertDuplicate());
			System.out.println(expertAllocatorData.getInterview().getInterviewerId());
			return expertAllocatorData;
		}
	}

}
