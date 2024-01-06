package com.barraiser.media_management.transcript;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.barraiser.common.model.TranscriptDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class TranscriptFetcher {
    private final Long MAX_NO_OF_MILLIS_FOR_MERGING_TRANSCRIPTIONS = 10 * 1000L;

    private final AmazonS3 s3Client;
    private final ObjectMapper objectMapper;

    public TranscriptDTO getTranscript(final String entityId) throws IOException {
        final String transcriptString = this.getRawTranscriptFromS3(entityId);
        if(transcriptString == null) {
            return null;
        }
        Transcript transcript = this.objectMapper.readValue(transcriptString, Transcript.class);
        transcript = this.filterForNoSpeakers(transcript);
        TranscriptDTO transcriptDTO = TranscriptDTO.builder()
            .transcriptions(transcript.getTranscriptions().stream().map(
                t -> TranscriptDTO.TranscriptionDTO.builder()
                    .startTime(t.getFrom())
                    .endTime(t.getTo())
                    .speaker(t.getSpeaker())
                    .text(t.getText())
                    .build()
            ).collect(Collectors.toList()))
            .build();
        transcriptDTO = transcriptDTO.toBuilder()
            .transcriptions(this.mergeTranscriptions(transcriptDTO.getTranscriptions()))
            .build();
        return transcriptDTO;
    }

    private String getRawTranscriptFromS3(final String entityId) throws IOException {
        final String object = String.format("%s.%s", entityId, "json");
        if(!this.s3Client.doesObjectExist(TranscriptUploader.S3_BUCKET, object)) {
            return null;
        }
        final S3Object s3object = this.s3Client.getObject(
            TranscriptUploader.S3_BUCKET,
            object
        );
        return StreamUtils.copyToString(s3object.getObjectContent(), StandardCharsets.UTF_8);
    }

    private Transcript filterForNoSpeakers(final Transcript transcript) {
        return Transcript.builder()
            .transcriptions(transcript.getTranscriptions().stream().filter(
                t -> t.getSpeaker() != null
            ).collect(Collectors.toList()))
            .build();
    }

    private List<TranscriptDTO.TranscriptionDTO> mergeTranscriptions(final List<TranscriptDTO.TranscriptionDTO> transcriptions) {
        if(transcriptions.isEmpty()) {
            return transcriptions;
        }
        final List<TranscriptDTO.TranscriptionDTO> mergedTranscriptions = new ArrayList<>();
        TranscriptDTO.TranscriptionDTO mergedTranscription = transcriptions.get(0);
        for(int i = 1; i < transcriptions.size(); ++i) {
            final TranscriptDTO.TranscriptionDTO transcription = transcriptions.get(i);
            if (transcription.getSpeaker().equals(mergedTranscription.getSpeaker()) &&
                transcription.getStartTime() - mergedTranscription.getEndTime() <= MAX_NO_OF_MILLIS_FOR_MERGING_TRANSCRIPTIONS) {
                mergedTranscription = mergedTranscription.toBuilder()
                    .endTime(transcription.getEndTime())
                    .text(mergedTranscription.getText() + " " + transcription.getText())
                    .build();
            }
            else {
                mergedTranscriptions.add(mergedTranscription);
                mergedTranscription = transcription;
            }
        }
        mergedTranscriptions.add(mergedTranscription);
        return mergedTranscriptions;
    }
}
