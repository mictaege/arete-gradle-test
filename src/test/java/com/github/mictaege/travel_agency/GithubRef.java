package com.github.mictaege.travel_agency;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.github.mictaege.arete.Ref;
import com.github.mictaege.arete.Refs;

/**
 * A {@link Ref} pointing to an issue of the arete-gradle GitHub repository,
 * e.g. {@code @GithubRef("14")} refers to
 * <a href="https://github.com/mictaege/arete-gradle/issues/14">issue 14</a>.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Repeatable(GithubRefs.class)
@Ref(url = "https://github.com/mictaege/arete-gradle/issues/")
public @interface GithubRef {

    /** The issue number, appended to the issue tracker URL of the meta {@link Ref}. */
    String value();

}
