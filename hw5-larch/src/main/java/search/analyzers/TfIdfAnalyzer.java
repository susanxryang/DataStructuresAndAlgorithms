package search.analyzers;

import datastructures.concrete.ChainedHashSet;
import datastructures.concrete.dictionaries.KVPair;
import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import search.models.Webpage;

import java.net.URI;

/**
 * This class is responsible for computing how "relevant" any given document is
 * to a given search query.
 *
 * See the spec for more details.
 */
public class TfIdfAnalyzer {
    // This field must contain the IDF score for every single word in all
    // the documents.
    private IDictionary<String, Double> idfScores;

    // This field must contain the TF-IDF vector for each webpage you were given
    // in the constructor.
    //
    // We will use each webpage's page URI as a unique key.
    private IDictionary<URI, IDictionary<String, Double>> documentTfIdfVectors;

    // Feel free to add extra fields and helper methods.
    //each document's norm vector
    private IDictionary<URI, Double> docNormVector;

    /**
     * @param webpages  A set of all webpages we have parsed. Must be non-null and
     *                  must not contain nulls.
     */
    public TfIdfAnalyzer(ISet<Webpage> webpages) {
        // Implementation note: We have commented these method calls out so your
        // search engine doesn't immediately crash when you try running it for the
        // first time.
        if (webpages == null) {
            throw new NullPointerException();
        }
        this.idfScores = this.computeIdfScores(webpages);
        this.documentTfIdfVectors = this.computeAllDocumentTfIdfVectors(webpages);
        this.docNormVector = new ChainedHashDictionary<>();
        this.precomputation(webpages);
    }

    private void precomputation(ISet<Webpage> webpages) {
        for (Webpage page: webpages) {
            URI uri = page.getUri();
            this.docNormVector.put(uri, norm(this.documentTfIdfVectors.get(uri)));
        }
    }

    // Note: this method, strictly speaking, doesn't need to exist. However,
    // we've included it so we can add some unit tests to help verify that your
    // constructor correctly initializes your fields.
    public IDictionary<URI, IDictionary<String, Double>> getDocumentTfIdfVectors() {
        return this.documentTfIdfVectors;
    }

    // Note: these private methods are suggestions or hints on how to structure your
    // code. However, since they're private, you're not obligated to implement exactly
    // these methods: feel free to change or modify these methods however you want. The
    // important thing is that your 'computeRelevance' method ultimately returns the
    // correct answer in an efficient manner.

    /**
     * Return a dictionary mapping every single unique word found
     * in every single document to their IDF score.
     */
    private IDictionary<String, Double> computeIdfScores(ISet<Webpage> pages) {
        IDictionary<String, Double> docScores = new ChainedHashDictionary<>();
        IDictionary<String, Double> idfScore = new ChainedHashDictionary<>();

        for (Webpage page : pages) {
            ISet<String> wordSet = new ChainedHashSet<>();
            IList<String> wordList = page.getWords();

            for (String word : wordList) {
                wordSet.add(word);
            }

            for (String name : wordSet) {
                double score =  docScores.getOrDefault(name, 0.0) + 1;
                docScores.put(name, score);
            }
        }

        for (KVPair<String, Double> term : docScores) {
            idfScore.put(term.getKey(), Math.log(pages.size() / term.getValue()));
        }

        return idfScore;
    }

    /**
     * Returns a dictionary mapping every unique word found in the given list
     * to their term frequency (TF) score.
     *
     * The input list represents the words contained within a single document.
     */
    private IDictionary<String, Double> computeTfScores(IList<String> words) {
        IDictionary<String, Double> wordCount = new ChainedHashDictionary<>();
        IDictionary<String, Double> tfScore = new ChainedHashDictionary<>();

        for (String word : words) {
            double scoreCount =  wordCount.getOrDefault(word, 0.0) + 1;
            wordCount.put(word, scoreCount);
        }

        double totalWords = (double) words.size();

        for (KVPair<String, Double> pair : wordCount) {
            tfScore.put(pair.getKey(), pair.getValue() / totalWords);
        }
        return tfScore;
    }

    /**
     * See spec for more details on what this method should do.
     */
    private IDictionary<URI, IDictionary<String, Double>> computeAllDocumentTfIdfVectors(ISet<Webpage> pages) {
        // Hint: this method should use the idfScores field and
        // call the computeTfScores(...) method.
        IDictionary<URI, IDictionary<String, Double>> docTFIdf = new ChainedHashDictionary<>();

        for (Webpage page : pages) {
            IDictionary<String, Double> scores = computeTfScores(page.getWords());
            IDictionary<String, Double> temp = new ChainedHashDictionary<>();
            for (KVPair<String, Double> pair : scores) {
                String word = pair.getKey();
                double tfScore = pair.getValue();
                double tfIdfScore = tfScore * this.idfScores.get(word);
                temp.put(word, tfIdfScore);
            }
            docTFIdf.put(page.getUri(), temp);
        }
        return docTFIdf;
    }

    /**
     * Returns the cosine similarity between the TF-IDF vector for the given query and the
     * URI's document.
     *
     * Precondition: the given uri must have been one of the uris within the list of
     *               webpages given to the constructor.
     */
    public Double computeRelevance(IList<String> query, URI pageUri) {
        // Note: The pseudocode we gave you is not very efficient. When implementing,
        // this method, you should:
        //
        // 1. Figure out what information can be precomputed in your constructor.
        //    Add a third field containing that information.
        //
        // 2. See if you can combine or merge one or more loops.

        IDictionary<String, Double> documentVector = documentTfIdfVectors.get(pageUri);

        IDictionary<String, Double> queryTfScores = this.computeTfScores(query);
        IDictionary<String, Double> queryVector = new ChainedHashDictionary<>(); // temp
        double numerator = 0.0;

        for (KVPair<String, Double> pair : queryTfScores) {
            String word = pair.getKey();
            double tfIdfScore = 0.0;
            if (idfScores.containsKey(word)) {
                tfIdfScore = pair.getValue() * idfScores.get(word);
            }
            queryVector.put(word, tfIdfScore);
            double queryWordScore = tfIdfScore;

            double docWordScore = 0.0;

            if (documentVector.containsKey(word)){
                docWordScore = documentVector.get(word);
            }
            numerator += queryWordScore * docWordScore;
        }

        double denominator = docNormVector.get(pageUri) * norm(queryVector);

        if (denominator != 0) {
            return numerator/denominator;
        } else {
            return 0.0;
        }
    }

    private double norm(IDictionary<String, Double> vector) {
        double output = 0.0;
        for (KVPair<String, Double> pair : vector) {
            double score = pair.getValue();
            output += score * score;
        }
        return Math.sqrt(output);
    }
}
