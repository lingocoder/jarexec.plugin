# A Gradle plugin that executes Java Jar files

The [*JarExec plugin*](http://bit.ly/JarExecPi) is now available to the community.

In addition to the core Jar executing capability, if you pass it an optional classpath and the name of a class with a main method, it'll also run plain old non-executable Jars too. 

The plugin also adds a super useful *`jarhelper`* property to the build. With that helper, you can do things like retrieve resolved artifacts from Gradle's dependency cache. I discussed an example of such a use case in [*a recent Gradle forums discussion*](http://bit.ly/LngoJar).

Please have a play-around with the plugin and let me know if it foots your bill?