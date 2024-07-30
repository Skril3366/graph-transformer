package graphTransformer.graph

case class UnderectedEdge[N, E](
  nodeOne: Node[N],
  nodeTwo: Node[N],
  value: E
)


case class UnderectedGraph[N, E](
  nodes: Set[Node[N]],
  edges: Set[UnderectedEdge[N, E]]
){
  // TODO: implement
}
