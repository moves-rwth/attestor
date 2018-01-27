package de.rwth.i2.attestor.grammar.canonicalization.indexedGrammar;

import de.rwth.i2.attestor.grammar.CollapsedHeapConfiguration;
import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationHelper;
import de.rwth.i2.attestor.grammar.canonicalization.EmbeddingCheckerProvider;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.AbstractMatchingChecker;
import de.rwth.i2.attestor.programState.indexedState.index.IndexCanonizationStrategy;
import gnu.trove.list.array.TIntArrayList;

/**
 * This class provides the methodExecution to canonicalisation which are specific for
 * indexed grammars.
 *
 * @author Hannah
 */
public class IndexedCanonicalizationHelper implements CanonicalizationHelper {

    public final IndexCanonizationStrategy indexCanonizationStrategy;
    public final EmbeddingCheckerProvider checkerProvider;
    public final EmbeddingIndexChecker indexChecker;

    /**
     * @param indexCanonicalizer canonicalises the indices before the graph is canonicalised.
     *                           Also responsible to canonicalise them only when admissible
     * @param checkerProvider    generates a EmbeddingChecker for given graph and pattern. Responsible
     *                           to generate the correct one for given communication and semantics.
     * @param indexChecker       responsible to match the indices of embeddings provided my the embeddingChecker
     */
    public IndexedCanonicalizationHelper(IndexCanonizationStrategy indexCanonicalizer,
                                         EmbeddingCheckerProvider checkerProvider,
                                         EmbeddingIndexChecker indexChecker) {

        super();
        this.indexCanonizationStrategy = indexCanonicalizer;
        this.checkerProvider = checkerProvider;
        this.indexChecker = indexChecker;
    }

    @Override
    public HeapConfiguration tryReplaceMatching(HeapConfiguration heapConfiguration,
                                           HeapConfiguration rhs, Nonterminal lhs) {

        HeapConfiguration result = null;

        AbstractMatchingChecker checker =
                checkerProvider.getEmbeddingChecker(heapConfiguration, rhs);

        if (checker.hasMatching()) {
            Matching embedding = checker.getMatching();
            try {
                IndexEmbeddingResult res =
                        indexChecker.getIndexEmbeddingResult(heapConfiguration, embedding, lhs);

                result = replaceEmbeddingBy(res.getMaterializedToAbstract(),
                        embedding, res.getInstantiatedLhs());
            } catch (CannotMatchException e) {
                //this may happen. continue as if no matching has been found.
            }
        }
        return result;
    }

    /**
     * replaces the embedding in  abstracted by the given nonterminal
     *
     * @param toAbstract  the outer graph.
     * @param embedding   the embedding of the inner graph in the outer graph
     * @param nonterminal the nonterminal to replace the embedding
     */
    private HeapConfiguration replaceEmbeddingBy(HeapConfiguration toAbstract, Matching embedding, Nonterminal nonterminal) {

        toAbstract = toAbstract.clone();
        return toAbstract.builder()
                .replaceMatching(embedding, nonterminal)
                .build();
    }

    /**
     * For indexed HeapConfigurations this performs index canonicalization.
     */
    @Override
    public HeapConfiguration prepareHeapForCanonicalization(HeapConfiguration toAbstract) {

        HeapConfiguration heap = toAbstract.clone();
        indexCanonizationStrategy.canonizeIndex(heap);
        return heap;
    }

    @Override
    public HeapConfiguration tryReplaceMatching(HeapConfiguration toAbstract, CollapsedHeapConfiguration rhs, Nonterminal lhs) {

        HeapConfiguration result = null;

        AbstractMatchingChecker checker =
                checkerProvider.getEmbeddingChecker(toAbstract, rhs.getCollapsed());

        if (checker.hasMatching()) {
            Matching embedding = checker.getMatching();
            try {
                IndexEmbeddingResult res =
                        indexChecker.getIndexEmbeddingResult(toAbstract, embedding, lhs);

                result = replaceCollapsedEmbeddingBy(res.getMaterializedToAbstract(),
                        embedding, res.getInstantiatedLhs(), rhs.getOriginalToCollapsedExternalIndices());
            } catch (CannotMatchException e) {
                //this may happen. continue as if no matching has been found.
            }
        }
        return result;
    }

    private HeapConfiguration replaceCollapsedEmbeddingBy(HeapConfiguration toAbstract,
                                                          Matching embedding,
                                                          Nonterminal nonterminal,
                                                          TIntArrayList externalIndicesMap) {

        toAbstract = toAbstract.clone();
        return toAbstract.builder()
                .replaceMatchingWithCollapsedExternals(embedding, nonterminal, externalIndicesMap)
                .build();
    }


}
