package com.lingocoder.file;

/**
 * <p>Specifies the contract that concrete cached artifact finder implementations must conform to.</p>
 *
 * <p>Consumers of this API are responsible for installing and configuring the dependency management system
 * that concrete implementations would be called on to query. That means setting the standard environment
 * variables prescribed by specific build systems. For example, <em>{@code GRADLE_USER_HOME}</em> or whatever.</p>
 */
public interface CachedArtifactFinder {

    /**
     * <p>Searches a dependency management system's locally-cached artifacts.</p>
     *
     * <p>This operation assumes artifact caches can be found at certain
     * standard locations on the local file system.</p>
     *
     * @param coordinates A non-empty, colon-delimited string of the form: „<em>group:artifact:version[:classifier]</em>“.
     *                    Commonly referred to as „<em>GAV</em>“ coordinates, this is the standard addressing scheme
     *                    dependency management systems use to distinguish individual modules from one another.
     *
     * @return A composite result that {@link java.util.Optional}ly holds the artifact specified by the given {@code coordinates} (<em>if found</em>)
     * and its parent folder.
     */
    RepoResult find(String coordinates);
}
