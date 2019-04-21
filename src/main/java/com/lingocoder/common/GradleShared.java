package com.lingocoder.common;

import static java.io.File.separator;
import static java.lang.System.getenv;

public class GradleShared {

    private static final String HOME = System.getProperty("user.home");

    public static final String G_USR_HOME_ENV =  "GRADLE";


    public static final String G_HOME_ENV =  "GRADLE_HOME";

    /** Expecting there to be either a $GRADLE_USER_HOME or a $GRADLE_HOME environment variable */
    public static final String GRADLE =
            getenv(G_USR_HOME_ENV) != null && !getenv(G_USR_HOME_ENV).isEmpty()
                    ? getenv(G_USR_HOME_ENV) : getenv(G_HOME_ENV) != null && !getenv(G_HOME_ENV).isEmpty()
                            ? getenv(G_HOME_ENV) : System.getProperty("gradle.user.home", new StringBuffer(HOME)
                    .append(separator).append(".gradle").toString());

    private GradleShared() { }
}
