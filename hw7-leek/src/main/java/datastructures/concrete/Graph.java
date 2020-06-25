package datastructures.concrete;

import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IEdge;
import datastructures.interfaces.IList;
import datastructures.interfaces.ISet;
import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.NoPathExistsException;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import misc.Sorter;


/**
 *
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 * Note: This class is not meant to be a full-featured way of representing a graph.
 *
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends IEdge<V> & Comparable<E>> {
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated than usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've constrained Graph
    //   so that E *must* always be an instance of IEdge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the IEdge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.

    private IDictionary<V, IList<E>> mapDic;
    private IList<E> edgesList;
    private IList<V> verticesList;


    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * Note that each edge in 'edges' represents a unique edge. For example, if 'edges'
     * contains an entry for '(A,B)' and for '(B,A)', that means there are two parallel
     * edges between vertex 'A' and vertex 'B'.
     *
     * @throws IllegalArgumentException if any edges have a negative weight
     * @throws IllegalArgumentException if any edges connect to a vertex not present in 'vertices'
     * @throws IllegalArgumentException if 'vertices' or 'edges' are null or contain null
     * @throws IllegalArgumentException if 'vertices' contains duplicates
     */
    public Graph(IList<V> vertices, IList<E> edges) {
        this.mapDic = new ChainedHashDictionary<>();

        this.edgesList = edges;
        this.verticesList = vertices;

        if (verticesList.contains(null) || edgesList.contains(null)) {
            throw new IllegalArgumentException("'vertices' or 'edges' are null or contain null");
        }

        for (V vertex: verticesList) {
            IList<E> e = new DoubleLinkedList<>();
            if (mapDic.containsKey(vertex)) {
                throw new IllegalArgumentException("duplicate vertices");
            } else {
                mapDic.put(vertex, e);
            }
        }

        for (E edge: edgesList){
            V vertOne = edge.getVertex1();
            V vertTwo = edge.getVertex2();
            double weight = edge.getWeight();

            if (weight < 0 || !verticesList.contains(vertOne) || !verticesList.contains(vertTwo)) {
              throw new IllegalArgumentException();
            }

            mapDic.get(vertOne).add(edge);
            mapDic.get(vertTwo).add(edge);
        }
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        if (set == null) {
            throw new IllegalArgumentException();
        }
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return verticesList.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        return edgesList.size();
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        ISet<E> mstSet = new ChainedHashSet<E>();
        ArrayDisjointSet<V> verticesSets = new ArrayDisjointSet<V>();

        for (V v: this.verticesList) {
            verticesSets.makeSet(v);
        }

        IList<E> sortedEdges = Sorter.topKSort(this.numEdges(), this.edgesList);

        for (E edge : sortedEdges) {
            V vertex1 = edge.getVertex1();
            V vertex2 = edge.getVertex2();
            int set1 = verticesSets.findSet(vertex1);
            int set2 = verticesSets.findSet(vertex2);
            if (set1 != set2) {
                mstSet.add(edge);
                verticesSets.union(edge.getVertex1(), edge.getVertex2());
            }
        }
        return mstSet;
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     * @throws IllegalArgumentException if start or end is null or not in the graph
     */
    public IList<E> findShortestPathBetween(V start, V end) {
        if (start.equals(end)) {
            return new DoubleLinkedList<E>();
        }
        if (start == null || end == null || !verticesList.contains(start) || !verticesList.contains(end)) {
            throw new IllegalArgumentException();
        }

        IDictionary<V, Vertex> pathMap = new ChainedHashDictionary<>();
        for (V vertex : verticesList) {
            pathMap.put(vertex, new Vertex(vertex, Double.POSITIVE_INFINITY));
        }

        ISet<V> processed = new ChainedHashSet<V>();

        IPriorityQueue<Vertex> priorityQ = new ArrayHeap<>();

        priorityQ.add(new Vertex(start, 0.0));
        pathMap.put(start, new Vertex(start, 0.0));

        while (!priorityQ.isEmpty() && priorityQ.peekMin().getVertex() != (end)){
            Vertex vertex = priorityQ.removeMin();

            V curr = vertex.getVertex();
            if (!processed.contains(curr)){
                processed.add(curr);
                for (E edgeOfV : mapDic.get(curr)){
                    V other = edgeOfV.getOtherVertex(curr);

                    IList<E> updateEdges = new DoubleLinkedList<>();
                    for (E edge: vertex.getPath()){
                        updateEdges.add(edge);
                    }
                    updateEdges.add(edgeOfV);

                    double oldDist = pathMap.get(other).getWeight();
                    double newDist = pathMap.get(curr).getWeight() + edgeOfV.getWeight();
                    if (newDist < oldDist) {
                        Vertex initialize = new Vertex(other, newDist, updateEdges);
                        if (priorityQ.contains(pathMap.get(other))){
                            priorityQ.replace(pathMap.get(other), initialize);
                        } else {
                            priorityQ.add(initialize);
                        }
                        pathMap.put(other, initialize);
                    }
                }
            }
        }

        if (pathMap.get(end).getWeight() != Double.POSITIVE_INFINITY) {
            return pathMap.get(end).getPath();
        }

        throw new NoPathExistsException();
    }



    private final class Vertex implements Comparable<Vertex> {
        private Double weight; // weight from start
        private V vertex;
        private IList<E> path;


        private Vertex(V vertex, double weight) {
            this.vertex = vertex;
            this.weight = weight;
            this.path = new DoubleLinkedList<E>();
        }

        private Vertex(V vertex, double weight, IList path) {
            this.vertex = vertex;
            this.weight = weight;
            this.path = path;
        }

        private V getVertex() {
            return this.vertex;
        }

        private double getWeight() {
            return this.weight;
        }

        @Override
        public int compareTo(Vertex other) {
            return (this.weight.compareTo(other.weight));
        }

        private IList<E> getPath(){
            return this.path;
        }
    }
}
