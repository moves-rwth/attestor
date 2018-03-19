![Build Status Attestor](https://travis-ci.org/moves-rwth/attestor.svg?branch=master)

# About

Pointers constitute an essential concept in modern programming languages, and are used for implementing dynamic data structures like lists, trees etc. 
However, many software bugs can be traced back to the erroneous use of pointers by e.g. dereferencing null pointers or accidentally pointing to wrong parts of the heap.
Due to the resulting unbounded state spaces, pointer errors are hard to detect.
Automated tool support for validation of pointer programs that provides meaningful debugging information in case of violations is therefore highly desirable.

Attestor is a verification tool that attempts to achieve both of these goals.
To this aim, it first constructs an abstract state space of the input program by means of symbolic execution. Each state depicts both links between heap objects and values of program variables using a graph representation. Abstraction is performed on state level by means of graph grammars. They specify the data structures maintained by the program, and describe how to summarise substructures of the heap in order to obtain a finite representation. After labelling each state with propositions that provide information about structural properties such as reachability or heap shapes, the actual verification task is performed in a second step. To this aim, the abstract state space is checked against a user-defined LTL specification.  In case of violations, a counterexample is provided.

In summary, Attestor's main features can be characterized as follows:

* It employs context-free graph grammars as a formal underpinning for defining heap abstractions.These grammars enable local heap concretisation and thus naturally provide implicit abstract semantics.
* The full instruction set of Java bytecode is handled. Program actions that are outside the scope of our analysis, such as arithmetic operations or Boolean tests on payload data, are handled by (safe) over-approximation.
* Specifications are given by linear-time temporal logic (LTL) formulae which support a rich set of program properties, ranging from memory safety over shape, reachability or balancedness to properties such as full traversal or preservation of the exact heap structure.
* Except for expecting a graph grammar that specifies the data structures handled by a program, the analysis is fully automated. In particular, no program annotations are required.
* Modular reasoning is supported in the form of contracts that summarise the effect of executing a (recursive) procedure.
These contracts can be automatically derived or manually specified.
* Feedback is provided through a comprehensive report including (minimal) non-spurious counterexamples in case of property violations.
* Its functionality is made accessible through the command line as well as a graphical user and an application programming interface.



# System Requirements

The following software has to be installed prior to the installation of Attestor:

- [Java JDK 1.8][3]
- [Apache Maven][4]
- (Windows) Since Attestor uses [soot][13], please make sure that rt.jar is in your CLASSPATH.


# Installation

    $ git clone https://github.com/moves-rwth/attestor.git
    $ mvn install

Please note that the installation requires an internet connection as maven will install additional dependencies.
  
# Running Attestor

![Build Status Benchmarks](https://travis-ci.org/moves-rwth/attestor-examples.svg?branch=master)
(status of examples repository)

The simplest (and recommended) way to use Attestor is to create a new benchmark project that contains the code to be analyzed.
To get started, there is a [separate repository][2] that collects benchmarks and example programs that have been successfully analyzed.
Each benchmark collection can be executed individually by running the following maven command from the benchmark's root directory:

    $ mvn clean install exec:exec@run

We also provide a maven archetype that allows to create new benchmark projects that include a small running example.
Details on creating new benchmark projects are found [here][14].

It is also possible to directly run attestor from the command-line using individual settings files.
Instructions on running attestor from the command-line can be found in the [wiki][15].
The format and possible abstractionOptions of Attestor's settings are also explained in the [wiki][16].

# Documentation and Publications

The API document is available as part of this repository [here][1].
Furthermore, the theoretical foundations underlying Attestor are described in the following research papers:

- Christina Jansen, Jens Katelaan, Christoph Matheja, Thomas Noll, Florian Zuleger: [Unified Reasoning About Robustness Properties of Symbolic-Heap Separation Logic][6]. ESOP: 611-638 (2017)
- Jonathan Heinen, Christina Jansen, Joost-Pieter Katoen, Thomas Noll: [Juggrnaut: using graph grammars for abstracting unbounded heap structures][7]. Formal Methods in System Design 47(2): 159-203 (2015)
- Jonathan Heinen, Christina Jansen, Joost-Pieter Katoen, Thomas Noll: [Verifying pointer programs using graph grammars][8]. Sci. Comput. Program. 97: 157-162 (2015)
- Christoph Matheja, Christina Jansen, Thomas Noll: [Tree-Like Grammars and Separation Logic][9]. APLAS: 90-108 (2015)
- Christina Jansen, Thomas Noll: [Generating Abstract Graph-Based Procedure Summaries for Pointer Programs][10]. ICGT 2014: 49-64



[1]: https://moves-rwth.github.io/attestor/doc/
[2]: https://github.com/moves-rwth/attestor-examples/tree/stable
[3]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
[4]: http://maven.apache.org/
[5]: https://arxiv.org/abs/1705.03754
[6]: https://link.springer.com/chapter/10.1007/978-3-662-54434-1_23
[7]: https://link.springer.com/article/10.1007/s10703-015-0236-1
[8]: http://www.sciencedirect.com/science/article/pii/S0167642313002967
[9]: https://link.springer.com/chapter/10.1007/978-3-319-26529-2_6
[10]: https://link.springer.com/chapter/10.1007/978-3-319-09108-2_4
[11]: https://en.wikipedia.org/wiki/Shape_analysis_(program_analysis)
[12]: https://en.wikipedia.org/wiki/Graph_rewriting
[13]: https://github.com/Sable/soot
[14]: https://github.com/moves-rwth/attestor-examples/tree/archetype
[15]: https://github.com/moves-rwth/attestor/wiki/Running-Attestor-from-the-command-line
[16]: https://github.com/moves-rwth/attestor/wiki/Settings-file

