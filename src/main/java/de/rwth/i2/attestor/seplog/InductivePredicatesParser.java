package de.rwth.i2.attestor.seplog;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.main.scene.DefaultScene;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.main.scene.SceneObject;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

/**
 * Parser to create hyperedge replacement grammars from a
 * restricted fragment of inductive predicate definitions
 * in symbolic-heap separation logic.
 *
 * @author Christoph
 */
public class InductivePredicatesParser extends SceneObject {

    public InductivePredicatesParser(SceneObject sceneObject) {
        super(sceneObject);
    }


    public Grammar parseFromFile(String filename) throws IOException {

        return parse(
                CharStreams.fromFileName(filename)
        );
    }

    public Grammar parseFromString(String input) {

        return parse(
                CharStreams.fromString(input)
        );
    }

    private Grammar parse(CharStream charStream) {

        SeparationLogicLexer lexer = new SeparationLogicLexer(charStream);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SeparationLogicParser parser = new SeparationLogicParser(tokens);

        ParseTree tree = parser.sid();
        ParseTreeWalker walker = new ParseTreeWalker();

        NonterminalCollector nonterminalCollector = new NonterminalCollector(scene());
        walker.walk(nonterminalCollector, tree);

        GrammarExtractor grammarExtractor = new GrammarExtractor(scene());
        walker.walk(grammarExtractor, tree);

        return grammarExtractor.getGrammar();
    }
}
