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

    public MavenLocalFileFinder() {
        this.artifactHelp = new ArtifactHelper();
        this.repoLocation = this.artifactHelp.locateMavenLocal().orElseThrow(MavenShared::mvnLocExcptn);
    }

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
