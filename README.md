# A Gradle plugin that executes Java Jar files

The [*JarExec plugin*](http://bit.ly/JarExecPi) is now available to the community.

JarExec takes advantage of Gradle's [*build cache functionality*](https://docs.gradle.org/current/userguide/build_cache.html#build_cache). Tasks that are cacheable leverage Gradle's build time-reducing, productivity-improving [*incremental build feature*](https://docs.gradle.org/current/userguide/more_about_tasks.html#sec:up_to_date_checks).

In addition to the core Jar executing capability, if you pass it an optional classpath and the name of a class with a main method, it'll also run plain old non-executable Jars too. 

The plugin also adds a super useful *`jarhelper`* property to the build. With that helper, you can do things like retrieve resolved artifacts from Gradle's dependency cache. I discussed an example of such a use case in [*a recent Gradle forums discussion*](http://bit.ly/LngoJar).

You can read the [*JavaDoc here*](https://lingocoder.github.io/docs/javadoc/index.html).

# Usage

*Requires JDK 8+*

## Launch An Executable Jar

    /* (1) Apply the jarexec plugin using the plugins DSL. */
    plugins{
        id 'com.lingocoder.jarexec' version '0.4.7'
    }
		
    /* (2) Configure repositories. jcenter is preferred by Gradle.org. */
    repositories {jcenter()}

    dependencies {
        /* Declare your api|implementation|testImplementation dependencies like you normally would. */
        api 'com.example:foo:0.0.0'

        /* (3) Declare a dependency on the executable jar that jarexec's :execjar task will execute. */
        runtimeOnly 'org.raml.jaxrs:raml-to-jaxrs-cli:3.0.5:jar-with-dependencies'
    }

    jarexec{
        /* (4) This example's input file. */
        def raml = file("src/main/resources/api.raml")
 
        /* (5) This example's output directory. */
        def jaxrs = file("$buildDir/generated/jaxrs")

        /* (6) Fetch your executable from the dependency cache */
        def jaxrsCli = jarhelper.fetch("org.raml.jaxrs:raml-to-jaxrs-cli:3.0.5:jar-with-dependencies").orElse(raml)

        /* (7) Optionally, confirm that your fetched jar is executable. */
        assert jarhelper.checkExecutable(jaxrsCli);

        /* (8) Configure jarexec's 'args' property with your list of arguments. */
        args = ["-r", "lingocoder", "-d", jaxrs.absolutePath, raml.absolutePath]

        /* (9) Configure jarexec's 'jar' property with your executable jar. */
        jar = jaxrsCli

        /* (10) Configure jarexec's 'watchInFile' property with your input file. */
        watchInFile = raml

        /* (11) Configure jarexec's 'watchOutDir' property with an output directory 
         * to make the task build-cacheable. */
        watchOutDir = jaxrs
    }

From the command line, run:  

    $ gradle --build-cache execjar

## Launch A Non-Executable Jar

The steps are pretty much the same as above. But in this case you need to add the *`mainClass`* and *`classPath`* properties:

    ...

    jarexec{

        /* (i) A list of arguments and options the main class needs */

        args = [
            "-a", "L:\\ingocoder\\input\\files\\abi.gav.coordinates.1.txt",
            "-c", "L:\\ingocoder\\classes\\", 
            "-d", "L:\\ingocoder\\input\\files\\abi.dependencies.paths.1.txt",
            "-p", "com.lingocoder.poc"]

        /* (ii) The class path the main class needs. This is configurable by adding what your main
         * class requires (including directories) to whatever configuration that works for you */

        classpath = configurations.default

        /* (iii) Configure jarexec's 'jar' property with your executable jar. */

        jar = jarhelper.fetch('com.lingocoder:abi.cli:0.4.7').orElse('build/libs/abi.cli-0.4.7.jar')

        /* (iv) Configure jarexec's 'watchInFile' property with your input file. */

        mainClass = 'com.lingocoder.abi.app.AbiApp'

        /* (v) Configure jarexec's 'watchOutDir' property with an output directory 
         * to make the task build-cacheable. */

        watchOutDir = file("L:/ingocoder/classes/")
    }

    ...

In both of the above cases, anything written to stdout or stderr by the main class of the given jar, is piped to your console.   