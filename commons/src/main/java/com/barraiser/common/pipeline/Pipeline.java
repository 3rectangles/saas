package com.barraiser.common.pipeline;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Pipeline<I, O> {
    private final PipelineStep<I, O> currentStep;

    public Pipeline(PipelineStep<I, O> currentStep) {
        this.currentStep = currentStep;
    }

    public <NewO> Pipeline <I, NewO> pipe(final PipelineStep<O, NewO> nextStep) {
        return new Pipeline<>(new PipelineStep<>() {
            @Override
            public NewO process(I input) {
                return nextStep.process(currentStep.process(input));
            }
        });
    }

    public O execute(final I input) {
        try {
            return currentStep.process(input);
        }
        catch (final PipelineStep.TerminateStepException e) {
            return null;
        }
    }
}
