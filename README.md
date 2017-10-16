![Build Status](https://travis-ci.org/moves-rwth/attestor.svg?branch=master)

# Attestor

Attestor is a [shape analysis][11] tool that attempts to discover precise
invariants about the data structures employed by a Java program.
It is based on various notions of [graph grammars][12] as a formal underpinning.

## Why Shape Analysis?

Many software bugs can be traced back to the erroneous use of pointers,
i.e., references to memory addresses. They constitute an essential
concept in modern programming languages, and are used for implementing
dynamic data structures like lists, trees etc. Due to the resulting
unbounded state spaces, pointer errors are hard to detect in sequential
programs. A shape analysis, such as the one implemented by Attestor, supports
developers twofold:

1. It supports automatic verification of the absence of (certain) pointer-related
   errors.
2. It provides useful debugging information in case errors have been detected.


## System Requirements

The following software has to be installed prior to the installation of Attestor:

- [Java JDK 1.8][3]
- [Apache Maven][4]
- (Windows) Since Attestor uses [soot][13], please make sure that rt.jar is in your CLASSPATH.


## Installation

    $ git clone https://github.com/moves-rwth/attestor.git
    $ mvn install

Please note that the installation requires an internet connection as maven will install additional dependencies.
  
## Running Attestor

The simplest (and recommended) way to use Attestor is to create a new benchmark project that contains the code to be analyzed.
To get started, there is a [separate repository][2] that collects benchmarks and example programs that have been successfully analyzed.
Each benchmark collection can be executed individually by running the following maven command from the benchmark's root directory:

    $ mvn clean install exec:exec@run

We also provide a maven archetype that allows to create new benchmark projects that include a small running example.
Details on creating new benchmark projects are found [here][14].

It is also possible to directly run attestor from the command-line using individual settings files.
Instructions on running attestor from the command-line can be found in the [wiki][15].
The format and possible options of Attestor's settings are also explained in the [wiki][16].

## Documentation and Publications

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

