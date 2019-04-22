package com.lingocoder.process.io;

/**
 * A POJO that contains the results of the {@link com.lingocoder.process.ProcessHelper}.
 */
public class ProcessIO {

    private final boolean ok;

    private final String stdout;

    private final String stderr;

    /**
     * Create an immutable instance.
     * 
     * @param ok A binary flag indicating the success or failure of running a native operating system {@link Process}.
     * @param stdout The output message produced by the successful running of a native operating system {@link Process}.
     * @param stderr The error message produced by the unsuccessful running of a native operating system {@link Process}.
     */
    public ProcessIO(boolean ok, String stdout, String stderr) {

        this.ok = ok;

        this.stdout = stdout;

        this.stderr = stderr;
    }

    /**
     * Access the binary flag that indicates the success or failure of running a native operating system {@link Process}.
     * 
     * @return {@code true} if the process ran without errors. Otherwise, {@code false}.
     */
    public boolean isOk() {
        return this.ok;
    }

    /**
     * Access the {@code stdout} produced by running a {@link Process}.
     * 
     * @return The output message produced by the successful running of a native operating system {@link Process}.
     */
    public String getStdout() {
        return this.stdout;
    }

    /**
     * Access the {@code stderr} produced by running a {@link Process}.
     * 
     * @return The error message produced by the unsuccessful running of a native operating system {@link Process}.
     */
    public String getStderr() {
        return this.stderr;
    }

    /**
     * Map this instance to its {@link String} representation.
     * @return A {@link String} of the form: „<em>ProcessIO{ok=true, stdout='foo...', stderr='boo...'}</em>“.
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ProcessIO{");
        sb.append("ok=").append(ok);
        sb.append(", stdout='").append(stdout).append('\'');
        sb.append(", stderr='").append(stderr).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
