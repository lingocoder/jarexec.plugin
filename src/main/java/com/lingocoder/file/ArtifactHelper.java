package com.lingocoder.file;

import com.lingocoder.common.MavenShared;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static com.lingocoder.common.Shared.LOC_ERR_MSG;
import static com.lingocoder.common.Shared.rejectMissingParam;

/**
 * A utility class that provides methods that convert sequences of strings
 * to the standard Maven-defined <em>{@code group:artifact:version} (aka, „GAV“)</em>
 * format.
 */
public class ArtifactHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger("ArtifactHelper");

    private final URI repoLocation;

    private final ArtifactCacheLocator cacheLocator = new ArtifactCacheLocator();

    /**
     * <p>Create an instance with default state. The created instance will attempt
     * to locate and work with a local file system dependency cache it assumes will be
     * installed at a standard default location. It will look for environment
     * variables such as <em>{@code M2_REPO}</em>, <em>{@code GRADLE_USER_HOME}</em>
     * and <em>{@code GRADLE_HOME}</em>. It will also look for a 
     * <em>{@code maven.local.repo}</em> System property.</p>
     * 
     * <p>If no such environment or property is found, it will assume the caller
     * intends for this instance to work with a local file system dependency cache located
     * in the home directory of the current user.</p>
     * 
     * <p>The methods of this instance operate in the context of the locations described
     * above.</p>
     */
    public ArtifactHelper() { LOGGER.debug("Initializing ArtifactHelper()");

        /* FIXME – Either a generic constructor or spin-off an Interface. */
        this.repoLocation = cacheLocator.locateMavenLocal().orElseThrow(MavenShared::mvnLocExcptn);

        if (!Files.exists(Paths.get(this.repoLocation))){
            throw new NonExistentLocationException(String.format(LOC_ERR_MSG, Paths.get(repoLocation).toFile().getAbsolutePath()));
        }
    }

    /**
     * <p>Create an instance that will do its work at the given {@code repoLocation}.</p>
     *  
     * <p>The created instance will attempt to locate and work with a local file system 
     * dependency cache it assumes will be installed at the specified {@link URI}.</p> 
     * 
     * <p>The methods of this instance operate in the context of the location of 
     * the specified {@link URI}.</p>
     * 
     * @param repoLocation The location on the local file system where a dependency cache can be found.
     */
    public ArtifactHelper(URI repoLocation) {LOGGER.debug("Initializing ArtifactHelper(URI repoLocation)");

        rejectMissingParam("repoLocation", repoLocation);

        if (!Files.exists(Paths.get(repoLocation))){
            throw new NonExistentLocationException(String.format(LOC_ERR_MSG, Paths.get(repoLocation).toFile().getAbsolutePath()));
        }
        this.repoLocation = repoLocation;
    }

    /**
     * Map the given {@code group} to a {@link Path}.
     * 
     * @param group The {@code group} to be resolved to a {@link Path}.
     * 
     * @return  An {@link Optional}-wrapped {@link Path} relative to this instances 
     *     {@code repoLocation} – if the location specified by the given group exists. 
     *     Otherwise, an {@link Optional#empty()} if no actual location exists for the given {@code group}.
     */
    public Optional<Path> groupToPath(final String group) {

        rejectMissingParam("group", group);

        Path path = Paths.get(repoLocation).resolve(Paths.get(group.replace(".", File.separator)));

        return Files.exists(path) ? Optional.of(path) : Optional.empty();
    }

    /**
     * Map the given {@code group} and {@code artifact} to a {@link Path}.
     * 
     * @param group The {@code group} to be resolved to a {@link Path}.
     * 
     * @param artifact The {@code artifact} to be resolved to a {@link Path}.
     * 
     * @return  An {@link Optional}-wrapped {@link Path} relative to this instances 
     *     {@code repoLocation} – if the location specified by the given {@code group} and {@code artifact}
     *     exists. Otherwise, an {@link Optional#empty()} if no actual location exists for the given {@code group} and {@code artifact}.
     */
    public Optional<Path> groupArtifactToPath(final String group, final String artifact) {

        rejectMissingParam("artifact", artifact);

        return this.groupToPath(group).map(p -> p.resolve(artifact));
    }

    /**
     * Map the given {@code group}, {@code artifact} and {@code version} to a {@link Path}.
     * 
     * @param group The {@code group} to be resolved to a {@link Path}.
     * 
     * @param artifact The {@code artifact} to be resolved to a {@link Path}.
     * 
     * @param version The {@code version} to be resolved to a {@link Path}.
     * 
     * @return  An {@link Optional}-wrapped {@link Path} relative to this instances 
     *     {@code repoLocation} – if the location specified by the given {@code group}, {@code artifact}
     *     and {@code version} exists. Otherwise, an {@link Optional#empty()} if no actual location exists
     *     for the given {@code group}, {@code artifact} and {@code version}.
     */
    public Optional<Path> groupArtifactVerToPath(final String group, final String artifact, final String version) {

        rejectMissingParam("version", version);

        return this.groupArtifactToPath(group, artifact).map(p -> p.resolve(version));
    }


    /**
     * Map the given {@code group}, {@code artifact}, {@code version}
     * and {@link Optional} {@code classifier} to a {@link Path}.
     * 
     * @param group The {@code group} to be resolved to a {@link Path}.
     * 
     * @param artifact The {@code artifact} to be resolved to a {@link Path}.
     * 
     * @param version The {@code version} to be resolved to a {@link Path}.
     * 
     * @param classifier An {@link Optional} {@code classifier} to be resolved to a {@link Path}.
     * 
     * @return  An {@link Optional}-wrapped {@link Path} relative to this instances 
     *     {@code repoLocation} – if the location specified by the given {@code group}, {@code artifact},
     *     {@code version} and {@code classifier} exists. Otherwise, an {@link Optional#empty()}
     *     if no actual location exists for the given {@code group}, {@code artifact}, {@code version} and {@code classifier}.
     */
    public Optional<Path> gavClassifierToPath(final String group, final String artifact, final String version, final Optional<String> classifier) {

        rejectMissingParam("version", version);

        StringBuffer artifactName = new StringBuffer(artifact);

        artifactName.append("-").append(version);

        classifier.ifPresent(s -> artifactName.append("-").append(s));

        artifactName.append(".jar");

        return this.groupArtifactToPath(group, artifact).map(p -> p.resolve(version).resolve(artifactName.toString())).filter(Files::exists);
    }

    /**
     * Fetch a dependency from the local Maven repo as a jar {@code File}.
     *
     * @param coordinates Expected to be in the standard colon-delimited form:
     *                   „group:artifact:version[:classifier]“.
     * @return - The jar {@code File} located at the given coordinates.
     */
    public Optional<File> fromMavenLocal(final String coordinates) {

        String group = this.extractGroup(coordinates);
        final String artifact = this.extractArtifact(coordinates);
        final String version = this.extractVersion(coordinates);
        Optional<String> classifier = this.extractClassifier(coordinates);

        return this.gavClassifierToPath(group,artifact,version,classifier).filter(Files::exists).map(Path::toFile);
    }

    /**
     * Map the given {@code coordinates} to only the {@code group} part.
     * 
     *  @param coordinates Expected to be in the standard colon-delimited form:
     *                   „group:artifact:version[:classifier]“.
     * 
     * @return The {@code group} part of the given {@code coordinates}.
     */
    public String extractGroup(final String coordinates){

        return this.splitCoordinates(coordinates)[0];

    }

    /**
     * Map the given {@code coordinates} to only the {@code artifact} part.
     * 
     *  @param coordinates Expected to be in the standard colon-delimited form:
     *                   „group:artifact:version[:classifier]“.
     * 
     * @return The {@code artifact} part of the given {@code coordinates}.
     */
    public String extractArtifact(final String coordinates){

        return this.splitCoordinates(coordinates)[1];

    }

    /**
     * Map the given {@code coordinates} to only the {@code version} part.
     * 
     *  @param coordinates Expected to be in the standard colon-delimited form:
     *                   „group:artifact:version[:classifier]“.
     * 
     * @return The {@code version} part of the given {@code coordinates}.
     */
    public String extractVersion(final String coordinates){

        return this.splitCoordinates(coordinates)[2];
    }

    /**
     * Map the given {@code coordinates} to only the {@code classifier} part.
     * 
     *  @param coordinates Expected to be in the standard colon-delimited form:
     *                   „group:artifact:version[:classifier]“.
     * 
     * @return The {@code classifier} part of the given {@code coordinates}.
     */
    public Optional<String> extractClassifier(final String coordinates){

        String[] gavCoords = this.splitCoordinates(coordinates);

        return gavCoords.length == 4 ? Optional.of( gavCoords[3]) : Optional.empty();
    }

    /**
     * Map the given {@code coordinates} to an array.
     * 
     *  @param coordinates Expected to be in the standard colon-delimited form:
     *                   „group:artifact:version[:classifier]“.
     * 
     * @return A {@link String} array whose elements will be  the colon-delimited parts
     *  of the given {@code coordinates}.
     */
    public String[] splitCoordinates(final String coordinates){

        rejectMissingParam("coordinates", coordinates);

        return coordinates.split(":");
    }

    /**
     * Looks for a local Maven repository in one of three standard locations on the file system:
     * <ol>
     *     <li><em>The default local Maven repository location: <strong>$HOME/.m2/repository/</strong></em>
     *     <li><em>A path referenced by an <strong>$M2_REPO_ENV</strong> environment variable</em></li>
     *     <li><em>A path referenced by a <strong>-Dmaven.local.repo=${...}</strong> system property</em></li>
     * </ol>
     * @return Either the location on the file system of a local Maven repository, or {@code Optional.empty()} if none was found.
     */
    public Optional<URI> locateMavenLocal(){

        return this.repoLocation != null ? Optional.of(this.repoLocation) : this.cacheLocator.locateMavenLocal();
    }
}
