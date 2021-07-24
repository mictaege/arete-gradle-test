package com.github.mictaege.arete_gradle_test;

import static com.github.mictaege.arete.ScreenshotExtension.TestResult.FAILURE;
import static com.github.mictaege.arete.ScreenshotExtension.TestResult.SUCCESS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.mictaege.arete.Feature;
import com.github.mictaege.arete.Given;
import com.github.mictaege.arete.Scenario;
import com.github.mictaege.arete.ScreenshotExtension;
import com.github.mictaege.arete.Spec;
import com.github.mictaege.arete.Then;
import com.github.mictaege.arete.When;

@Tag("slow")
@Spec class AnotherFeatureSpec {

    @RegisterExtension
    public ScreenshotExtension screenshots = new ScreenshotExtension(new DummyScreenshotTaker(), SUCCESS, FAILURE);

    @Feature class MyFeature {

        @Scenario class MyScenario {
            @Given void aTest() {}
            @When void doing() {}
            @Then void expect() {}
        }

    }

    @Feature class MyOtherFeature {

        @Scenario class MyScenario {
            @Given void aTest() {}
            @When void doing() {}
            @Then void expect() {}
        }

        @Scenario class MyOtherScenario {
            @Given void anotherTest() {}
            @When void doingSomethingElse() {}
            @Then void expectDifferentResult() {
                assertThat(15, is(15));
            }

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
