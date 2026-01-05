package com.github.mictaege.arete_gradle_test;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.time.LocalDateTime;
import java.util.function.Function;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.mictaege.arete.ExampleCsv;
import com.github.mictaege.arete.ExampleCsvSource;
import com.github.mictaege.arete.ExampleGrid;
import com.github.mictaege.arete.ExampleGridSource;
import com.github.mictaege.arete.ExampleSource;
import com.github.mictaege.arete.Examples;
import com.github.mictaege.arete.Feature;
import com.github.mictaege.arete.Given;
import com.github.mictaege.arete.Narrative;
import com.github.mictaege.arete.Scenario;
import com.github.mictaege.arete.ScreenshotExtension;
import com.github.mictaege.arete.SeeAlso;
import com.github.mictaege.arete.Spec;
import com.github.mictaege.arete.Then;
import com.github.mictaege.arete.When;

@Tag("internal")
@Narrative(value = """
        *In order* to perform [arithmetic](https://de.wikipedia.org/wiki/Arithmetik) calculations
        
        A calculator should provide the basic operations
        - comparison
        - addition
        - subtraction
        - multiplication
        - division
        ```
        Examples:
        | operation | result |
        |-----------|--------|
        | 1 + 1     | 2      |
        | 2 - 1     | 1      |
        | 2 * 2     | 4      |
        | 4 / 2     | 2      |
        ```
        """,
    imageResourcePath = {
        "com/github/mictaege/arete_gradle_test/stars1.jpeg",
        "com/github/mictaege/arete_gradle_test/stars2.jpeg",
        "com/github/mictaege/arete_gradle_test/stars3.jpeg",
        "com/github/mictaege/arete_gradle_test/stars4.jpeg"
    },
    plantUml = """
            @startuml
            class Car
            
            Driver - Car : drives >
            Car *- Wheel : have 4 >
            Car -- Person : < owns
            
            @enduml
            """,
    attachmentResourcePath = {
        "com/github/mictaege/arete_gradle_test/Arete.pdf",
        "com/github/mictaege/arete_gradle_test/stars1.jpeg",
        "com/github/mictaege/arete_gradle_test/stars2.jpeg",
        "com/github/mictaege/arete_gradle_test/stars.7z"
    }
)
@Spec class ArithmeticCalculationsSpec {

    @RegisterExtension
    public ScreenshotExtension screenshots = new ScreenshotExtension(new ErrorScreenshotTaker());

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

        @ExampleGrid(order = 2, columns = {"Start", "End", "Result"}, srcMethod = "futureDatesExamples")
        void comparingFutureDates(final LocalDateTime start, final LocalDateTime end, final boolean expected) {
            assertThat(start.isBefore(end), is(expected));
        }

        void futureDatesExamples(final ExampleGridSource s) {
            final Function<LocalDateTime, String> toStr = (d) -> ofPattern("dd.MM.yyyy HH:mm").format(d);
            s.row(s.given(now().plusMinutes(5), toStr) , s.given(now().plusMinutes(4), toStr), s.then(false));
            s.row(s.given(now().plusMinutes(5), toStr) , s.given(now().plusMinutes(5), toStr), s.then(false));
            s.row(s.given(now().plusMinutes(5), toStr) , s.given(now().plusMinutes(6), toStr), s.then(true));
        }

        @ExampleCsv(order = 3, columns = {"First", "Second", "Larger"}, csvData
                = "lion, cat, lion" + "\n"
                + "dog, bird, bird" + "\n"
                + "bear, eagle, bear" + "\n"
                + "fish, shark, shark")
        void comparingLargerStrings(final String first, final String second, final String larger) {
            assertThat(maxString(first, second), is(larger));
        }

        @ExampleCsv(order = 4, delimiter = ';', columns = {"First", "Second", "Smaller"}, srcMethod = "smallerCsvData")
        void comparingSmallerStrings(final String first, final String second, final String smaller) {
            assertThat(minString(first, second), is(smaller));
        }

        private void smallerCsvData(final ExampleCsvSource source) {
            source.setCsvData(
                    "cat; lion; cat" + "\n"
                    + "bird; dog; dog" + "\n"
                    + "bear; eagle; eagle" + "\n"
                    + "shark; fish; fish");
        }

        @ExampleCsv(order = 5, delimiter = ';', columns = {"First", "Second", "Equal Size?"}, csvResourcePath = "com/github/mictaege/arete_gradle_test/equalCsvData.csv")
        void comparingEqualSizedStrings(final String first, final String second, final String equal) {
            assertThat(equalString(first, second), is(equal));
        }

        private String maxString(final String a, final String b) {
            return a.length() > b.length() ? a : b;
        }

        private String minString(final String a, final String b) {
            return a.length() < b.length() ? a : b;
        }

        private String equalString(final String a, final String b) {
            return a.length() == b.length() ? "true" : "false";
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

            @Narrative(value = {
                    "In order to calculate the area",
                    "A circle should be squared"
                },
                imageResourcePath = "com/github/mictaege/arete_gradle_test/stars4.jpeg",
                plantUml = {
                    """
                        @startuml
                            participant User
                            User -> A: DoWork
                            activate A
                    
                            A -> B: << createRequest >>
                            activate B
                    
                            B -> C: DoWork
                            activate C
                            C --> B: WorkDone
                            destroy C
                    
                            B --> A: RequestCreated
                            deactivate B
                    
                            A -> User: Done
                            deactivate A
                        @enduml
                    """,
                    """
                        @startuml
                            left to right direction
                            actor Guest as g
                            package Professional {
                              actor Chef as c
                              actor "Food Critic" as fc
                            }
                            package Restaurant {
                              usecase "Eat Food" as UC1
                              usecase "Pay for Food" as UC2
                              usecase "Drink" as UC3
                              usecase "Review" as UC4
                            }
                            fc --> UC4
                            g --> UC1
                            g --> UC2
                            g --> UC3
                        @enduml
                    """
                }
            )
            @SeeAlso(XDescribeSpec.class)
            @SeeAlso(DescribeSpec.ADescription.class)
            @SeeAlso(AnotherDescribeSpec.ADescription.class)
            @SeeAlso(Comparison.ComparingLocalDateTimes.class)
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
