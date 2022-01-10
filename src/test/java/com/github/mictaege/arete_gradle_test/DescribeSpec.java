package com.github.mictaege.arete_gradle_test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import com.github.mictaege.arete.Describe;
import com.github.mictaege.arete.ExampleSource;
import com.github.mictaege.arete.Examples;
import com.github.mictaege.arete.ItShould;
import com.github.mictaege.arete.Spec;

@Spec class DescribeSpec {

    @Describe class ADescription {
        @ItShould void doSomething() {
        }

        @Examples(desc = "Examples for doing something", pattern = "{0} * 2 => {1}", srcMethod = "doSomething")
        void doSomething(final int a, final int expected) {
            assertThat(a * 2, is(expected));
        }

        void doSomething(final ExampleSource s) {
            s.example(s.given(5), s.then(10));
            s.example(s.given(10), s.then(20));
            s.example(s.given(20), s.then(40));
        }

    }

}
