package graphTransformer.transformation

import zio.Task
import zio.UIO

import graphTransformer.graph.Graph
import graphTransformer.graph.DirectedGraph
import graphTransformer.graph.Node
import graphTransformer.graph.DirectedEdge

abstract case class Transformation[F[_], I, O](input_example: I, output_example: O) {
  def name: String

  override def toString: String = "Transformation_" + name
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
    // if n1.value.output_example.getClass().isInstance(n2.value.input_example.getClass())
    if n1.value.output_example == n2.value.input_example
  } yield DirectedEdge(n1, n2, ())

  println(edges)

  // DirectedGraph(nodes, edges)
  ???
}
