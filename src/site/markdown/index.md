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


## Status

[![Build Status](https://travis-ci.org/earcam/io.earcam.unexceptional.svg?branch=master)](https://travis-ci.org/earcam/io.earcam.unexceptional)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.earcam/io.earcam.unexceptional/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.earcam/io.earcam.unexceptional)
[![Javadoc](http://www.javadoc.io/badge/io.earcam/io.earcam.unexceptional/badge.svg?color=yellowgreen)](http://www.javadoc.io/doc/io.earcam/io.earcam.unexceptional)

## Licences
[![Licence: Apache](https://img.shields.io/badge/License-Apache-yellow.svg)](http://www.apache.org/licenses/LICENSE-2.0)
[![Licence: BSD](https://img.shields.io/badge/License-BSD-yellow.svg)](https://opensource.org/licenses/BSD)
[![Licence: Eclipse](https://img.shields.io/badge/License-Eclipse-yellow.svg)](https://www.eclipse.org/legal/epl-v10.html)
[![Licence: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)


----


<ul class="nav nav-list">
	<li class="nav-header">Quick Links</li>
	<li><a href="./dependency.html" title="Add Maven, Gradle, Ivy, SBT or download binary JAR">Include Dependency</a></li>
	<li><a href="./examples.html" title="Usage Snippets">Sample Examples</a></li>
	<li><a href="http://github.com/earcam/io.earcam.unexceptional/issues" title="Report Issue or Request Feature">Report Issue</a></li>
</ul>



[package-summary-java-util-function]: https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html