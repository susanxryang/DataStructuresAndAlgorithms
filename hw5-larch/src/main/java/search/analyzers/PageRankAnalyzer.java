package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.concrete.dictionaries.KVPair;
import datastructures.interfaces.IDictionary;
// import datastructures.interfaces.IList;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
// import misc.exceptions.InvalidElementException;
// import misc.exceptions.NotYetImplementedException;
import search.models.Webpage;

import java.net.URI;
import java.security.InvalidParameterException;

/**
 * This class is responsible for computing the 'page rank' of all available webpages.
 * If a webpage has many different links to it, it should have a higher page rank.
 * See the spec for more details.
 */
public class PageRankAnalyzer {
    private IDictionary<URI, Double> pageRanks;
    //private ISet<URI> allURIs;

    /**
     * Computes a graph representing the internet and computes the page rank of all
     * available webpages.
     *
     * @param webpages  A set of all webpages we have parsed. Must be non-null and must not contain
     *                  nulls.
     * @param decay     Represents the "decay" factor when computing page rank (see spec). Must be a
     *                  number between 0 and 1, inclusive.
     * @param epsilon   When the difference in page ranks is less than or equal to this number,
     *                  stop iterating. Must be a non-negative number.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges. Must be a non-negative number. (A limit of 0 should
     *                  simply return the initial page rank values from 'computePageRank'.)
     */
    public PageRankAnalyzer(ISet<Webpage> webpages, double decay, double epsilon, int limit) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        //

        // Step 1: Make a graph representing the 'internet'
        // IDictionary<URI, ISet<URI>> graph = this.makeGraph(webpages);

        // Step 2: Use this graph to compute the page rank for each webpage
        // this.pageRanks = this.makePageRanks(graph, decay, limit, epsilon);

        // Note: we don't store the graph as a field: once we've computed the
        // page ranks, we no longer need it!

        IDictionary<URI, ISet<URI>> graph = makeGraph(webpages);
        this.pageRanks = makePageRanks(graph, decay, limit, epsilon);
    }

    /**
     * This method converts a set of webpages into an unweighted, directed graph,
     * in adjacency list form.
     *
     * You may assume that each webpage can be uniquely identified by its URI.
     *
     * Note that a webpage may contain links to other webpages that are *not*
     * included within set of webpages you were given. You should omit these
     * links from your graph: we want the final graph we build to be
     * entirely "self-contained".
     */
    private IDictionary<URI, ISet<URI>> makeGraph(ISet<Webpage> webpages) {
        IDictionary<URI, ISet<URI>> graph = new ChainedHashDictionary<URI, ISet<URI>>();

        for (Webpage page : webpages){
            if (!graph.containsKey(page.getUri())){
                graph.put(page.getUri(), new ChainedHashSet<URI>());
            }
        }

        for (Webpage webpage : webpages) {
            ISet<URI> linkedPage = new ChainedHashSet<>();
            URI thisPage = webpage.getUri();
            IList<URI> links = webpage.getLinks();

            for (URI link : links) {
                if (graph.containsKey(link) && thisPage != link) {
                    linkedPage.add(link);

                }
                graph.put(thisPage, linkedPage);
            }
        }
        return graph;
    }

    /**
     * Computes the page ranks for all webpages in the graph.
     *
     * Precondition: assumes 'this.graphs' has previously been initialized.
     *
     * @param decay     Represents the "decay" factor when computing page rank (see spec).
     * @param epsilon   When the difference in page ranks is less than or equal to this number,
     *                  stop iterating.
     * @param limit     The maximum number of iterations we spend computing page rank. This value
     *                  is meant as a safety valve to prevent us from infinite looping in case our
     *                  page rank never converges.
     */
    private IDictionary<URI, Double> makePageRanks(IDictionary<URI, ISet<URI>> graph,
                                                   double decay,
                                                   int limit,
                                                   double epsilon) {
        IDictionary<URI, Double> oldRanks = new ChainedHashDictionary<URI, Double>();
        // Step 1: The initialize step should go here
        double init = (double) 1 / graph.size();
        for (KVPair<URI, ISet<URI>> page : graph) {
            oldRanks.put(page.getKey(), init);
        }

        for (int i = 0; i < limit; i++) {
            // Step 2: The update step should go here
            IDictionary<URI, Double> updateRanks = new ChainedHashDictionary<>();
            for (KVPair<URI, Double> pair: oldRanks) {
                updateRanks.put(pair.getKey(), 0.0);
            }

            // update
            for (KVPair<URI, ISet<URI>> page : graph){
                double oldRank = oldRanks.get(page.getKey());
                ISet<URI> linked = page.getValue();
                int linkedSize = page.getValue().size();

                // increase page rank of all webpage if no linked pages
                if (linkedSize != 0){
                    for (URI link : linked) {
                        double updateValue = (double) decay * oldRank / linkedSize;
                        double newRank = 0.0;
                        if (updateRanks.containsKey(link)) {
                            newRank = updateRanks.get(link);
                        }
                        updateRanks.put(link, newRank + updateValue);
                    }
                } else {
                    double updateValue = (double) decay * oldRank / graph.size();
                    for (KVPair<URI, Double> pair : updateRanks) {
                        double newRank = updateRanks.get(pair.getKey());
                        updateRanks.put(pair.getKey(), updateRanks.getOrDefault(pair.getKey(), 0.0) + updateValue);
                    }
                }
            }

            // Step 3: the convergence step should go here.
            // Return early if we've converged.
            boolean converge = true;
            for (KVPair<URI, Double> rank : updateRanks){
                double newRank = (double) rank.getValue() + ((1 - decay) / graph.size());
                updateRanks.put(rank.getKey(), newRank);
                double oldRank = oldRanks.get(rank.getKey());

                if (converge && Math.abs(oldRank - rank.getValue()) >= epsilon) {
                    oldRanks = updateRanks;
                    converge = false;
                }
            }
            if (converge) {
                return oldRanks;
            }
        }
        return oldRanks;
    }

    /**
     * Returns the page rank of the given URI.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public double computePageRank(URI pageUri) {
        if (!pageRanks.containsKey(pageUri)) {
            throw new InvalidParameterException();
        }
        return pageRanks.get(pageUri);
    }
}
