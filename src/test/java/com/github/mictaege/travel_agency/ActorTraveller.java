package com.github.mictaege.travel_agency;

import static com.github.mictaege.arete.StereoTypes.ACTOR;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.mictaege.arete.StereoType;
import org.junit.jupiter.api.Tag;

@Target(TYPE)
@Retention(RUNTIME)
@Tag("traveller")
@StereoType(ACTOR)
public @interface ActorTraveller {
}
