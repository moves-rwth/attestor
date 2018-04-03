![Build Status Attestor](https://img.shields.io/travis/moves-rwth/attestor.svg)
![Benchmarks Status](https://img.shields.io/travis/moves-rwth/attestor-examples.svg?label=benchmarks)
[![Attestor on Maven Central](https://img.shields.io/maven-central/v/de.rwth.i2/attestor.svg)](https://mvnrepository.com/artifact/de.rwth.i2/attestor)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
![Languages](https://img.shields.io/github/languages/top/moves-rwth/attestor.svg)
![Last update](https://img.shields.io/github/last-commit/moves-rwth/attestor.svg)

Attestor is a graph-based tool for analysing Java programs operating on dynamic data structures. It involves the generation of an abstract state space employing user-supplied graph grammars. LTL model checking is then applied to the generated state space, supporting both structural and functional correctness properties. The analysis is fully automated, procedure-modular, and provides visual feedback including counterexamples in case of property violations.

# Contents

### Quickstart

* [What is Attestor?](#what-is-attestor)
* [System Requirements](#system-requirements)
* [Reproducing Benchmarks](#reproducing-benchmarks) (without installation)
* [Installation](#installation)
* [Getting Started](#getting-started)

### Documentation

* [Detailed Example Analysis](#detailed-example-analysis)
* [Options & Settings](#options--settings)
* [Graphical User Interface](#graphical-user-interface)
* [Architecture](#architecture)
* [Glossary](#glossary)

### People & Publications

* [People](#people)
* [Publications](#publications)

# Quickstart

## What is Attestor?

Pointers constitute an essential concept in modern programming languages, and are used for implementing dynamic data structures like lists, trees etc. 
However, many software bugs can be traced back to the erroneous use of pointers by e.g. dereferencing null pointers or accidentally pointing to wrong parts of the heap.
Due to the unbounded state spaces arising from dynamic data structures, pointer errors are hard to detect.
Automated tool support for validation of pointer programs that provides meaningful debugging information in case of violations is therefore highly desirable.

Attestor is a verification tool that attempts to achieve both of these goals.
To this aim, it first constructs an abstract state space of the input program by means of symbolic execution.
Each state depicts both links between heap objects and values of program variables using a graph representation.
Abstraction is performed on state level by means of graph grammars.
They specify the data structures maintained by the program, and describe how to summarise substructures of the heap in order to obtain a finite representation.
After automatically labelling each state with propositions that provide information about structural properties such as reachability or heap shapes, the actual verification task is performed in a second step. 
To this aim, the abstract state space is checked against a user-defined LTL specification.
In case of violations, a counterexample is provided.

In summary, Attestor's main features can be characterised as follows:

* It employs context-free graph grammars as a formal underpinning for defining heap abstractions. 
These grammars enable local heap concretisation and thus naturally provide implicit abstract semantics.
* The full instruction set of Java bytecode is handled. 
Program actions that are outside the scope of our analysis, such as arithmetic operations or Boolean tests on payload data, are handled by (safe) over-approximation.
* Specifications are given by linear-time temporal logic (LTL) formulae, which support a rich set of program properties ranging from memory safety over shape, reachability or balancedness to properties such as full traversal or exact preservation of the heap structure.
* Except for expecting a graph grammar that specifies the data structures handled by a program, the analysis is fully automated. 
In particular, no program annotations are required.
* Modular reasoning is supported in the form of contracts that summarise the effect of executing a (recursive) procedure.
These contracts can be automatically derived or manually specified.
* Feedback is provided through a comprehensive report including (minimal) non-spurious counterexamples in case of property violations.
* Tool functionality is made accessible through the command line as well as a graphical user and an application programming interface.

## System Requirements

The following software has to be installed prior to the installation of Attestor:

- [Java JDK 1.8][3]
- [Apache Maven][4]
- (Windows) Since Attestor uses [soot][13], please make sure that rt.jar is in your CLASSPATH.

## Reproducing Benchmarks

We distribute executable bundles consisting of the latest stable Attestor version together will all benchmarks on [maven central](https://mvnrepository.com/artifact/de.rwth.i2/attestor-examples). 
To run benchmarks on the latest version of Attestor, please proceed as follows.

##### Unix-based operating systems
     $ git clone https://github.com/moves-rwth/attestor-examples.git
     $ chmod +x run.sh
     $ ./run.sh
     
##### All operating systems
     $ git clone https://github.com/moves-rwth/attestor-examples.git
     $ mvn clean install exec:exec@run

Given the [system requirements](#system-requirements), no installation of Attestor is required to reproduce and comprehend previously reported benchmark results. 
We collect all benchmarks in a [separate repository](https://github.com/moves-rwth/attestor-examples) including auxiliary scripts to install, run and evaluate all benchmarks.
Please confer the documentation in the [benchmark repository](https://github.com/moves-rwth/attestor-examples) for further details.

## Installation

We distribute executable `.jar` files of stable Attestor releases on [maven central](https://mvnrepository.com/artifact/de.rwth.i2/attestor). 
To install the latest version of Attestor, please proceed as follows: 

    $ git clone https://github.com/moves-rwth/attestor.git
    $ mvn install

Please note that the installation requires an internet connection as maven will install additional dependencies.

## Getting Started

After installation, an executable jar file is created in the directory `target` within the cloned repository. The name of executable jar is of the form 

     attestor-<VERSION>-jar-with-dependencies.jar 

where `<VERSION>` is the previously cloned version of the Attestor repository.
To execute Attestor, it suffices to run

     $ java -jar attestor-<VERSION>-jar-with-dependencies.jar 

from within the `target` directory. 
This should display a help page explaining all available [command line options](https://github.com/moves-rwth/attestor/wiki/Command-Line-Options).
Since the above jar file contains all dependencies, it is safe to rename it and move the file to a more convenient directory.

Detailed step-by-step instructions on using Attestor to analyze Java programs are found on the [detailed walkthrough page](https://github.com/moves-rwth/attestor/wiki/Walkthrough).

Furthermore, we maintain a collection of running examples (including source code, user-defined graph grammars, and configuration files) in a [separate repository](https://github.com/moves-rwth/attestor-examples). All of these examples can be directly executed using provided auxiliary scripts. Please confer the documentation in the examples repository for further details.

# Documentation

## Detailed Example Analysis

An instructive example on using Attestor for program analyisis is found in our  [Wiki](https://github.com/moves-rwth/attestor/wiki/Walkthrough).

## Options & Settings

Attestor can be configured using various [command line options](https://github.com/moves-rwth/attestor/wiki/Command-Line-Options). Alternatively, it is possible to store such a configuration in a dedicated [settings file](https://github.com/moves-rwth/attestor/wiki/Settings-file).
In particular, the options allow to pass [linear temporal logic specifications](https://github.com/moves-rwth/attestor/wiki/LTL-Specifications) to be verified for the provided Java program.

It is possible to manually supply [graph grammars](https://github.com/moves-rwth/attestor/wiki/Graph-Grammar-Syntax), [initial heaps](https://github.com/moves-rwth/attestor/wiki/Heap-Configuration-Syntax), and [contracts](https://github.com/moves-rwth/attestor/wiki/Contract-File-Syntax) to Attestor.

Please confer the respective pages for further details.

## Graphical User Interface

[State spaces](https://github.com/moves-rwth/attestor/wiki/Glossary#state-space), [counterexamples](https://github.com/moves-rwth/attestor/wiki/Glossary#counterexample), [contracts](https://github.com/moves-rwth/attestor/wiki/Glossary#contract), and [graph grammars](https://github.com/moves-rwth/attestor/wiki/Glossary#hyperedge-replacement-grammar) can be exported by Attestor to a webpage for graphical exploration.
The corresponding export options are found [here](https://github.com/moves-rwth/attestor/wiki/Command-Line-Options#export-options).

Moreover, a brief explanation of our graphical notation for [heap configurations](https://github.com/moves-rwth/attestor/wiki/Glossary#heap-configuration) is found [here](https://github.com/moves-rwth/attestor/wiki/Graphical-Notation).

## Architecture

Attestor is organised in modular phases, for example `marking generation`, `state space generation`, and `model-checking`. Please confer our [Wiki page on Attestor's architecture](https://github.com/moves-rwth/attestor/wiki/Architecture) for further details.

## Glossary

A [glossary](https://github.com/moves-rwth/attestor/wiki/Glossary) providing brief explanations of most technical terms is found in our Wiki. 

# People & Publications

## People

Attestor is developed by the [Chair for Software Modeling and Verification](https://moves.rwth-aachen.de/) at [RWTH Aachen University](http://www.rwth-aachen.de/) by

* [Christoph Matheja](http://moves.rwth-aachen.de/people/cmatheja/),
* Hannah Arndt,
* [Christina Jansen](http://moves.rwth-aachen.de/people/cjansen/), and
* [Thomas Noll](https://moves.rwth-aachen.de/people/noll/).

## Publications 

- Hannah Arndt, Christina Jansen, Joost-Pieter Katoen, Christoph Matheja, Thomas Noll: Let this Graph be your Witness! An Attestor for Verifying Java Pointer Programs. CAV 2018, to appear.
- Christina Jansen: [Static Analysis of Pointer Programs - Linking Graph Grammars
and Separation Logic][17]. PhD Thesis. RWTH Aachen University, 2017. 
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
[17]: http://dx.doi.org/10.18154/RWTH-2017-09657
