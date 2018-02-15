package de.rwth.i2.attestor.grammar.canonicalization.defaultGrammar;

import de.rwth.i2.attestor.grammar.CollapsedHeapConfiguration;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import gnu.trove.list.array.TIntArrayList;

/**
 * This class provides the methodExecution to canonicalisation which are specific for
 * default (non-indexed) grammars.
 *
 * @author Hannah
 */
public class DefaultCanonicalizationHelper implements CanonicalizationHelper {

    public final EmbeddingCheckerProvider provider;

    /**
     * @param provider generates a EmbeddingChecker for given graph and pattern. Responsible
     *                 to generate the correct one for given communication and semantics.
     */
    public DefaultCanonicalizationHelper(EmbeddingCheckerProvider provider) {

        super();
        this.provider = provider;
    }

    @Override
    public HeapConfiguration tryReplaceMatching(HeapConfiguration toAbstract,
                                           HeapConfiguration rhs, Nonterminal lhs) {

        AbstractMatchingChecker checker = provider.getEmbeddingChecker(toAbstract, rhs);

        if (checker.hasMatching()) {
            Matching embedding = checker.getMatching();
            HeapConfiguration result = replaceEmbeddingBy(toAbstract, embedding, lhs);
            return result;
        }
        return null;
    }

    /**
     * replaces the embedding in  abstracted by the given nonterminal
     *
     * @param toAbstract the outer graph.
     * @param embedding       the embedding of the inner graph in the outer graph
     * @param nonterminal     the nonterminal to replace the embedding
     */
    private HeapConfiguration replaceEmbeddingBy(HeapConfiguration toAbstract, Matching embedding,
                                                 Nonterminal nonterminal) {

        return toAbstract.clone().builder().replaceMatching(embedding, nonterminal).build();
    }

    @Override
    public HeapConfiguration prepareHeapForCanonicalization(HeapConfiguration toAbstract) {

        return toAbstract;
    }

    @Override
    public HeapConfiguration tryReplaceMatching(HeapConfiguration toAbstract,
                                                CollapsedHeapConfiguration rhs,
                                                Nonterminal lhs) {

        HeapConfiguration collapsedHc = rhs.getCollapsed();
        AbstractMatchingChecker checker = provider.getEmbeddingChecker(toAbstract, collapsedHc);

        if (checker.hasMatching()) {

            Matching embedding = checker.getMatching();
            return replaceCollapsedEmbeddingBy(toAbstract, embedding, lhs, rhs.getOriginalToCollapsedExternalIndices());
        }
        return null;
    }

    private HeapConfiguration replaceCollapsedEmbeddingBy(HeapConfiguration toAbstract,
                                                          Matching embedding,
                                                          Nonterminal nonterminal,
                                                          TIntArrayList externalIndicesMap) {

        return toAbstract.clone().builder().replaceMatchingWithCollapsedExternals(
                embedding, nonterminal, externalIndicesMap
        ).build();
    }


}