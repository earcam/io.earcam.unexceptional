
A small cohesive module, leveraging Java 8 functional capabilities to remove most of the boiler-plate of handling checked exceptions.  


<ul class="nav nav-list">
	<li class="nav-header">Unexceptional</li>
	<li>/ʌnɪkˈsɛpʃ(ə)n(ə)l,ʌnɛkˈsɛpʃ(ə)n(ə)l/&nbsp;&nbsp;<span title="Pronunciation" style="display:inline-block;"><input src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAQAAAC1QeVaAAAAi0lEQVQokWNgQAYyQFzGsIJBnwED8DNcBpK+DM8YfjMUokqxMRxg+A9m8TJsBLLSEFKMDCuBAv/hCncxfGWQhUn2gaVAktkMXkBSHmh0OwNU8D9csoHhO4MikN7BcAGb5H+GYiDdCTQYq2QubkkkY/E6CLtXdiJ7BTMQMnAHXxFm6IICvhwY8AYQLgCw2U9d90B8BAAAAABJRU5ErkJggg==" width="14" height="14" type="image" onclick="pronounce('unexceptional--_gb_1.mp3')" /></span></li>
	<li><i>adjective:</i> not out of the ordinary; usual.</li>
	<li><i>synonyms:</i> ordinary, average, typical, common, everyday, run-of-the-mill, middle-of-the-road, stock, mediocre, so-so, pedestrian, unremarkable, undistinguished, indifferent, unimpressive.</li>
</ul>

<br/>

<ul class="nav nav-list">
	<li class="nav-header">Emetic</li>
	<li>/ɪˈmɛtɪk/&nbsp;&nbsp;<span title="Pronunciation" style="display:inline-block;"><input src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAOCAQAAAC1QeVaAAAAi0lEQVQokWNgQAYyQFzGsIJBnwED8DNcBpK+DM8YfjMUokqxMRxg+A9m8TJsBLLSEFKMDCuBAv/hCncxfGWQhUn2gaVAktkMXkBSHmh0OwNU8D9csoHhO4MikN7BcAGb5H+GYiDdCTQYq2QubkkkY/E6CLtXdiJ7BTMQMnAHXxFm6IICvhwY8AYQLgCw2U9d90B8BAAAAABJRU5ErkJggg==" width="14" height="14" type="image" onclick="pronounce('emetic--_gb_1.8.mp3')" /></span></li>
	<li><i>adjective:</i> (of a substance) causing vomiting.</li>
</ul>

<br/>


This module consists of:
 
0. Types equivalent to those found in [java.util.function][package-summary-java-util-function] but with checked methods (declaring `Throwable`)
0. Static helper class, **Exceptional**, numerous utility methods covering:
    * Convert checked method handles to `java.util.function`al interfaces
    * Invoke checked functional types without `try-catch`
    * Concise IO stream usage (re-read as terse one-liner - arguably laconic over expressive), condensing _create-operate-close_.
    * _Dirty_ compiler trick; throwing a checked exception as unchecked
    * Factories for some common types where the constructor declares a checked exception (e.g. java.net.URI, java.net.URL)
    * Convert checked exceptions to unchecked equivalents
    * _Swallow_ exceptions (accepts throwables, but only swallows exceptions - errors are rethrown)  
    * Unwrap reflective exceptions
    
0. Simple mirror **EmeticStream**, wrapping `java.util.stream.Stream` providing equivalent methods taking checked functional types (terminates immediately on exception)

The intended use of this library is to make code more concise when handling checked exceptions for the **impossible** (logically we *know* this cannot occur) or **unrecoverable** (we've no alternative and cannot degrade with grace, game over) cases.

Please use responsibly, always consider if a raised exception can actually be handled, and if not, then could it be propagated with a more contextual type/message?  Be considerate to readers of logs, unlucky users and your future self.

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
	<li><a class="externalLink" href="https://maven-badges.herokuapp.com/maven-central/io.earcam/io.earcam.unexceptional" title="Maven Central"><img src="https://maven-badges.herokuapp.com/maven-central/io.earcam/io.earcam.unexceptional/badge.svg" alt="Maven Central" /></a></li>
	<li><a class="externalLink" href="http://www.javadoc.io/doc/io.earcam/io.earcam.unexceptional" title="JavaDoc"><img src="https://www.javadoc.io/badge/io.earcam/io.earcam.unexceptional.svg?color=green" alt="JavaDoc" /></a></li>
	<li><a class="externalLink" href="https://travis-ci.org/earcam/io.earcam.unexceptional" title="Travis CI"><img src="https://travis-ci.org/earcam/io.earcam.unexceptional.svg?branch=master" alt="Travis CI" /></a></li>
	<!-- shelving Circle CI for now
	<li><a class="externalLink" href="https://circleci.com/gh/earcam/io.earcam.unexceptional" title="Circle CI"><img src="https://circleci.com/gh/earcam/io.earcam.unexceptional.svg?style=svg" alt="Circle CI" /></a></li>
	-->
	<li><a href="https://ci.appveyor.com/project/earcam/io-earcam-unexceptional"><img src="https://ci.appveyor.com/api/projects/status/iwsmbc4n61m79ac4?svg=true"/></a></li>
	<li><a class="externalLink" href="https://sonarcloud.io/dashboard?id=io.earcam%3Aio.earcam.unexceptional" title="SonarQube Quality Gate"><img src="https://sonarcloud.io/api/badges/gate?key=io.earcam%3Aio.earcam.unexceptional" alt="Sonar Quality Gate" /></a></li>
	<li><a class="externalLink" href="https://codecov.io/github/earcam/io.earcam.unexceptional?branch=master" title="CodeCov"><img src="https://codecov.io/github/earcam/io.earcam.unexceptional/coverage.svg?branch=master" alt="CodeCov" /></a></li>
	<li><a href="https://scan.coverity.com/projects/earcam-io-earcam-unexceptional"><img alt="Coverity Scan Build Status" src="https://scan.coverity.com/projects/13461/badge.svg"/></a></li>
	<li><a href="https://bestpractices.coreinfrastructure.org/projects/1177"><img src="https://bestpractices.coreinfrastructure.org/projects/1177/badge"/></a></li>
	<li><a href="https://bitbucket.org/earcam/io.earcam.unexceptional/issues" title="Average time to resolve an issue"><img src="https://isitmaintained.com/badge/resolution/earcam/io.earcam.unexceptional.svg" alt="Average time to resolve an issue"/></a></li>
	<li><a href="https://bitbucket.org/earcam/io.earcam.unexceptional/issues?status=open" title="Percentage of issues still open"><img src="https://isitmaintained.com/badge/open/earcam/io.earcam.unexceptional.svg" alt="Percentage of issues still open"/></a></li>
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