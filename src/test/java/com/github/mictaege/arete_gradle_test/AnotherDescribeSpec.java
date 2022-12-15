package com.github.mictaege.arete_gradle_test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Disabled;
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
        @ItShould @Disabled void notDoSomething() {
            assertThat(5, is(7));
        }
    }

}
