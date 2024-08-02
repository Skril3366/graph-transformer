package graphTransformer.transformation

import graphTransformer.graph.DirectedEdge
import graphTransformer.graph.DirectedGraph
import graphTransformer.graph.Node

abstract case class Transformation[F[_], +I, +O](
    input_example: I,
    output_example: O
) {
  def name: String

  override def toString: String = "Transformation_" + name
}

type TransformationGraph[F[_], I, O] =
  DirectedGraph[Transformation[F, I, O], Unit]

def transitionGraph[F[_], I, O](
    transformations: Set[Transformation[F, I, O]]
): TransformationGraph[F, I, O] = {

  val nodes = transformations.map(Node(_))
  val edges = for {
    n1 <- nodes
    n2 <- nodes
    c1 = n1.value.output_example.getClass()
    c2 = n2.value.input_example.getClass()
    if c2.isInstanceOf[c1.type]
  } yield DirectedEdge(n1, n2, ())
  DirectedGraph(nodes, edges)
}
