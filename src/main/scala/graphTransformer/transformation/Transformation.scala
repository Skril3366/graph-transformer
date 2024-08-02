package graphTransformer.transformation

import zio.Task
import zio.UIO

import graphTransformer.graph.Graph
import graphTransformer.graph.DirectedGraph
import graphTransformer.graph.Node
import graphTransformer.graph.DirectedEdge

trait TData[T] {
  // Unique identifier for the data type
  def TypeName: String
}

trait Transformation[F[_], I <: TData[I], O <: TData[O]] {
  def name: String
}

type TransformationGraph = DirectedGraph[Transformation[?, ?, ?], Unit]

def transitionGraph[T <: Transformation[?, ?, ?]](
    transformations: Set[T]
): TransformationGraph = {

  val nodes = transformations.map(Node(_))
  val edges = for {
    n1 <- nodes
    n2 <- nodes
    if n1 != n2
  } yield DirectedEdge(n1, n2, ())

  // DirectedGraph(nodes, edges)
  ???
}
