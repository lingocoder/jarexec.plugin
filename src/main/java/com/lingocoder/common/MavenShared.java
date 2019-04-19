package com.lingocoder.common;

import com.lingocoder.file.NonExistentLocationException;

import java.io.File;

import static java.io.File.separator;
import static java.lang.System.getenv;

public class MavenShared {

    private static final String HOME = System.getProperty("user.home");

    public static final File DEFAULT_MVN_LOCAL = new File(HOME + separator + ".m2" + separator + "repository");

    public static final String LOCATED_MVN_LOCAL = "Located a local Maven Repo at '{}'";

    public static final String MVN_LOCAL_NOT_FOUND = "Attempted to locate a local Maven Repo at '{}'. Not found";

    public static final String M2_REPO_ENV = "M2_REPO";

    /** There might or might not be a file separator on the end */
    public static final String M2_REPO =
            getenv(M2_REPO_ENV) == null || getenv(M2_REPO_ENV).isEmpty()
                    ? System.getProperty("maven.repo.local",
                    DEFAULT_MVN_LOCAL.getAbsolutePath())
                    : getenv(M2_REPO_ENV);


    private MavenShared() {
    }

    public static NonExistentLocationException mvnLocExcptn() {
        return new NonExistentLocationException(String.format(MVN_LOCAL_NOT_FOUND.replace("{}", "%s"), System.getProperty("user.home")));
    }
}
