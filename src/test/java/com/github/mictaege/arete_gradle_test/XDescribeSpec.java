package com.github.mictaege.arete_gradle_test;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;

import com.github.mictaege.arete.Describe;
import com.github.mictaege.arete.ItShould;
import com.github.mictaege.arete.Spec;

@Tag("fast")
@Tag("internal")
@Spec class XDescribeSpec {

    @Describe class ADescription {
        @ItShould void doSomething() {
        }
    }

}
