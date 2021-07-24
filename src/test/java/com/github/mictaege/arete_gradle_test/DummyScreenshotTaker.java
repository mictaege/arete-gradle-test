package com.github.mictaege.arete_gradle_test;

import java.io.File;

import com.github.mictaege.arete.ScreenshotTaker;
import com.google.common.io.Files;

class DummyScreenshotTaker implements ScreenshotTaker {

    @Override
    public byte[] getImageBytes() {
        try {
            return Files.asByteSource(new File(getClass().getResource("Dummy.png").toURI())).read();
        } catch (final Exception e) {
            return new byte[0];
        }
    }

    @Override
    public String getFileExtension() {
        return "png";
    }

}
