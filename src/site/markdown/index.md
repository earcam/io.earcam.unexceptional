# Unexceptional

<p>
/ʌnɪkˈsɛpʃ(ə)n(ə)l,ʌnɛkˈsɛpʃ(ə)n(ə)l/&nbsp;&nbsp;<span title="Pronunciation" style="display:inline-block;"><input src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAQAAAC1QeVaAAAAi0lEQVQokWNgQAYyQFzGsIJBnwED8DNcBpK+DM8YfjMUokqxMRxg+A9m8TJsBLLSEFKMDCuBAv/hCncxfGWQhUn2gaVAktkMXkBSHmh0OwNU8D9csoHhO4MikN7BcAGb5H+GYiDdCTQYq2QubkkkY/E6CLtXdiJ7BTMQMnAHXxFm6IICvhwY8AYQLgCw2U9d90B8BAAAAABJRU5ErkJggg==" width="14" height="14" type="image" onclick="pronounce('unexceptional--_gb_1.mp3')" />
</span>
</p>

_adjective:_ 
	not out of the ordinary; usual.

_synonyms:_
	ordinary, average, typical, common, everyday, run-of-the-mill, middle-of-the-road, stock, mediocre, so-so, pedestrian, unremarkable, undistinguished, indifferent, unimpressive.
    

## Description

A small cohesive module, leveraging Java 8 functional capabilities to remove some of the boiler-plate of handling checked exceptions.  

Comprising of:
 
0. Types equivalent to those found in [java.util.function][package-summary-java-util-function] but with checked methods (declaring `Throwable`)
0. Static helper class, **Exceptional**, numerous utility methods covering:
    * Unchecking method handles to functional interfaces from the `java.util.function` family
    * Invoking checked lambdas/method-references without the need for try-catch
    * Converting checked exceptions to unchecked equivalents
    * A dirty compiler trick for throwing a checked exception as unchecked (you should never need this)
0. Simple wrapper **EmeticStream** around `java.util.stream.Stream` with similar methods taking checked functional types

The intended use of this library is to make code more concise when handling checked exceptions for the **impossible** (logically we *know* this cannot occur) or **unrecoverable** (we've no alternative and cannot degrade with grace, game over) cases.

Please use responsibly, always consider if a raised exception can actually be handled, and if not, then could it be wrapped with a more contextual type/message? (be considerate to readers of logs, unlucky users and your future self).


### Exceptional

A static utility class, with methods to:

* Convert checked exceptions to unchecked
* Invoke checked methods (via method-handle), automatically wrapping checked exceptions with appropriate unchecked equivalent
* Throw checked exceptions without declaring (not advisable)
* Unwrap reflective exceptions
* Factories for some common types where the constructor declares a checked exception (e.g. java.net.URI, java.net.URL)
* Highly concise handling for most common IO stream usage (re-read as terse one-liner - arguably laconic over expressive), condensing create-operate-close into a single call.
* Method to _swallow_ exceptions (accepting throwables, but only swallowing exceptions - errors are  rethrown)  


### EmeticStream

**Emetic**
<p>
/ɪˈmɛtɪk/&nbsp;&nbsp;<span title="Pronunciation" style="display:inline-block;"><input src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAQAAAC1QeVaAAAAi0lEQVQokWNgQAYyQFzGsIJBnwED8DNcBpK+DM8YfjMUokqxMRxg+A9m8TJsBLLSEFKMDCuBAv/hCncxfGWQhUn2gaVAktkMXkBSHmh0OwNU8D9csoHhO4MikN7BcAGb5H+GYiDdCTQYq2QubkkkY/E6CLtXdiJ7BTMQMnAHXxFm6IICvhwY8AYQLgCw2U9d90B8BAAAAABJRU5ErkJggg==" width="14" height="14" type="image" onclick="pronounce('emetic--_gb_1.8.mp3')" />
</span>
</p>

_adjective:_
	(of a substance) causing vomiting.

A mirror of `java.util.stream.Stream` accepting the checked variants of `java.util.function`.  Terminates immediately on exception.



----


<ul class="nav nav-list">
	<li class="nav-header">Quick Links</li>
	<li><a href="./dependency.html" title="Add Maven, Gradle, Ivy, SBT or download binary JAR">Include Dependency</a></li>
	<li><a href="./examples.html" title="Usage Snippets">Sample Examples</a></li>
	<li><a href="https://github.com/earcam/io.earcam.unexceptional/issues" title="Report Issue or Request Feature">Report Issue</a></li>
</ul>

<br/>

<ul class="nav nav-list">
	<li class="nav-header">Status</li>
	<li><a class="externalLink" href="https://travis-ci.org/earcam/io.earcam.unexceptional" title="Travis CI"><img src="https://travis-ci.org/earcam/io.earcam.unexceptional.svg?branch=master" alt="Travis CI" /></a></li>
	<li><a class="externalLink" href="https://circleci.com/gh/earcam/io.earcam.unexceptional" title="Circle CI"><img src="https://circleci.com/gh/earcam/io.earcam.unexceptional.svg?style=svg" alt="Circle CI" /></a></li>
	<li><a class="externalLink" href="https://maven-badges.herokuapp.com/maven-central/io.earcam/io.earcam.unexceptional" title="Maven Central"><img src="https://maven-badges.herokuapp.com/maven-central/io.earcam/io.earcam.unexceptional/badge.svg" alt="Maven Central" /></a></li>
	<li><a class="externalLink" href="http://www.javadoc.io/doc/io.earcam/io.earcam.unexceptional" title="JavaDoc"><img src="http://www.javadoc.io/badge/io.earcam/io.earcam.unexceptional.svg?color=green" alt="JavaDoc" /></a></li>
	<li><a class="externalLink" href="https://sonarcloud.io/dashboard?id=io.earcam%3Aio.earcam.unexceptional" title="SonarQube Quality Gate"><img src="https://sonarcloud.io/api/badges/gate?key=io.earcam%3Aio.earcam.unexceptional" alt="Sonar Quality Gate" /></a></li>
	<li><a class="externalLink" href="https://codecov.io/github/earcam/io.earcam.unexceptional?branch=master" title="CodeCov"><img src="http://codecov.io/github/earcam/io.earcam.unexceptional/coverage.svg?branch=master" alt="CodeCov" /></a></li>
</ul>

<br/>

<ul class="nav nav-list">
	<li class="nav-header">Supported JVMs</li>
	<li><a class="externalLink" href="https://www.azul.com/downloads/zulu/" title="Azul Zulu JDK8"><img src="https://img.shields.io/badge/JDK8-Azul_Zulu-2B60DE.svg" alt="Azul JDK8" /></a></li>
	<li><a class="externalLink" href="https://www.oracle.com/technetwork/java/javase" title="Oracle Hotspot JDK8"><img src="https://img.shields.io/badge/JDK8-Oracle_Hotspot-red.svg" alt="Oracle JDK8" /></a></li>
	<li><a class="externalLink" href="http://openjdk.java.net/" title="OpenJDK JDK8"><img src="https://img.shields.io/badge/JDK8-OpenJDK-orange.svg" alt="OpenJDK JDK8" /></a></li>
	<li><a class="externalLink" href="https://www.ibm.com/developerworks/java/jdk" title="IBM JDK8"><img src="https://img.shields.io/badge/JDK8-IBM-blue.svg" alt="IBM JDK8" /></a></li>
</ul>

<br/>

<ul class="nav nav-list">
	<li class="nav-header">Licences</li>
	<li><a class="externalLink" href="https://opensource.org/licenses/BSD" title="Licence: BSD"><img src="https://img.shields.io/badge/License-BSD-yellow.svg" alt="Licence: BSD" /></a></li>
	<li><a class="externalLink" href="https://www.eclipse.org/legal/epl-v10.html" title="Licence: Eclipse"><img src="https://img.shields.io/badge/License-Eclipse-yellow.svg" alt="Licence: Eclipse" /></a></li>
	<li><a class="externalLink" href="http://www.apache.org/licenses/LICENSE-2.0" title="Licence: Apache"><img src="https://img.shields.io/badge/License-Apache-yellow.svg" alt="Licence: Apache" /></a></li>
	<li><a class="externalLink" href="https://opensource.org/licenses/MIT" title="Licence: MIT"><img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="Licence: MIT" /></a></li>
</ul>


[package-summary-java-util-function]: https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html