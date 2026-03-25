package com.github.mictaege.travel_agency;

import static com.github.mictaege.arete.ScreenshotTaker.TestResult.FAILURE;
import static com.github.mictaege.arete.ScreenshotTaker.TestResult.SUCCESS;
import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.util.Set;
import java.util.stream.Stream;

import com.github.mictaege.arete.ScreenshotTaker;
import com.google.common.io.Files;

class UiDummyScreenshotTaker implements ScreenshotTaker {

    @Override
    public Set<TestResult> takeWhen() {
        return Stream.of(SUCCESS, FAILURE).collect(toSet());
    }

    @Override
    public byte[] getImageBytes() {
        try {
            return Files.asByteSource(new File(getClass().getResource("ui-dummy.png").toURI())).read();
        } catch (final Exception e) {
            return new byte[0];
        }
    }

}
