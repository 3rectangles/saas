/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.sfn.async;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.services.sfn.SfnAsyncClient;
import software.amazon.awssdk.services.sfn.model.GetActivityTaskRequest;
import software.amazon.awssdk.services.sfn.model.GetActivityTaskResponse;
import software.amazon.awssdk.services.sfn.model.SendTaskFailureRequest;
import software.amazon.awssdk.services.sfn.model.SendTaskSuccessRequest;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;

/**
 * The sole purpose of this class is to streamline the way we write step
 * function activity today. @{@link StepFunction} is a representation of
 * an AWS StepFunction and its activities. Goal is to make the
 * implementation readable and confined at one place.
 *
 * @param <T>
 *            is the data type that will be passed from through all the
 *            activities.
 */
@Log4j2
public abstract class StepFunction<T> {
	public static final String ACTIVITY_ARN_PREFIX = "arn:aws:states:ap-south-1:969111487786:activity:";

	private final List<? extends StepFunctionActivity<T>> activities;
	private final SfnAsyncClient sfnAsyncClient;
	private final ObjectMapper objectMapper;
	private final Environment environment;

	public StepFunction(final List<? extends StepFunctionActivity<T>> activities,
			final SfnAsyncClient sfnAsyncClient,
			final ObjectMapper objectMapper,
			final Environment environment) {
		this.activities = activities;
		this.sfnAsyncClient = sfnAsyncClient;
		this.objectMapper = objectMapper;
		this.environment = environment;
	}

	@PostConstruct
	void init() {
		this.activities.forEach(this::process);
	}

	private void process(final StepFunctionActivity<T> activity) {

		final GetActivityTaskRequest request = GetActivityTaskRequest.builder()
				.activityArn(this.nameToArn(activity.name()))
				.workerName("async-worker")
				.build();

		this.sfnAsyncClient.getActivityTask(request)
				.whenComplete((response, throwable) -> {
					if (throwable != null) {
						log.error("Something really bad with StepFunction Polling");
						log.error(throwable, throwable);

						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
						this.process(activity);
					} else {
						try {
							// log.info("{} : Received & processing .{} {}, {}", activity.name(),
							// Instant.now(),

							// response.input(),
							// response.taskToken());

							if (response.input() != null) {
								T output = activity.process(this.getInput(response.input()));
								this.reportSuccess(response.taskToken(), this.getOutput(output));
							}
						} catch (final Exception ex) {
							this.reportFailure(response, ex);
						}
						this.process(activity);
					}
				});
	}

	private void reportFailure(GetActivityTaskResponse response, Exception ex) {
		log.error(ex, ex);

		try {
			this.sfnAsyncClient.sendTaskFailure(SendTaskFailureRequest.builder()
					.error(ex.getMessage())
					.taskToken(response.taskToken())
					.build())
					.join();
			log.warn("Sent failure");
		} catch (Exception ignored) {
		}
	}

	private void reportSuccess(final String taskToken, final StepData data)
			throws JsonProcessingException {
		// log.info("Really Sending Success");
		this.sfnAsyncClient.sendTaskSuccess(SendTaskSuccessRequest.builder()
				.output(this.objectMapper.writeValueAsString(data))
				.taskToken(taskToken)
				.build())
				.join();
		// log.info("Successfully completed the task");
	}

	private String nameToArn(final String name) {
		return String.format("%s%s-%s", ACTIVITY_ARN_PREFIX, this.environment.getActiveProfiles()[0], name);
	}

	private String getInput(final String input) throws JsonProcessingException {
		final StepData stepData = this.objectMapper.readValue(input, StepData.class);
		return this.objectMapper.writeValueAsString(stepData.getData());
	}

	private StepData getOutput(final T response) throws JsonProcessingException {
		final String st = this.objectMapper.writeValueAsString(response);
		final JsonNode node = this.objectMapper.readValue(st, JsonNode.class);
		final StepData stepData = new StepData();
		stepData.setData(node);
		return stepData;
	}

	@Data
	private static class StepData {
		private JsonNode data;
	}

}
