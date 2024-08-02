package graphTransformer.graph

import cats.syntax.option._

/**
 * This is an implementation of directed graph which may contain multi-edges and self-loops. The graph is immutable and
 * all operations return a new graph.
 */
case class DirectedGraph[N, E](
    nodes: Set[Node[N]],
    edges: Set[DirectedEdge[N, E]]
) extends Graph[N, E, DirectedEdge, DirectedGraph] {

  override def make[N2, E2](
      nodes: Set[Node[N2]],
      edges: Set[DirectedEdge[N2, E2]]
  ) =
    DirectedGraph(nodes, edges)

  def makeEdge[N2, E2](
      from: Node[N2],
      to: Node[N2],
      value: E2
  ): DirectedEdge[N2, E2] = DirectedEdge(from, to, value)

  override def edgesCommonNode(
      edge1: DirectedEdge[N, E],
      edge2: DirectedEdge[N, E]
  ): Option[Node[N]] =
    if edge1.to == edge2.from then edge1.to.some else None

  override lazy val adjacencyMatrix: AdjacencyMatrix[N, E, DirectedEdge] =
    new AdjacencyMatrix(
      edges
        .map(e => (e.from, e.to, e))
        .groupBy(_._1)
        .map((k, v) =>
          (
            k,
            v.groupBy(_._2).map((k, v) => (k, v.map(_._3)))
          )
        )
    )
}
