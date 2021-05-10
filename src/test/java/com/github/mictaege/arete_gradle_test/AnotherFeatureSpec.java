package com.github.mictaege.arete_gradle_test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Disabled;

import com.github.mictaege.arete.Feature;
import com.github.mictaege.arete.Given;
import com.github.mictaege.arete.Scenario;
import com.github.mictaege.arete.Spec;
import com.github.mictaege.arete.Then;
import com.github.mictaege.arete.When;

@Spec class AnotherFeatureSpec {

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
