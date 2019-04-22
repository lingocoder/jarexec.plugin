package com.lingocoder.jar;

import com.lingocoder.process.io.ProcessIO;
import com.lingocoder.process.ProcessHelper;
import com.lingocoder.io.CommandHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

/**
 * A utility that provides convenience methods for working with Java {@code jar}
 * files similar to the way they would be used on the command line.
 */
public class JarTool {

    private static Logger LOGGER = LoggerFactory.getLogger(JarTool.class);

    private final CommandHelper cmdHelp;
    private final ProcessHelper procHelp;

    /**
     * Create an instance with default state.
     */
    public JarTool() {

        this.cmdHelp = new CommandHelper();
        this.procHelp = new ProcessHelper();
    }

    /**
     * Run the jar tool. Not to be confused with "<em><strong>executing</strong> a jar file</em>". There's a different,
     * more appropriately-named method for that.
     *
     * @param commands The commands the jar tool will run. <em><strong>DO NOT</strong></em> prepend the commands
     *                 with the {@code jar} command. That would be telling the {@code jar} command to run itself.
     *
     * @return A composite result of the run.
     */
    public ProcessIO run(String...commands) {

        String[] cmdArray = this.cmdHelp.refreshCmds(Optional.empty(), commands);

        LOGGER.debug("From JarTool. Command to execute is: '{}'", this.cmdHelp.toString(cmdArray));

        return this.procHelp.runProcess(cmdArray);
    }

    /**
     * Execute a jar file in the JVM. Not to be confused with "<em><strong>running</strong> the jar tool</em>". There's a different,
     * more appropriately-named method for that.
     *
     * @param commands The commands that will be passed to the {@code Main-Class} of the jar that will be executed. <em><strong>DO NOT</strong></em> prepend the commands
     *                 with the neither the {@code java} command nor the {@code -jar} command. That would be telling {@code java} to run itself.
     *
     * @return A composite result of the execution.
     */
    public ProcessIO execute(String...commands) {

        String[] cmdArray = this.cmdHelp.refreshCmds(Optional.of("-"), commands);

        System.out.printf("From JarTool. Command to execute is: '%s'%n", this.cmdHelp.toString(cmdArray));

        return this.procHelp.runProcess(cmdArray);
    }
}
