package graphTransformer.transformation

trait Transformation[F[_], I, O] {
  def name: String
}
