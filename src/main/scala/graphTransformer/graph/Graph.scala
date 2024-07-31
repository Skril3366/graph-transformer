package graphTransformer.graph

import scala.annotation.tailrec

case class Node[N](value: N) {
  def map[T](f: N => T) = this.copy(f(value))
}

sealed trait Edge[N, E, EE[_, _] <: Edge[?, ?, ?]] {
  val value: E
  def nodes: (Node[N], Node[N])
  def mapNodes[T](f: N => T): EE[T, E]
  def map[T](f: E => T): EE[N, T]
}

case class UnderectedEdge[N, E](
    nodeOne: Node[N],
    nodeTwo: Node[N],
    value: E
) extends Edge[N, E, UnderectedEdge] {
  def nodes = (nodeOne, nodeTwo)
  def mapNodes[T](f: N => T): UnderectedEdge[T, E] =
    this.copy(nodeOne.map(f), nodeTwo.map(f), value)
  def map[T](f: E => T): UnderectedEdge[N, T] = this.copy(value = f(value))
}

case class DirectedEdge[N, E](
    from: Node[N],
    to: Node[N],
    value: E
) extends Edge[N, E, DirectedEdge] {
  def nodes = (from, to)
  def mapNodes[T](f: N => T) = this.copy(from.map(f), to.map(f), value)
  def map[T](f: E => T) = this.copy(value = f(value))
}

trait Graph[N, E, EE[N, E] <: Edge[N, E, EE], GG[N, E] <: Graph[N, E, EE, GG]] {
  type EdgeType[N, E] = EE[N, E]
  type GraphType[N, E] = GG[N, E]

  type ThisNodeType = Node[N]
  type ThisEdgeType = EdgeType[N, E]
  type ThisGraphType = GraphType[N, E]

  val nodes: Set[ThisNodeType]
  val edges: Set[ThisEdgeType]

  def make[N2, E2](
      nodes: Set[Node[N2]],
      edges: Set[EdgeType[N2, E2]]
  ): GraphType[N2, E2]

  def makeEdge[N2, E2](
      from: Node[N2],
      to: Node[N2],
      value: E2
  ): EdgeType[N2, E2]

  /** Returns common node between two edges if exists, used in other functions
    */
  def edgesCommonNode(
      edge1: ThisEdgeType,
      edge2: ThisEdgeType
  ): Option[ThisNodeType]

  lazy val adjacencyMatrix: Map[ThisNodeType, Map[ThisNodeType, Set[
    ThisEdgeType
  ]]]

  /** A graph where edges become nodes and two nodes are connected if they had a
    * node in between
    *
    * refer to https://en.wikipedia.org/wiki/Line_graph for more info
    */
  lazy val lineGraph: UnderectedGraph[E, N] = {
    val newNodes = edges.map(e => Node(e.value))
    val newEdges = for {
      edge1 <- edges
      edge2 <- edges
      intersectionNode <- edgesCommonNode(edge1, edge2).toSet
    } yield UnderectedEdge(
      Node(edge1.value),
      Node(edge2.value),
      intersectionNode.value
    )
    UnderectedGraph(newNodes, newEdges)
  }

  def addNodes(ns: ThisNodeType*): ThisGraphType = make(nodes ++ ns, edges)

  /** Adds edges to the graph if all the nodes in the edges are present,
    * otherwise returns an error with the list of nodes that are not present
    */
  def addEdges(
      es: ThisEdgeType*
  ): Either[NoSuchNodesInGraph[N], ThisGraphType] =
    es.flatMap(e => e.nodes.toList).filter(nodes.contains) match {
      case l if l.length == es.length * 2 =>
        Right(make(nodes, edges ++ es))
      case l => Left(NoSuchNodesInGraph(l))
    }

  /** Removes nodes from the graph and all the edges that are connected to them
    */
  def removeNodes(ns: ThisNodeType*): ThisGraphType = {
    val edgesToRemove =
      edges.filter(e => e.nodes.toList.intersect(ns).length == 0).toSet
    make(nodes -- ns.toSet, edges -- edgesToRemove)
  }

  def removeEdges(es: ThisEdgeType*): ThisGraphType =
    make(nodes, edges -- es.toSet)

  /*
   * Maps data inside the nodes
   */
  def mapNodes[N2](f: N => N2): GraphType[N2, E] =
    make(nodes.map(_.map(f)), edges.map(_.mapNodes(f)))

  /*
   * Maps data inside the edges
   */
  def mapEdges[E2](f: E => E2): GraphType[N, E2] =
    make(nodes, edges.map(_.map(f)))

  /** Given a function that creates a subgraph out of a graph's node, creates
    * replaces all nodes with those subgraphs.
    *
    * Nodes from 2 subgraphs are connected if and only if there was an edge
    * between the original corresponding nodes
    *
    * NOTE: there's no similar function for flatMapping edges as it requires
    * also function E => E1 together with E => DirectedGraph[N, E2]
    */
  def flatMapNodes[N2](
      f: N => GraphType[N2, E]
  ): GraphType[N2, E] = {
    val newGraphs = nodes.map(n => (n, f(n.value))).toMap

    val newNodes = newGraphs.values.toSet.flatMap(_.nodes)
    val newEdges = newGraphs.values.toSet.flatMap(_.edges)

    val additionalEdges = for {
      edge <- edges
      subgraphNodes = (n: ThisNodeType) =>
        newGraphs.get(n).toSet.flatMap(_.nodes)
      (fromNode, toNode) = edge.nodes
      from <- subgraphNodes(fromNode)
      to <- subgraphNodes(toNode)
    } yield makeEdge(from, to, edge.value)

    make(newNodes, newEdges ++ additionalEdges)
  }

  /* Collects all the edges reachable from the given node by walking the graph
   * using BFS algorithm
   */
  def reachableEdges(node: ThisNodeType): Set[ThisEdgeType] = {
    @tailrec
    def visit(
        toVisit: List[ThisNodeType],
        visited: Set[ThisNodeType],
        collected: Set[ThisEdgeType]
    ): Set[ThisEdgeType] = toVisit match {
      case Nil => collected
      case head :: tail => {
        val newNodes = adjacencyMatrix(head).keySet.diff(visited)
        val newEdges = adjacencyMatrix(head).values.flatten
        visit(tail ++ newNodes, visited + head, collected ++ newEdges)
      }
    }
    visit(List(node), Set.empty, Set.empty)
  }

  /** Collects all reachable nodes from the given node using BFS algorithm
    */
  def reachableNodes(node: ThisNodeType): Set[ThisNodeType] =
    reachableEdges(node).flatMap(n => n.nodes.toList)
}
