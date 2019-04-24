package com.lingocoder.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Shared {

    public static File DEFAULT_TMP_FILE;

    static{
        try {
            DEFAULT_TMP_FILE = Files.createTempFile("default",null).toFile();
            DEFAULT_TMP_FILE.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static final String LOC_ERR_MSG = "'%s' is a non-existent location. Check that you have either:\n\n" +
            "\t1) Called a method using a path to an existing location.\n" +
            "\t2) Have a distribution of Maven or Gradle installed at their standard default locations.\n" +
            "\t3) Exported/Set an M2_REPO or GRADLE environment variable that references an existing location.\n" +
            "\t4) Executed this application with either a '-Dmaven.repo.local={your_maven_repo_location}' or '-Dgradle.user.home={your_gradle_user_home_location}'" +
            " System property configured appropriately for the specific runtime context.";

    public static final String NO_NULLS_ALLOWED = "'%' parameter cannot be '%'.";

    public static void rejectMissingParam(String paramName, String param) {
        if(param == null || param.isEmpty()){
            throw new IllegalArgumentException(String.format(NO_NULLS_ALLOWED, paramName, param));
        }
    }

    public static <T extends CharSequence, U>  void rejectMissingParam(T paramName, U param) {
        if(param == null){
            throw new IllegalArgumentException(String.format(NO_NULLS_ALLOWED, paramName, param));
        }
    }

    private Shared() {
    }
}
