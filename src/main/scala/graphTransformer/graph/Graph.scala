package graphTransformer.graph

import scala.annotation.tailrec
import scala.reflect.ClassTag

case class Node[N](value: N) {
  def map[T](f: N => T) = this.copy(f(value))
}

sealed trait Edge[N, E, EE[_, _] <: Edge[?, ?, ?]] {
  val value: E
  def nodes: (Node[N], Node[N])
  def mapNodes[T](f: N => T): EE[T, E]
  def map[T](f: E => T): EE[N, T]
}

case class UndirectedEdge[N, E](
    nodeOne: Node[N],
    nodeTwo: Node[N],
    value: E
) extends Edge[N, E, UndirectedEdge] {
  def nodes = (nodeOne, nodeTwo)
  def mapNodes[T](f: N => T): UndirectedEdge[T, E] =
    this.copy(nodeOne.map(f), nodeTwo.map(f), value)
  def map[T](f: E => T): UndirectedEdge[N, T] = this.copy(value = f(value))

  override def equals(obj: Any): Boolean = obj match {
// NOTE: unchecked is used to suppress warning
    case that: UndirectedEdge[N, E] @unchecked =>
      this.value == that.value && this.nodes.toList
        .diff(that.nodes.toList)
        .isEmpty
    case _ => false
  }
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
  val nodes: Set[Node[N]]
  val edges: Set[EE[N, E]]

  def make[N2, E2](
      nodes: Set[Node[N2]],
      edges: Set[EE[N2, E2]]
  ): GG[N2, E2]

  def makeEdge[N2, E2](
      from: Node[N2],
      to: Node[N2],
      value: E2
  ): EE[N2, E2]

  /** Returns common node between two edges if exists, used in other functions
    */
  def edgesCommonNode(
      edge1: EE[N, E],
      edge2: EE[N, E]
  ): Option[Node[N]]

  lazy val adjacencyMatrix: AdjacencyMatrix[N, E, EE]

  /** A graph where edges become nodes and two nodes are connected if they had a
    * node in between
    *
    * refer to https://en.wikipedia.org/wiki/Line_graph for more info
    */
  lazy val lineGraph: UndirectedGraph[E, N] = {
    val newNodes = edges.map(e => Node(e.value))
    val newEdges = for {
      edge1 <- edges
      edge2 <- edges
      intersectionNode <- edgesCommonNode(edge1, edge2).toSet
    } yield UndirectedEdge(
      Node(edge1.value),
      Node(edge2.value),
      intersectionNode.value
    )
    UndirectedGraph(newNodes, newEdges)
  }

  def addNodes(ns: Node[N]*): GG[N, E] = make(nodes ++ ns, edges)

  /** Adds edges to the graph if all the nodes in the edges are present,
    * otherwise returns an error with the list of nodes that are not present
    */
  def addEdges(
      es: EE[N, E]*
  ): Either[NoSuchNodesInGraph[N], GG[N, E]] =
    es.flatMap(e => e.nodes.toList).filterNot(nodes.contains) match {
      case l if l.nonEmpty => Left(NoSuchNodesInGraph(l))
      case l               => Right(make(nodes, edges ++ es))
    }

  /** Removes nodes from the graph and all the edges that are connected to them
    */
  def removeNodes(ns: Node[N]*): GG[N, E] = {
    val edgesToRemove =
      edges.filter(e => e.nodes.toList.intersect(ns).isEmpty).toSet
    make(nodes -- ns.toSet, edges -- edgesToRemove)
  }

  def removeEdges(es: EE[N, E]*): GG[N, E] =
    make(nodes, edges -- es.toSet)

  /*
   * Maps data inside the nodes
   */
  def mapNodes[N2](f: N => N2): GG[N2, E] =
    make(nodes.map(_.map(f)), edges.map(_.mapNodes(f)))

  /*
   * Maps data inside the edges
   */
  def mapEdges[E2](f: E => E2): GG[N, E2] =
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
      f: N => GG[N2, E]
  ): GG[N2, E] = {
    val newGraphs = nodes.map(n => (n, f(n.value))).toMap

    val newNodes = newGraphs.values.toSet.flatMap(_.nodes)
    val newEdges = newGraphs.values.toSet.flatMap(_.edges)

    val additionalEdges = for {
      edge <- edges
      subgraphNodes = (n: Node[N]) => newGraphs.get(n).toSet.flatMap(_.nodes)
      (fromNode, toNode) = edge.nodes
      from <- subgraphNodes(fromNode)
      to <- subgraphNodes(toNode)
    } yield makeEdge(from, to, edge.value)

    make(newNodes, newEdges ++ additionalEdges)
  }

  /* Collects all the edges reachable from the given node by walking the graph
   * using BFS algorithm
   */
  def reachableEdges(node: Node[N]): Set[EE[N, E]] = {
    @tailrec
    def visit(
        toVisit: List[Node[N]],
        visited: Set[Node[N]],
        collected: Set[EE[N, E]]
    ): Set[EE[N, E]] = toVisit match {
      case Nil => collected
      case head :: tail => {
        val newNodes = adjacencyMatrix.getToNodes(head)
        val newEdges = adjacencyMatrix.getToEdges(head)
        visit(tail ++ newNodes, visited + head, collected ++ newEdges)
      }
    }
    visit(List(node), Set.empty, Set.empty)
  }

  /** Collects all reachable nodes from the given node using BFS algorithm
    *
    * NOTE: returns also returns the starting node
    */
  def reachableNodes(node: Node[N]): Set[Node[N]] =
    reachableEdges(node).flatMap(n => n.nodes.toList) + node
}
