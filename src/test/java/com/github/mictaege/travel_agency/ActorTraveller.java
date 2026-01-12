package com.github.mictaege.travel_agency;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.Tag;

@Target(TYPE)
@Retention(RUNTIME)
@Tag("actor-traveller")
public @interface ActorTraveller {
}
