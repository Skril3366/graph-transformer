package graphTransformer.graph

sealed trait GraphError(msg: String)

case class NoSuchNodesInGraph[N](nodes: Seq[Node[N]]) extends GraphError(f"No such node(s) in graph: $nodes")
case class NoSuchEdgesInGraph[N, E](edges: Seq[DirectedEdge[N, E]]) extends GraphError(f"No such edge(s) in graph: $edges")
