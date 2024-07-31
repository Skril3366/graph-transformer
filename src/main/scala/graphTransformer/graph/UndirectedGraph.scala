package graphTransformer.graph

import scala.annotation.tailrec

case class UndirectedGraph[N, E](
    nodes: Set[Node[N]],
    edges: Set[UndirectedEdge[N, E]]
) extends Graph[N, E, UndirectedEdge, UndirectedGraph] {

  override def make[N2, E2](
      nodes: Set[Node[N2]],
      edges: Set[UndirectedEdge[N2, E2]]
  ): UndirectedGraph[N2, E2] = UndirectedGraph(nodes, edges)

  def makeEdge[N2, E2](
      from: Node[N2],
      to: Node[N2],
      value: E2
  ): UndirectedEdge[N2, E2] = UndirectedEdge(from, to, value)

  override def edgesCommonNode(
      edge1: UndirectedEdge[N, E],
      edge2: UndirectedEdge[N, E]
  ): Option[Node[N]] = {
    val intersection = edge1.nodes.toList.intersect(edge2.nodes.toList)

    if intersection.length == 1 then Some(intersection.head) else None
  }

  override lazy val adjacencyMatrix: AdjacencyMatrix[N, E, UndirectedEdge] =
    new AdjacencyMatrix(
      edges
        .flatMap(e =>
          List((e.nodeOne, e.nodeTwo, e), (e.nodeTwo, e.nodeOne, e))
        )
        .groupBy(_._1)
        .map((k, v) =>
          (
            k,
            v.groupBy(_._2).map((k, v) => (k, v.map(_._3)))
          )
        )
    )
}
