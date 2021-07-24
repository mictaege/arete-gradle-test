package com.github.mictaege.arete_gradle_test;

import static com.github.mictaege.arete.ScreenshotExtension.TestResult.FAILURE;
import static com.github.mictaege.arete.ScreenshotExtension.TestResult.SUCCESS;

import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.mictaege.arete.Describe;
import com.github.mictaege.arete.ItShould;
import com.github.mictaege.arete.ScreenshotExtension;
import com.github.mictaege.arete.Spec;

@Spec class AnotherDescribeSpec {

    @RegisterExtension
    public ScreenshotExtension screenshots = new ScreenshotExtension(new DummyScreenshotTaker(), SUCCESS, FAILURE);

    @Describe class ADescription {
        @ItShould void doSomething() {
        }
    }

}
