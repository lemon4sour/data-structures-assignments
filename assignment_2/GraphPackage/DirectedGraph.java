package GraphPackage;
import java.util.Iterator;
import ADTPackage.*; // Classes that implement various ADTs
/**
 A class that implements the ADT directed graph.
 @author Frank M. Carrano
 @author Timothy M. Henry
 @version 5.1
 */
public class DirectedGraph<T> implements GraphInterface<T>
{
	private DictionaryInterface<T, VertexInterface<T>> vertices;
	private int edgeCount;

	public DirectedGraph()
	{
		vertices = new UnsortedLinkedDictionary<>();
		edgeCount = 0;
	} // end default constructor

	public boolean addVertex(T vertexLabel)
	{
		VertexInterface<T> addOutcome = vertices.add(vertexLabel, new Vertex<>(vertexLabel));
		return addOutcome == null; // Was addition to dictionary successful?
	} // end addVertex

	public boolean addEdge(T begin, T end, double edgeWeight)
	{
		boolean result = false;
		VertexInterface<T> beginVertex = vertices.getValue(begin);
		VertexInterface<T> endVertex = vertices.getValue(end);
		if ( (beginVertex != null) && (endVertex != null) )
			result = beginVertex.connect(endVertex, edgeWeight);
		if (result)
			edgeCount++;
		return result;
	} // end addEdge

	public boolean addEdge(T begin, T end)
	{
		return addEdge(begin, end, 0);
	} // end addEdge

	public boolean hasEdge(T begin, T end)
	{
		boolean found = false;
		VertexInterface<T> beginVertex = vertices.getValue(begin);
		VertexInterface<T> endVertex = vertices.getValue(end);
		if ( (beginVertex != null) && (endVertex != null) )
		{
			Iterator<VertexInterface<T>> neighbors = beginVertex.getNeighborIterator();
			while (!found && neighbors.hasNext())
			{
				VertexInterface<T> nextNeighbor = neighbors.next();
				if (endVertex.equals(nextNeighbor))
					found = true;
			} // end while
		} // end if

		return found;
	} // end hasEdge

	public boolean isEmpty()
	{
		return vertices.isEmpty();
	} // end isEmpty

	public void clear()
	{
		vertices.clear();
		edgeCount = 0;
	} // end clear

	public int getNumberOfVertices()
	{
		return vertices.getSize();
	} // end getNumberOfVertices

	public int getNumberOfEdges()
	{
		return edgeCount;
	} // end getNumberOfEdges

	protected void resetVertices()
	{
		Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();
		while (vertexIterator.hasNext())
		{
			VertexInterface<T> nextVertex = vertexIterator.next();
			nextVertex.unvisit();
			nextVertex.setCost(0);
			nextVertex.setPredecessor(null);
		} // end while
	} // end resetVertices

	public StackInterface<T> getTopologicalOrder()
	{
		resetVertices();

		StackInterface<T> vertexStack = new LinkedStack<>();
		int numberOfVertices = getNumberOfVertices();
		for (int counter = 1; counter <= numberOfVertices; counter++)
		{
			VertexInterface<T> nextVertex = findTerminal();
			nextVertex.visit();
			vertexStack.push(nextVertex.getLabel());
		} // end for

		return vertexStack;
	} // end getTopologicalOrder

	public QueueInterface<T> getBreadthFirstSearch(T origin, T end) {
	    int visitedCount = 0;
		QueueInterface<T> outputQueue = new LinkedQueue<>();

		QueueInterface<VertexInterface<T>> visitQueue = new LinkedQueue<>();
		VertexInterface<T> originVertex = vertices.getValue(origin);
		visitQueue.enqueue(originVertex); //origin

		while (!visitQueue.isEmpty()) {
			visitedCount++;
			VertexInterface<T> currentVertex = visitQueue.dequeue();
			currentVertex.visit();
			outputQueue.enqueue(currentVertex.getLabel());

			if (currentVertex.getLabel().equals(end)) {
				resetVertices();
				System.out.println("BFS : Visited "+visitedCount+" vertices.");
				return outputQueue; //end
			}

			Iterator<VertexInterface<T>> neighborIterator = currentVertex.getNeighborIterator();
			while (neighborIterator.hasNext()) {
				VertexInterface<T> neighborVertex = neighborIterator.next();
				if (!neighborVertex.isVisited()) visitQueue.enqueue(neighborVertex);
			}
		}

		resetVertices();
		return null;
	}

	public QueueInterface<T> getDepthFirstSearch(T origin, T end) {
		int visitedCount = 0;
		QueueInterface<T> outputQueue = new LinkedQueue<>();

		StackInterface<VertexInterface<T>> visitStack = new LinkedStack<>();
		VertexInterface<T> originVertex = vertices.getValue(origin);
		visitStack.push(originVertex); //origin

		while (!visitStack.isEmpty()) {
			visitedCount++;
			VertexInterface<T> currentVertex = visitStack.pop();
			currentVertex.visit();
			outputQueue.enqueue(currentVertex.getLabel());

			if (currentVertex.getLabel().equals(end)) {
				resetVertices();
				System.out.println("DFS : Visited "+visitedCount+" vertices.");
				return outputQueue; //end
			}

			Iterator<VertexInterface<T>> neighborIterator = currentVertex.getNeighborIterator();
			while (neighborIterator.hasNext()) {
				VertexInterface<T> neighborVertex = neighborIterator.next();
				if (!neighborVertex.isVisited()) visitStack.push(neighborVertex);
			}
		}

		resetVertices();
		return null;
	}

	public int getShortestPath(T begin, T end, StackInterface<T> path) {

		QueueInterface<VertexInterface<T>> visitQueue = new LinkedQueue<>();
		VertexInterface<T> originVertex = vertices.getValue(begin);
		visitQueue.enqueue(originVertex); //origin

		boolean finish = false;
		while (!visitQueue.isEmpty() && !finish) {
			VertexInterface<T> currentVertex = visitQueue.dequeue();
			currentVertex.visit();

			Iterator<VertexInterface<T>> neighborIterator = currentVertex.getNeighborIterator();
			while (neighborIterator.hasNext() && !finish) {
				VertexInterface<T> neighborVertex = neighborIterator.next();
				if (!neighborVertex.isVisited()) {
					neighborVertex.setCost(currentVertex.getCost() + 1); //add one extra cost to neighboring vertex
					neighborVertex.setPredecessor(currentVertex);
					visitQueue.enqueue(neighborVertex);
				}

				if (neighborVertex.getLabel().equals(end)) {
					finish = true; //end
				}
			}
		}

		//construct path
		path.push(vertices.getValue(end).getLabel());

		VertexInterface<T> travelVertex = vertices.getValue(end);
		while (travelVertex.hasPredecessor()) {
			travelVertex = travelVertex.getPredecessor();
			path.push(travelVertex.getLabel());
		}


		int outputLength = (int) vertices.getValue(end).getCost();
		resetVertices();
		System.out.println("Shortest Path: Visited "+(outputLength+1)+" vertices.");

		return outputLength;
	}



	public double getCheapestPath(T begin, T end, StackInterface<T> path) {
		if (path == null) return -1;

		QueueInterface<EntryPQ> visitQueue = new LinkedQueue<>();
		VertexInterface<T> originVertex = vertices.getValue(begin);
		visitQueue.enqueue(new EntryPQ(originVertex,0, null)); //origin
		//EntryPQ is used because of the possibility of different paths with different costs to have the same destination.

		boolean finish = false;
		while (!visitQueue.isEmpty() && !finish) {
			EntryPQ currentEntry = visitQueue.dequeue();
			VertexInterface<T> currentVertex = currentEntry.getVertex();

			if (currentVertex.isVisited()) continue;

			currentVertex.visit();
			currentVertex.setCost(currentEntry.getCost());
			currentVertex.setPredecessor(currentEntry.getPredecessor());

			if (currentVertex.getLabel().equals(end)) {
				finish = true; //end
			}
			else {
				Iterator<VertexInterface<T>> neighborIterator = currentVertex.getNeighborIterator();
				Iterator<Double> weightIterator = currentVertex.getWeightIterator();
				while (neighborIterator.hasNext()) {
					VertexInterface<T> neighbor = neighborIterator.next();
					Double weight = weightIterator.next();

					if (!neighbor.isVisited())
						visitQueue.enqueue(new EntryPQ(neighbor, currentVertex.getCost() + weight, currentVertex)); //add current cost to neighbor cost
				}
			}
		}

		//construct path
		int visitedCount = 1;
		double cost = vertices.getValue(end).getCost();
		path.push(vertices.getValue(end).getLabel());

		VertexInterface<T> travelVertex = vertices.getValue(end);
		while (travelVertex.hasPredecessor()) {
			visitedCount++;
			travelVertex = travelVertex.getPredecessor();
			path.push(travelVertex.getLabel());
		}
		System.out.println("Cheapest Path: Visited "+visitedCount+" vertices.");
		resetVertices();

		return cost;
	}

	protected VertexInterface<T> findTerminal()
	{
		boolean found = false;
		VertexInterface<T> result = null;

		Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();

		while (!found && vertexIterator.hasNext())
		{
			VertexInterface<T> nextVertex = vertexIterator.next();

			// If nextVertex is unvisited AND has only visited neighbors)
			if (!nextVertex.isVisited())
			{
				if (nextVertex.getUnvisitedNeighbor() == null )
				{
					found = true;
					result = nextVertex;
				} // end if
			} // end if
		} // end while

		return result;
	} // end findTerminal

	// Used for testing
	public void displayEdges()
	{
		System.out.println("\nEdges exist from the first vertex in each line to the other vertices in the line.");
		System.out.println("(Edge weights are given; weights are zero for unweighted graphs):\n");
		Iterator<VertexInterface<T>> vertexIterator = vertices.getValueIterator();
		while (vertexIterator.hasNext())
		{
			((Vertex<T>)(vertexIterator.next())).display();
		} // end while
	} // end displayEdges 

	private class EntryPQ implements Comparable<EntryPQ>
	{
		private VertexInterface<T> vertex;
		private VertexInterface<T> previousVertex;
		private double cost; // cost to nextVertex

		private EntryPQ(VertexInterface<T> vertex, double cost, VertexInterface<T> previousVertex)
		{
			this.vertex = vertex;
			this.previousVertex = previousVertex;
			this.cost = cost;
		} // end constructor

		public VertexInterface<T> getVertex()
		{
			return vertex;
		} // end getVertex

		public VertexInterface<T> getPredecessor()
		{
			return previousVertex;
		} // end getPredecessor

		public double getCost()
		{
			return cost;
		} // end getCost

		public int compareTo(EntryPQ otherEntry)
		{
			// Using opposite of reality since our priority queue uses a maxHeap;
			// could revise using a minheap
			return (int)Math.signum(otherEntry.cost - cost);
		} // end compareTo

		public String toString()
		{
			return vertex.toString() + " " + cost;
		} // end toString 
	} // end EntryPQ
} // end DirectedGraph