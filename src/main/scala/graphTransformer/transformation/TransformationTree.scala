package graphTransformer.transformation

import zio.Task
import zio.UIO

import graphTransformer.graph.Graph
import graphTransformer.graph.DirectedGraph

trait TransformationTree[F[_]] extends DirectedGraph[Transformation[F, Any, Any], Unit] {
  // def validate() -> Boolean
}
