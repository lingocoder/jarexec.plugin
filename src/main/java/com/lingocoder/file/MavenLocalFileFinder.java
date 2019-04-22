package com.lingocoder.file;

import com.lingocoder.common.MavenShared;

import java.io.File;
import java.net.URI;

/**
 * <p>Searches a locally-installed Maven repository for locally-cached artifacts.</p>
 *
 * <p>Consumers of this class are responsible for installing and configuring Maven with
 * the standard Maven environment variables. For example, <em>{@code M2_REPO}</em>.</p>
 */
public class MavenLocalFileFinder implements CachedArtifactFinder {

    private final URI repoLocation;

    private final ArtifactHelper artifactHelp;

    /**
     * <p>Create an instance with default state. The created instance will attempt
     * to locate and work with a Maven installation's local file system dependency
     * cache it assumes will be installed at a standard default location. It will
     * look for an <em>{@code M2_REPO}</em> environment variable It will also look
     * for a <em>{@code maven.local.repo}</em> System property.</p>
     * 
     * <p>If no such environment or property is found, it will assume the caller 
     * intends for this instance to work with a local Maven dependency cache
     * located in the home directory of the current user.</p>
     * 
     * <p>The methods of this instance operate in the context of the locations described
     * above.</p>
     */
    public MavenLocalFileFinder() {
        this.artifactHelp = new ArtifactHelper();
        this.repoLocation = this.artifactHelp.locateMavenLocal().orElseThrow(MavenShared::mvnLocExcptn);
    }

    /**
     * <p>Create an instance that will do its work at the given {@code repoLocation}.</p>
     *  
     * <p>The created instance will attempt to locate and work with a Maven installation's
     * local file system dependency cache it assumes will be installed at the specified
     * {@link URI}.</p>
     * 
     * <p>The methods of this instance operate in the context of the location of 
     * the specified {@link URI}.</p>
     * 
     * @param repoLocation The location on the local file system where a dependency cache can be found.
     */
    public MavenLocalFileFinder(URI repoLocation) {
        this.repoLocation = repoLocation;
        this.artifactHelp = new ArtifactHelper(repoLocation);
    }

    @Override
    /**
     * {@inheritDoc}
     */
    public RepoResult find(String coordinates) {

        /* TODO – Allow empty version coordinate */

        String group = artifactHelp.extractGroup(coordinates);

        String artifact = artifactHelp.extractArtifact(coordinates);

        String version = artifactHelp.extractVersion(coordinates);

        File nullObj = new File("@d\\e#/v@;n;u<l!l");

        return new DefaultRepoResult(artifactHelp.groupArtifactVerToPath(group,artifact,version).map(path -> path.toFile()).orElse(nullObj), artifactHelp.fromMavenLocal(coordinates).orElse(nullObj));
    }
}
