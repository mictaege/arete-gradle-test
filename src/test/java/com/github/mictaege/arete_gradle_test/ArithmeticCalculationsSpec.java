package com.github.mictaege.arete_gradle_test;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDateTime;
import java.util.function.Function;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;

import com.github.mictaege.arete.ExampleSource;
import com.github.mictaege.arete.Examples;
import com.github.mictaege.arete.Feature;
import com.github.mictaege.arete.Given;
import com.github.mictaege.arete.Narrative;
import com.github.mictaege.arete.Scenario;
import com.github.mictaege.arete.Spec;
import com.github.mictaege.arete.Then;
import com.github.mictaege.arete.When;

@Tag("internal")
@Narrative({
        "In order to perform arithmetic calculations",
        "A calculator should provide the basic operations",
        "- comparison",
        "- addition",
        "- subtraction",
        "- multiplication",
        "- division"
})
@Spec class ArithmeticCalculationsSpec {

    @Feature class Comparison {

        @Scenario class ComparingLocalDateTimes {
            @Given void twoLocalDateTimes() {}
            @When void compare() {}
            @Then void theEarlierAndLaterShouldBeIdentified() {}
        }

        @Examples(order = 1, pattern = "{0} before {1} => {2}", srcMethod = "pastDatesExamples")
        void comparingPastDates(final LocalDateTime start, final LocalDateTime end, final boolean expected) {
            assertThat(start.isBefore(end), is(expected));
        }

        void pastDatesExamples(final ExampleSource s) {
            final Function<LocalDateTime, String> toStr = (d) -> ofPattern("dd.MM.yyyy HH:mm").format(d);
            s.example(s.given(now().minusMinutes(5), toStr) , s.given(now().minusMinutes(4), toStr), s.then(true));
            s.example(s.given(now().minusMinutes(5), toStr) , s.given(now().minusMinutes(5), toStr), s.then(false));
            s.example(s.given(now().minusMinutes(5), toStr) , s.given(now().minusMinutes(6), toStr), s.then(false));
        }

        @Examples(order = 2, pattern = "{0} before {1} => {2}", srcMethod = "futureDatesExamples")
        void comparingFutureDates(final LocalDateTime start, final LocalDateTime end, final boolean expected) {
            assertThat(start.isBefore(end), is(expected));
        }

        void futureDatesExamples(final ExampleSource s) {
            final Function<LocalDateTime, String> toStr = (d) -> ofPattern("dd.MM.yyyy HH:mm").format(d);
            s.example(s.given(now().plusMinutes(5), toStr) , s.given(now().plusMinutes(4), toStr), s.then(false));
            s.example(s.given(now().plusMinutes(5), toStr) , s.given(now().plusMinutes(5), toStr), s.then(false));
            s.example(s.given(now().plusMinutes(5), toStr) , s.given(now().plusMinutes(6), toStr), s.then(true));
        }

    }

    @Feature class MyOtherFeature {

        @Scenario class MyScenario {
            @Given void aTest() {}
            @When void doing() {}
            @Then void expect() {}

            @Examples(pattern = "{0} less then {1} => {2}", srcClass = DoSomethingExamples.class)
            void doSomething(final int a, final int b, final boolean expected) {
                assertThat(a <= b, is(expected));
            }

            class DoSomethingExamples extends ExampleSource {
                @Override
                protected void init() {
                    example(given(5), given(4), then(false));
                    example(given(5), given(5), then(false));
                    example(given(5), given(6), then(true));
                }
            }
        }

        @Scenario class MyOtherScenario {
            @Given void anotherTest() {}
            @When void doingSomethingElse() {}
            @Then void expectDifferentResult() {
                assertThat(8, is(15));
            }

            @Narrative({
                    "In order to calculate the area",
                    "A circle should be squared"
            })
            @Scenario class MyScenario {
                @Given void aTest() {}
                @When void doing() {}
                @Then @Disabled void expect() {}
            }

        }

    }


    @Scenario class MyScenario {
        @Given void aTest() {}
        @When void doing() {}
        @Then void expect() {}
    }

}
