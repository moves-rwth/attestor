package de.rwth.i2.attestor.seplog;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

/**
 * Parser to construct a HeapConfiguration from a formula in a
 * restricted fragment in symbolic heap separation logic with
 * inductive predicate definitions.
 *
 * This parser requires for every predicate symbol that the
 * scene already contains a corresponding nonterminal symbol
 * whose label matches the predicate symbol.
 */
public class SymbolicHeapParser extends SceneObject {

    public SymbolicHeapParser(SceneObject otherObject) {
        super(otherObject);
    }

    public HeapConfiguration parseFromString(String input) {

        return parse(
                CharStreams.fromString(input)
        );
    }

    public HeapConfiguration parseFromFile(String filename) throws IOException {

        return parse(
                CharStreams.fromFileName(filename)
        );
    }

    private HeapConfiguration parse(CharStream charStream) {

        SeparationLogicLexer lexer = new SeparationLogicLexer(charStream);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SeparationLogicParser parser = new SeparationLogicParser(tokens);

        ParseTree tree = parser.heap();
        ParseTreeWalker walker = new ParseTreeWalker();

        VariableExtractor variableExtractor = new VariableExtractor();
        walker.walk(variableExtractor, tree);

        HeapConfigurationExtractor heapExtractor = new HeapConfigurationExtractor(scene(),variableExtractor);

        walker.walk(heapExtractor, tree);

        return heapExtractor.getHeapConfiguration();
    }

}
