package com.lingocoder.process.io;

/**
 * A POJO that contains the results of the {@link com.lingocoder.process.ProcessHelper}.
 */
public class ProcessIO {

    private final boolean ok;

    private final String stdout;

    private final String stderr;

    public ProcessIO(boolean ok, String stdout, String stderr) {

        this.ok = ok;

        this.stdout = stdout;

        this.stderr = stderr;
    }

    public boolean isOk() {
        return this.ok;
    }

    public String getStdout() {
        return this.stdout;
    }

    public String getStderr() {
        return this.stderr;
    }

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
