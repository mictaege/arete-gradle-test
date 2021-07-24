package com.github.mictaege.arete_gradle_test;

import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.mictaege.arete.Describe;
import com.github.mictaege.arete.ItShould;
import com.github.mictaege.arete.ScreenshotExtension;
import com.github.mictaege.arete.Spec;

@Spec class AnotherDescribeSpec {

    @RegisterExtension
    public ScreenshotExtension screenshots = new ScreenshotExtension(new DummyScreenshotTaker());

    @Describe class ADescription {
        @ItShould void doSomething() {
        }
    }

}
