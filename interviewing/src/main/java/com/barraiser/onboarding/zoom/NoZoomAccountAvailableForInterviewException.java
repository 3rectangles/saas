package com.barraiser.onboarding.zoom;

/**
 * An exception to be thrown when there is no zoom account available to be used for scheduling
 * interview.
 */
public class NoZoomAccountAvailableForInterviewException extends RuntimeException {
  public NoZoomAccountAvailableForInterviewException(final String s) {
    super(s);
  }
}
