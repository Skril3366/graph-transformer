package graphTransformer.transformation



class UserTransformationGraph[F[_], I, O](transitionGraph: TransformationGraph) {
  /// constructs user graph if it is valid according to the transition graph
  def fromGraph(user_graph: TransformationGraph): Option[UserTransformationGraph[F, I, O]] = ???

  /// add transformation to the graph, if types mismatch return Empty
  def insertTransformation(
      t: Transformation[?, ?, ?],
      toNode: Transformation[?, ?, ?]
  ): Option[UserTransformationGraph[F, I, O]] =
    ???

  /// remove transformation from the graph, if types mismatch return Empty
  def removeTransformation(
      t: Transformation[?, ?, ?]
  ): Option[UserTransformationGraph[F, I, O]] = ???

  /// remove all transformations that are successors of the given transformation. Even Poly ones
  def cascade(
      t: Transformation[?, ?, ?]
  ): UserTransformationGraph[F, I, O] = ???

  // def isValid(ttg: TransformationTransitionGraph[F, I, O]): Boolean
}
