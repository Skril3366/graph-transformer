package graphTransformer.graph

import scala.annotation.tailrec

case class UnderectedGraph[N, E](
    nodes: Set[Node[N]],
    edges: Set[UnderectedEdge[N, E]]
) extends Graph[N, E, UnderectedEdge, UnderectedGraph] {

  override def make[N2, E2](
      nodes: Set[Node[N2]],
      edges: Set[UnderectedEdge[N2, E2]]
  ): UnderectedGraph[N2, E2] = UnderectedGraph(nodes, edges)

  def makeEdge[N2, E2](
      from: Node[N2],
      to: Node[N2],
      value: E2
  ): EdgeType[N2, E2] = UnderectedEdge(from, to, value)

  override def edgesCommonNode(
      edge1: UnderectedEdge[N, E],
      edge2: UnderectedEdge[N, E]
  ): Option[ThisNodeType] =
    edge1.nodes.toList.intersect(edge2.nodes.toList).headOption

  override lazy val adjacencyMatrix
      : Map[Node[N], Map[Node[N], Set[UnderectedEdge[N, E]]]] =
    edges
      .flatMap(e => List((e.nodeOne, e.nodeTwo, e), (e.nodeTwo, e.nodeOne, e)))
      .groupBy(_._1)
      .map((k, v) =>
        (
          k,
          v.groupBy(_._2).map((k, v) => (k, v.map(_._3))).withDefault(Map.empty)
        )
      )
      .withDefault(Map.empty)
}
