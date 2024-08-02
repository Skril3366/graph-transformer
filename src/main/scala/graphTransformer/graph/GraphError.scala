package graphTransformer.graph

sealed trait GraphError(msg: String)

case class NoSuchNodesInGraph[N](nodes: Seq[Node[N]]) extends GraphError(f"No such node(s) in graph: $nodes")
