package com.github.mictaege.travel_agency;

import com.github.mictaege.arete.StereoType;
import org.junit.jupiter.api.Tag;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static com.github.mictaege.arete.StereoTypes.ENTITY;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target(TYPE)
@Retention(RUNTIME)
@Tag("Reservation")
@StereoType(ENTITY)
public @interface EntityReservation {
}
