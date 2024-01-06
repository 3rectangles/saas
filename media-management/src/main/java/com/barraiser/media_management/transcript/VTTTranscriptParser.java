package com.barraiser.media_management.transcript;

import com.barraiser.common.utilities.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class VTTTranscriptParser {
    private final DateUtils dateUtils;

    public Transcript parse(final String vttTranscript) throws IOException {
        List<String> rawTranscriptLines = this.getLinesFromText(vttTranscript);
        rawTranscriptLines = this.stripHeader(rawTranscriptLines);
        final List<List<String>> cues = this.getCues(rawTranscriptLines);
        final List<Transcript.Transcription> transcriptions = cues.stream().map(this::mapCueToTranscript).collect(Collectors.toList());
        return Transcript.builder().transcriptions(transcriptions).build();
    }

    private List<String> getLinesFromText(final String text) throws IOException {
        final List<String> lines = new ArrayList<>();
        final BufferedReader reader = new BufferedReader(new StringReader(text));
        String textLine = "";
        while((textLine = reader.readLine()) != null) {
            lines.add(textLine.trim());
        }
        return lines;
    }

    private List<String> stripHeader(final List<String> transcriptLines) {
        return transcriptLines.subList(2, transcriptLines.size());
    }

    private List<List<String>> getCues(final List<String> transcriptLines) {
        final List<List<String>> cues = new ArrayList<>();
        List<String> currentCue = new ArrayList<>();
        for(String line : transcriptLines) {
            if(StringUtils.isBlank(line)) {
                cues.add(currentCue);
                currentCue = new ArrayList<>();
            }
            else {
                currentCue.add(line);
            }
        }
        return cues;
    }

    private Transcript.Transcription mapCueToTranscript(List<String> cue) {
        final String[] lastLine = cue.get(2).split(":");
        final String[] timestamps = cue.get(1).split(" --> ");
        return Transcript.Transcription.builder()
            .from(this.dateUtils.getTimeStringInMillis(timestamps[0], DateUtils.ZOOM_TRANSCRIPT_TIME_FORMAT))
            .to(this.dateUtils.getTimeStringInMillis(timestamps[1], DateUtils.ZOOM_TRANSCRIPT_TIME_FORMAT))
            .speaker(lastLine.length > 1 ? lastLine[0] : null)
            .text(lastLine.length > 1 ? lastLine[1] : lastLine[0])
            .build();
    }
}
