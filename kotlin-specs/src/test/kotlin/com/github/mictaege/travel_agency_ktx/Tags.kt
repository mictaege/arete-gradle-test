package com.github.mictaege.travel_agency_ktx

import com.github.mictaege.arete.Ref
import com.github.mictaege.arete.StereoType
import com.github.mictaege.arete.StereoTypes.ACTOR
import com.github.mictaege.arete.StereoTypes.ENTITY
import com.github.mictaege.arete.StereoTypes.EXTERNAL
import com.github.mictaege.arete.StereoTypes.WORKFLOW
import org.junit.jupiter.api.Tag

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag("Traveller")
@StereoType(ACTOR)
annotation class ActorTraveller

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag("Accommodation")
@StereoType(ACTOR)
annotation class ActorAccommodation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag("Agency")
@StereoType(ACTOR)
annotation class ActorAgency

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag("Room")
@StereoType(ENTITY)
annotation class EntityRoom

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag("Offer")
@StereoType(ENTITY)
annotation class EntityOffer

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag("Reservation")
@StereoType(ENTITY)
annotation class EntityReservation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag("Payment-Service")
@StereoType(EXTERNAL)
annotation class ExternalPaymentService

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag("Booking")
@StereoType(WORKFLOW)
annotation class FlowBooking

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag("Mediation")
@StereoType(WORKFLOW)
annotation class FlowMediation

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@Ref(url = "https://github.com/mictaege/arete-gradle/issues/{0}", project = "arete-gradle")
annotation class GithubRef(val value: String)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Tag("Milestone-1")
annotation class Milestone1
