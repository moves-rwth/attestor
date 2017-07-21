# Attestor
A Shape Analysis Tool based on Graph Grammars

## System Requirements

- [Java JDK 1.8 or higher][3]
- [Apache Maven][4]

## Installation

    $ git clone https://github.com/moves-rwth/attestor.git
    $ mvn install

## Documentation

The API document is available as part of this repository [here][1].
Furthermore, the theoretical foundations underlying Attestor are described in the following research papers:

- Hannah Arndt, Christina Jansen, Christoph Matheja, Thomas Noll: [Heap Abstraction Beyond Context-Freeness][5]. CoRR abs/1705.03754 (2017)
- Christina Jansen, Jens Katelaan, Christoph Matheja, Thomas Noll, Florian Zuleger: [Unified Reasoning About Robustness Properties of Symbolic-Heap Separation Logic][6]. ESOP: 611-638 (2017)
- Jonathan Heinen, Christina Jansen, Joost-Pieter Katoen, Thomas Noll: [Juggrnaut: using graph grammars for abstracting unbounded heap structures][7]. Formal Methods in System Design 47(2): 159-203 (2015)
- Jonathan Heinen, Christina Jansen, Joost-Pieter Katoen, Thomas Noll: [Verifying pointer programs using graph grammars][8]. Sci. Comput. Program. 97: 157-162 (2015)
- Christoph Matheja, Christina Jansen, Thomas Noll: [Tree-Like Grammars and Separation Logic][9]. APLAS: 90-108 (2015)
- Christina Jansen, Thomas Noll: [Generating Abstract Graph-Based Procedure Summaries for Pointer Programs][10]. ICGT 2014: 49-64



## Examples and Benchmarks

There is a [separate repository][2] that collects benchmarks and example programs that have been successfully analyzed.
Each benchmark can be executed individually using maven.

[1]: https://moves-rwth.github.io/attestor/doc/
[2]: https://github.com/moves-rwth/attestor-examples
[3]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
[4]: http://maven.apache.org/
[5]: https://arxiv.org/abs/1705.03754
[6]: https://link.springer.com/chapter/10.1007/978-3-662-54434-1_23
[7]: https://link.springer.com/article/10.1007/s10703-015-0236-1
[8]: http://www.sciencedirect.com/science/article/pii/S0167642313002967
[9]: https://link.springer.com/chapter/10.1007/978-3-319-26529-2_6
[10]: https://link.springer.com/chapter/10.1007/978-3-319-09108-2_4
