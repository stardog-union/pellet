package jjtraveler.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import jjtraveler.Visitable;

public class EdgesGraph implements Graph
{
	Collection edges = new Vector();

	public void addEdge(Visitable from, Visitable to)
	{
		Edge edge = new Edge(from, to);
		edges.add(edge);
	}

	public String toDot(String name)
	{
		String result = "digraph " + name + " {\n";
		Iterator edgeIterator = edges.iterator();
		while (edgeIterator.hasNext())
		{
			Edge edge = (Edge) edgeIterator.next();
			result += edge.toDot();
		}
		result += "}\n";
		return result;
	}

	public String toRSF()
	{
		return toRSF("edge");
	}

	public String toRSF(String defaultEdgeType)
	{
		String result = "";
		Iterator edgeIterator = edges.iterator();
		while (edgeIterator.hasNext())
		{
			Edge edge = (Edge) edgeIterator.next();
			result += edge.toRSF(defaultEdgeType);
		}
		return result;
	}

	public boolean equals(Object o)
	{
		boolean result = false;
		if (o instanceof EdgesGraph)
		{
			EdgesGraph g = (EdgesGraph) o;
			result = g.edges.equals(edges);
		}
		return result;
	}

	public int hashCode()
	{
		int result = super.hashCode();
		Iterator edgeIterator = edges.iterator();
		while (edgeIterator.hasNext())
		{
			Edge edge = (Edge) edgeIterator.next();
			result += edge.hashCode();
		}
		return result;
	}

	class Edge implements Comparable
	{
		Visitable source;

		Visitable target;

		public Edge(Visitable source, Visitable target)
		{
			this.source = source;
			this.target = target;
		}

		public String toDot()
		{
			return source + " -> " + target + ";\n";
		}

		public String toRSF(String edgeType)
		{
			return edgeType + " " + source + " " + target + "\n";
		}

		public boolean equals(Object o)
		{
			boolean result = false;
			if (o instanceof Edge)
			{
				Edge edge = (Edge) o;
				result = edge.source.equals(source) && edge.target.equals(target);
			}
			return result;
		}

		public int hashCode()
		{
			return source.hashCode() + target.hashCode();
		}

		public int compareTo(Object o)
		{
			if (o instanceof Edge)
			{
				Edge edge = (Edge) o;
				return this.hashCode() - edge.hashCode();
			}
			throw new ClassCastException("Tried to compare an Edge to something that is not an edge");
		}
	}
}
