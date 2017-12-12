package de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar;

import de.rwth.i2.attestor.grammar.IndexMatcher;
import de.rwth.i2.attestor.grammar.materialization.communication.CannotMaterializeException;
import de.rwth.i2.attestor.grammar.materialization.indexedGrammar.IndexMaterializationStrategy;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.index.AbstractIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexVariable;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.iterator.TIntIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class can be used to match the indices of already matched graphs.
 * To this end it can instantiate the index variable with any sequence (such a variable may
 * exist in the "pattern" graph) and by materialising any occurring abstract index symbols (which
 * may exist in the "target" graph and if applicable in the instantiation sequence.
 * <p>
 * For determining the matches between single indices you must provide an IndexMatcher which is
 * also responsible for managing the index grammar.
 * You must also provide an indexMaterializer which is able to apply materialization rules to a
 * heapConfiguration.
 * <p>
 * This class will then take care that a consistent materialization is found for all nonterminals
 * and will fail if this is impossible.
 *
 * @author Hannah
 */
public class EmbeddingIndexChecker {

    private static final Logger logger = LogManager.getLogger("EmbeddingIndexChecker");


    final IndexMatcher indexMatcher;
    final IndexMaterializationStrategy indexMaterializer;


    public EmbeddingIndexChecker(IndexMatcher matcher, IndexMaterializationStrategy materializer) {

        this.indexMatcher = matcher;
        this.indexMaterializer = materializer;
    }

    /**
     * Computes the necessary materialization and instantiation to match the nonterminals of the
     * embedding in toAbstract. If this is possible it returns a appropriately materialised copy
     * of toAbstract and and instantiated copy of lhs
     *
     * @param toAbstract The "outer" graph
     * @param embedding  the embedding of the pattern into the graph toAbstract
     * @param lhs        the nonterminal with which the embedded pattern will be replaced
     * @return an IndexEmbeddingResult containing the materialised graph and the instantiated lhs
     * @throws CannotMatchException if the indices of the matched endges cannot be matched.
     */
    public IndexEmbeddingResult getIndexEmbeddingResult(HeapConfiguration toAbstract,
                                                        Matching embedding,
                                                        Nonterminal lhs) throws CannotMatchException {

        Map<AbstractIndexSymbol, List<IndexSymbol>> materializations = new LinkedHashMap<>();
        List<IndexSymbol> instantiation = new ArrayList<>();

        HeapConfiguration pattern = embedding.pattern();
        TIntIterator iterator = pattern.nonterminalEdges().iterator();
        while (iterator.hasNext()) {
            int nt = iterator.next();

            computeNecessaryChangesFor(nt, toAbstract, embedding, materializations, instantiation, pattern);

        }

        toAbstract = applyMaterializationsTo(toAbstract, materializations);
        pattern = applyInstantiationTo(pattern, instantiation);
        checkAppliedResult(toAbstract, embedding, pattern);

        lhs = applyInstantiationTo(lhs, instantiation);

        return new IndexEmbeddingResult(toAbstract, lhs);
    }

    private void computeNecessaryChangesFor(int nt, HeapConfiguration toAbstract, Matching embedding,
                                            Map<AbstractIndexSymbol, List<IndexSymbol>> materializations, List<IndexSymbol> instantiation,
                                            HeapConfiguration pattern) throws CannotMatchException {

        Nonterminal patternLabel = pattern.labelOf(nt);
        Nonterminal targetLabel = toAbstract.labelOf(embedding.match(nt));

        if (patternLabel instanceof IndexedNonterminal
                && targetLabel instanceof IndexedNonterminal) {

            IndexedNonterminal materializable = (IndexedNonterminal) targetLabel;
            materializable = applyCurrentMaterializationTo(materializations, materializable);
            IndexedNonterminal instantiable = (IndexedNonterminal) patternLabel;
            instantiable = applyInstantiationTo(instantiation, instantiable);

            if (!indexMatcher.canMatch(materializable, instantiable)) {
                throw new CannotMatchException();
            } else {

                updateWithNecessaryMaterialization(materializations, instantiation, materializable, instantiable);
                updateWithNecessaryInstantiation(instantiation, materializable, instantiable);
            }
        }
    }

    private void updateWithNecessaryInstantiation(List<IndexSymbol> instantiation, IndexedNonterminal materializable,
                                                  IndexedNonterminal instantiable) throws CannotMatchException {

        if (indexMatcher.needsInstantiation(materializable, instantiable)) {
            updateInstantiation(instantiation, indexMatcher.getNecessaryInstantiation(materializable, instantiable));
        }
    }

    private void updateWithNecessaryMaterialization(Map<AbstractIndexSymbol, List<IndexSymbol>> materializations,
                                                    List<IndexSymbol> instantiation, IndexedNonterminal materializable, IndexedNonterminal instantiable) {

        if (indexMatcher.needsMaterialization(materializable, instantiable)) {
            Pair<AbstractIndexSymbol, List<IndexSymbol>> materializationRule =
                    indexMatcher.getMaterializationRule(materializable, instantiable);
            updateMaterializations(materializations, materializationRule);
            updateInstantiation(instantiation, materializationRule);
        }
    }

    private Nonterminal applyInstantiationTo(Nonterminal lhs, List<IndexSymbol> instantiation) {

        if (!instantiation.isEmpty() && lhs instanceof IndexedNonterminal) {
            IndexedNonterminal iLhs = (IndexedNonterminal) lhs;
            lhs = iLhs.getWithProlongedIndex(instantiation);
        }
        return lhs;
    }


    /**
     * To avoid checking corner cases in the original compuation of matchings,
     * the indices with applied materialization and instantiation are checked for
     * equality.
     *
     * @param toAbstract the outer graph
     * @param embedding  the matching from pattern to outer graph elements
     * @param pattern    the embedded graph
     * @throws CannotMatchException if one of the indices does not match
     */
    private void checkAppliedResult(HeapConfiguration toAbstract, Matching embedding, HeapConfiguration pattern)
            throws CannotMatchException {

        TIntIterator iterator = pattern.nonterminalEdges().iterator();
        while (iterator.hasNext()) {
            int nt = iterator.next();

            Nonterminal patternLabel = pattern.labelOf(nt);
            Nonterminal targetLabel = toAbstract.labelOf(embedding.match(nt));
            if (!patternLabel.equals(targetLabel)) {
                throw new CannotMatchException();
            }
        }
    }


    /**
     * Applies all the materialization rules in materializations to the
     * graph hc.
     *
     * @param hc               the graph to which to apply the materializations
     * @param materializations the rules for materialization, e.g. X &#8594; ssX, Y &#8594; sZ
     * @return
     */
    private HeapConfiguration applyMaterializationsTo(HeapConfiguration hc,
                                                      Map<AbstractIndexSymbol, List<IndexSymbol>> materializations) {

        for (Entry<AbstractIndexSymbol, List<IndexSymbol>> rule : materializations.entrySet()) {
            try {
                hc = this.indexMaterializer.getMaterializedCloneWith(hc, rule.getKey(), rule.getValue());
            } catch (CannotMaterializeException e) {
                logger.error("materialization after index matching faild.");
                e.printStackTrace();
            }
        }
        return hc;
    }

    /**
     * Applies the instantiation rule to the graph pattern
     *
     * @param pattern       the graph to which the instantiation is applied
     * @param instantiation sequence
     * @return
     */
    private HeapConfiguration applyInstantiationTo(HeapConfiguration pattern, List<IndexSymbol> instantiation) {

        IndexSymbol indexVariable = IndexVariable.getIndexVariable();
        pattern = pattern.clone();
        HeapConfigurationBuilder builder = pattern.builder();
        TIntIterator edgeIter = pattern.nonterminalEdges().iterator();
        while (edgeIter.hasNext()) {
            int indexOfNonterminal = edgeIter.next();
            Nonterminal nonterminal = pattern.labelOf(indexOfNonterminal);
            if (nonterminal instanceof IndexedNonterminal) {
                IndexedNonterminal nonterminalToMaterialize = (IndexedNonterminal) nonterminal;
                if (nonterminalToMaterialize.getIndex().getLastIndexSymbol().equals(indexVariable)) {

                    Nonterminal nonterminalWithMaterializedIndex =
                            applyInstantiationTo(instantiation, nonterminalToMaterialize);
                    builder.replaceNonterminal(indexOfNonterminal, nonterminalWithMaterializedIndex);
                }

            }
        }
        return builder.build();
    }


    private void updateMaterializations(Map<AbstractIndexSymbol, List<IndexSymbol>> materializations,
                                        Pair<AbstractIndexSymbol, List<IndexSymbol>> newMaterializationRule) {

        applyNewMaterialiationTo(materializations, newMaterializationRule);

        if (!materializations.containsKey(newMaterializationRule.first())) {
            materializations.put(newMaterializationRule.first(), newMaterializationRule.second());
        }

    }

    private void updateInstantiation(List<IndexSymbol> instantiation,
                                     Pair<AbstractIndexSymbol, List<IndexSymbol>> newMaterializationRule) {

        if (!instantiation.isEmpty()) {
            materializeIn(instantiation, newMaterializationRule.first(), newMaterializationRule.second());
        }
    }

    private void updateInstantiation(List<IndexSymbol> instantiation, List<IndexSymbol> necessaryInstantiation) throws CannotMatchException {

        if (!instantiation.isEmpty() && !instantiation.equals(necessaryInstantiation)) {
            throw new CannotMatchException();
        } else instantiation.addAll(necessaryInstantiation);
    }

    /**
     * applies the new materialization to all the current materializations.
     * For example if materializations containsSubsumingState the rules
     * X -> sX,
     * Y -> ssX and
     * A -> ssB
     * and newMaterializations is (X,sZ),
     * materializations will afterwards consist of
     * X -> ssZ,
     * Y -> sssZ and
     * > -> ssB.
     *
     * @param materializations
     * @param newMaterialization
     */
    private void applyNewMaterialiationTo(
            Map<AbstractIndexSymbol, List<IndexSymbol>> materializations,
            Pair<AbstractIndexSymbol, List<IndexSymbol>> newMaterialization) {

        for (Entry<AbstractIndexSymbol, List<IndexSymbol>> materializationRule : materializations.entrySet()) {
            List<IndexSymbol> rhs = materializationRule.getValue();
            materializeIn(rhs, newMaterialization.first(), newMaterialization.second());
        }
    }

    /**
     * returns the last element in the index, i.e. c in [a,b,c].
     *
     * @param index the index containing the elements
     * @return the last symbol of index
     */
    private IndexSymbol getLastSymbolOf(List<IndexSymbol> index) {

        return index.get(index.size() - 1);
    }

    /**
     * materializes the given index using the rule lhs -> rhs.
     * For example if index = ssX, lhs = X, rhs = Z index is afterwards ssZ.
     * If the last symbol of index does not equal lhs nothing happens,
     * so for example calling index = ssY, lhs = X, rhs = Z will leave index unchanged.
     *
     * @param index the index to materialize
     * @param lhs   the abstract index symbol to materialized
     * @param rhs   the sequence of index symbols with which to materialize
     */
    private void materializeIn(List<IndexSymbol> index, IndexSymbol lhs, List<IndexSymbol> rhs) {

        if (getLastSymbolOf(index).equals(lhs)) {
            index.remove(index.size() - 1);
            index.addAll(rhs);
        }
    }

    private IndexedNonterminal applyCurrentMaterializationTo(
            Map<AbstractIndexSymbol, List<IndexSymbol>> currentMaterializations,
            IndexedNonterminal materializable) {

        IndexSymbol lastIndexSymbol = materializable.getIndex().getLastIndexSymbol();
        if (lastIndexSymbol instanceof AbstractIndexSymbol) {
            if (currentMaterializations.containsKey(lastIndexSymbol)) {
                return materializable.getWithProlongedIndex(currentMaterializations.get(lastIndexSymbol));
            }
        }
        return materializable;
    }

    private IndexedNonterminal applyInstantiationTo(List<IndexSymbol> instantiation, IndexedNonterminal instantiable) {

        IndexSymbol lastSymbol = instantiable.getIndex().getLastIndexSymbol();
        if (!instantiation.isEmpty() && lastSymbol instanceof IndexVariable) {
            return instantiable.getWithProlongedIndex(instantiation);
        } else {
            return instantiable;
        }

    }

}
