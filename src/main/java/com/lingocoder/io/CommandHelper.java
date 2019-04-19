package com.lingocoder.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * A utility that provides convenience methods for working with command lines.
 */
public class CommandHelper {

    private final List<String> jarCmds = new ArrayList<>();


    public CommandHelper() {
        this.jarCmds.addAll(Arrays.asList("java", "-jar"));

    }

    /**
     * Produces a flattened representation of the given {@link String} array.
     *
     * @param args A sequence of {@link String}s that will be flattened into a single {@link String}.
     *
     * @return All of the elements of the given {@code args} param, concatenated into a single {@link String}.
     */
    public String toString(String...args) {
        StringBuilder str = new StringBuilder();

        for (String arg : args) {
            str.append(arg.trim()).append(" ");
        }

        return str.toString().trim();
    }

    /**
     * Produces a {@link String} array that contains commands that the {@code java} tool could execute from the command line.
     *
     * @param aJar A {@link File} representing a {@code jar}.
     *
     * @param args The sequence of commands or arguments that the main class within the given {@code aJar} param would process.
     *
     * @return A {@link String} array that expands to: „<em>{@code java -jar ${aJar} ${args}...}</em>“
     */
    public String[] toCmdArray(File aJar, List<String> args) {

/*        if(!aJar.exists()){ throw new IllegalArgumentException("'" + aJar.getAbsolutePath() + "' does not exist.");}*/

        jarCmds.add(2, aJar.getPath()); /* Looks like "java -jar /some/path/to/some.jar" at this point */

        jarCmds.addAll(args);

        return jarCmds.toArray(new String[args.size()]);
    }

    /**
     * Produces a {@link String} that the {@code java} tool could execute from the command line.
     *
     * @param aJar A {@link File} representing a {@code jar}.
     *
     * @param args The sequence of commands or arguments that the main class within the given {@code aJar} param would process.
     *
     * @return A {@link String} of the form: „<em>{@code java -jar ${aJar} ${args}...}</em>“
     */
    public String toCommand(File aJar, List<String> args) {

        return this.toString(this.toCmdArray(aJar, args));
    }

    /**
     * Produces {@link String} array containing one of two Java command line tools to execute, followed by the given commands/arguments.
     *
     * @param hyphen An {@link Optional} containing either an „{@code Optional.of(‚-‘)}“ or „{@code Optional.empty()}“.
     *
     * @param commands The sequence of commands or arguments that the particular selected program would process.
     *
     * @return „<em>{@code java -jar ${commands}...}</em>“ if the {@code hypen} param is „<em>{@code Optional.of(‚-‘)}<em>“. Otherwise, returns „<em>{@code jar ${commands}}</em>“ if the {@code hypen} param is „{@code Optional.empty()}“ (<em>or anything other than „<em>{@code Optional.of(‚-‘)}</em>“).
     */
    public String[] refreshCmds(Optional<String> hyphen, String... commands) {

        List<String> cmdList = new ArrayList<>(commands.length + 1);

        int off;

        if(hyphen.isPresent() && hyphen.get().equals("-")){

            cmdList.add(0, "java");

            cmdList.add(1, "-jar");

            off = 2;
        } else {cmdList.add(0, "jar"); off = 1;}

        for (String command : commands) {

            cmdList.add(off++, command);
        }

        return cmdList.toArray(new String[0]);
    }
}
