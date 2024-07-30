package graphTransformer.graph

import scala.annotation.tailrec

// TODO: move to a Graph file if I plan to make trait Graph
case class Node[N](value: N){
  def map[T](f: N => T) = this.copy(f(value))
}

case class DirectedEdge[N, E](from: Node[N], to: Node[N], value: E) {
    def mapNodes[T](f: N => T) = this.copy( from.map(f), to.map(f), value)
    def map[T](f: E => T) = this.copy(value = f(value))
  }

// TODO: think if I want to factor out common logic for directed and underected
// into a common trait
case class DirectedGraph[N, E](nodes: Set[Node[N]], edges: Set[DirectedEdge[N, E]]){

  type NodeType = Node[N]
  type EdgeType = DirectedEdge[N, E]
  type GraphType = DirectedGraph[N, E]

  lazy val adjacencyMatrix: Map[NodeType, Map[NodeType, Set[EdgeType]]] =
    edges
      .map(e => (e.from, e.to, e))
      .groupBy(_._1)
      .map((k,v) => (k, v.groupBy(_._2).map((k, v) => (k, v.map(_._3))).withDefault(Map.empty)))
      .withDefault(Map.empty)

  // Edges become nodes and we connect them if they had a node in between
  // https://en.wikipedia.org/wiki/Line_graph
  lazy val lineGraph: UnderectedGraph[E, N] = {
    val newNodes = edges.map(e => Node(e.value))
    val newEdges = for {
      edge1 <- edges
      edge2 <- edges
      if edge1.to == edge2.from
    } yield UnderectedEdge(Node(edge1.value), Node(edge2.value), edge1.to.value)

    UnderectedGraph(newNodes, newEdges)
  }

  def addNodes(ns: NodeType*): GraphType = DirectedGraph(nodes ++ ns, edges)

  def addEdges(es: EdgeType*): Either[NoSuchNodesInGraph[N],GraphType] =
    es.flatMap(e => List(e.from, e.to)).filter(nodes.contains) match {
      case l if l.length == es.length * 2 => Right(DirectedGraph(nodes, edges ++ es))
      case l => Left(NoSuchNodesInGraph(l))
    }

  // TODO: make variadic
  def removeNodes(ns: NodeType*): GraphType = {
    val edgesToRemove = edges.filter(e => ns.contains(e.from) || ns.contains(e.to)).toSet
    DirectedGraph(nodes -- ns.toSet, edges -- edgesToRemove)
  }

  def removeEdges(es: EdgeType*): GraphType = DirectedGraph(nodes, edges -- es.toSet)

  def mapNodes[N2](f: N => N2): DirectedGraph[N2, E] = DirectedGraph(nodes.map(_.map(f)), edges.map(_.mapNodes(f)))

  def mapEdges[E2](f: E => E2): DirectedGraph[N, E2] = DirectedGraph(nodes, edges.map(_.map(f)))

  // Creates subgraphs for each graph and then connects all the nodes from 1
  // subgraph to another if there was an edge between them in the original graph
  // NOTE: there's no similar function for flatMapping edges as it requires also
  // function E => E1 together with E => DirectedGraph[N, E2]
  def flatMapNodes[N2](f: N => DirectedGraph[N2, E]): DirectedGraph[N2, E] = {
    val newGraphs = nodes.map(n => (n, f(n.value))).toMap

    val newNodes = newGraphs.values.toSet.flatMap(_.nodes)
    val newEdges = newGraphs.values.toSet.flatMap(_.edges)

    val additionalEdges = for {
          edge <- edges
          subgraphNodes = (n: NodeType) => newGraphs.get(n).toSet.flatMap(_.nodes)
          from <- subgraphNodes(edge.from)
          to <- subgraphNodes(edge.to)
        } yield DirectedEdge(from, to, edge.value)

    DirectedGraph(newNodes, newEdges ++ additionalEdges)
  }


  // Collects all reachable edges from the given node using BFS algorithm
  def reachableEdges(node: NodeType): Set[EdgeType] = {
    @tailrec
    def visit(toVisit: List[NodeType], visited: Set[NodeType], collected: Set[EdgeType]): Set[EdgeType] = toVisit match{
      case Nil => collected
      case head :: tail => {
        val newNodes = adjacencyMatrix(head).keySet.diff(visited)
        val newEdges = adjacencyMatrix(head).values.flatten
        visit(tail ++ newNodes, visited + head, collected ++ newEdges)
      }
    }

    visit(List(node), Set.empty, Set.empty)
  }

  // Collects all reachable nodes from the given node using BFS algorithm
  def reachableNodes(node: NodeType): Set[NodeType] = reachableEdges(node).flatMap(n => Set(n.from, n.to))
}
