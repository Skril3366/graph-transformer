package graphTransformer.graph

class AdjacencyMatrix[N, E, EE[_, _] <: Edge[?, ?, ?]](
    matrix: Map[Node[N], Map[Node[N], Set[EE[N, E]]]]
) {
  def getToNodes(from: Node[N]): Set[Node[N]] = matrix.getOrElse(from, Map.empty).keySet
  def getToEdges(from: Node[N]): Set[EE[N, E]] = matrix.getOrElse(from, Map.empty).values.flatten.toSet
  def apply(from: Node[N])(to: Node[N]) =
    matrix.getOrElse(from, Map.empty).getOrElse(to, Set.empty)
}
