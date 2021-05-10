package com.github.mictaege.arete_gradle_test;

import com.github.mictaege.arete.Describe;
import com.github.mictaege.arete.ItShould;
import com.github.mictaege.arete.Spec;

@Spec class XDescribeSpec {

    @Describe class ADescription {
        @ItShould void doSomething() {
        }
    }

}
