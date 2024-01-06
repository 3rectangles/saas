package com.barraiser.common.pipeline;

public abstract class PipelineStep<I, O> {
    public abstract O process(I input);

    protected final void terminate() {
        throw new TerminateStepException();
    }

    public static class TerminateStepException extends RuntimeException {

    }
}
