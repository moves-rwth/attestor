package de.rwth.i2.attestor.seplog;

import de.rwth.i2.attestor.main.scene.Scene;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * A listener for SID parse trees, which creates all nonterminals, which have been defined
 * on the left-hand side of an SID rule.
 *
 * @author Christoph
 */
class NonterminalCollector extends SeparationLogicBaseListener {

    /**
     * The scene used to create nonterminals.
     */
    private Scene scene;

    /**
     * Stores the label of the next nonterminal that should be created.
     */
    private String label;

    /**
     * Stores for each parameter whether it corresponds to a reduction tentacle.
     */
    private List<Boolean> reductionTentacles;

    /**
     * @param scene The global scene used for object initialization.
     */
    NonterminalCollector(@Nonnull Scene scene) {

        this.scene = scene;
    }


    @Override
    public void enterSidRuleHead(SeparationLogicParser.SidRuleHeadContext ctx) {

        reductionTentacles = new ArrayList<>();
    }

    @Override
    public void enterPredicateSymbol(SeparationLogicParser.PredicateSymbolContext ctx) {

        label = ctx.getText();
    }

    @Override
    public void enterFreeVariableDeclaration(SeparationLogicParser.FreeVariableDeclarationContext ctx) {

        // if a free variable is marked as a "reduction tentacle" then there will be an additional
        // context child to indicate this.
        reductionTentacles.add(ctx.getChildCount() > 1);
    }

    @Override
    public void exitSidRuleHead(SeparationLogicParser.SidRuleHeadContext ctx) {

        int rank = reductionTentacles.size();
        boolean[] isReductionTentacle = new boolean[rank];
        for(int i=0; i < rank; i++) {
            isReductionTentacle[i] = reductionTentacles.get(i);
        }

        scene.createNonterminal(label, rank, isReductionTentacle);

        // reset temporary data
        reductionTentacles = null;
        label = null;
    }
}
