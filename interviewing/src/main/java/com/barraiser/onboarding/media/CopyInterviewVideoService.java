package com.barraiser.onboarding.media;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.transfer.TransferManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class CopyInterviewVideoService {
    private final static String S3_BUCKET = "barraiser-videos";
    private final static String S3_PREFIX = "interview_recording";

    private final TransferManager transferManager;
    private final AmazonS3 s3Client;

    public void copyInterviewVideo(final String fromInterviewId, final String toInterviewId) {
        final ObjectListing listing = this.s3Client.listObjects(S3_BUCKET, S3_PREFIX + "/" + fromInterviewId);
        listing.getObjectSummaries().forEach(object -> {
            final String destinationKey = String.format("%s/%s/%s.mp4", S3_PREFIX, toInterviewId, UUID.randomUUID().toString());
            this.transferManager.copy(S3_BUCKET, object.getKey(), S3_BUCKET, destinationKey);
        });
    }
}
