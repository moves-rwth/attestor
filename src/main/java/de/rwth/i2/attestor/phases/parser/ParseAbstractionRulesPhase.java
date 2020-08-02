package de.rwth.i2.attestor.phases.parser;

import de.rwth.i2.attestor.domain.RelativeInteger;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.FileReader;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.InputSettings;
import de.rwth.i2.attestor.phases.transformers.AbstractionRuleTransformer;
import de.rwth.i2.attestor.phases.transformers.GrammarTransformer;
import de.rwth.i2.attestor.phases.transformers.InputSettingsTransformer;
import de.rwth.i2.attestor.predicateAnalysis.AbstractionRule;
import de.rwth.i2.attestor.predicateAnalysis.RelativeIntegerLexer;
import de.rwth.i2.attestor.predicateAnalysis.RelativeIntegerParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ParseAbstractionRulesPhase extends AbstractPhase implements AbstractionRuleTransformer<RelativeInteger> {
    private Grammar grammar;
    private final Map<Nonterminal, Map<Integer, AbstractionRuleExpression>> backward = new HashMap<>();
    private final Map<Nonterminal, Map<Integer, Map<Integer, AbstractionRuleExpression>>> forward = new HashMap<>();
    private final BasicNonterminal.Factory nonterminalFactory = new BasicNonterminal.Factory();

    public ParseAbstractionRulesPhase(Scene scene) {
        super(scene);
    }

    @Override
    public AbstractionRule<RelativeInteger> getAbstractionRule() {
        return new AbstractionRule<RelativeInteger>() {
            @Override
            public Map<Integer, RelativeInteger> abstractForward(RelativeInteger index, Nonterminal nt, HeapConfiguration rule) {
                return forward.get(nt).get(grammar.getRulePosition(nt, rule)).entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().evaluate(index, null)));
            }

            @Override
            public RelativeInteger abstractBackward(Map<Integer, RelativeInteger> assign, Nonterminal nt, HeapConfiguration rule) {
                return backward.get(nt).get(grammar.getRulePosition(nt, rule)).evaluate(null, assign);
            }
        };
    }

    @Override
    public String getName() {
        return "Parse abstraction rule";
    }

    @Override
    public void executePhase() throws IOException {
        grammar = getPhase(GrammarTransformer.class).getGrammar();
        InputSettings inputSettings = getPhase(InputSettingsTransformer.class).getInputSettings();

        for (String file : inputSettings.getUserDefinedAbstractionRuleFiles()) {
            loadAbstractionRule(file);
        }
    }

    @Override
    public void logSummary() {

    }

    @Override
    public boolean isVerificationPhase() {
        return false;
    }

    private void loadAbstractionRule(String file) throws FileNotFoundException {
        if (forward.size() != 0 || backward.size() != 0) {
            logger.debug("Extending previously set abstraction rule.");
        }

        String str = FileReader.read(file);
        JSONArray array = new JSONArray(str);
        for (int i = 0; i < array.length(); i++) {
            JSONObject fragment = array.getJSONObject(i);
            Nonterminal nt = nonterminalFactory.get(fragment.getString("nonterminal"));

            // forward
            JSONArray mappings = fragment.getJSONArray("forward");
            for (int j = 0; j < mappings.length(); j++) {
                forward.computeIfAbsent(nt, key -> new HashMap<>());
                JSONArray mapping = mappings.getJSONArray(j);
                for (int k = 0; k < mapping.length(); k++) {
                    forward.get(nt).computeIfAbsent(j, key -> new HashMap<>());
                    String idx = mapping.getJSONArray(k).getString(0);
                    String expression = mapping.getJSONArray(k).getString(1);
                    forward.get(nt).get(j).put(Integer.getInteger(idx), parseExpression(expression));
                }
            }

            // backward
            JSONArray expressions = fragment.getJSONArray("backward");
            for (int j = 0; j < expressions.length(); j++) {
                backward.computeIfAbsent(nt, key -> new HashMap<>());
                backward.get(nt).put(j, parseExpression(expressions.getString(j)));
            }
        }
    }

    private AbstractionRuleExpression parseExpression(String expression) {
        CharStream stream = CharStreams.fromString(expression);
        RelativeIntegerLexer lexer = new RelativeIntegerLexer(stream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        RelativeIntegerParser parser = new RelativeIntegerParser(tokens);
        ParseTreeWalker walker = new ParseTreeWalker();
        ParseTree tree = parser.expr();
        RelativeIntegerExtractor listener = new RelativeIntegerExtractor();
        walker.walk(listener, tree);
        return listener.getResult();
    }
}
