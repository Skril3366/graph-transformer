package graphTransformer.transformation

import zio.UIO
import zio.Task
import zio.ZIO

/// Special case of transfomation for merging multiple inputs into one output.
/// Allow to compose multiple transformations branches into a single one.
trait TransformationPoly[F[_], I, O] extends Transformation[F, I, O] {
  def apply(inputs: I*): F[O]
}

trait TransformationPolyPure[I, O] extends TransformationPoly[UIO, I, O] {
  def apply(inputs: I*): UIO[O] = ZIO.succeed(pureApply(inputs))

  def pureApply(inputs: Seq[I]): O
}

trait TransformationPolyIO[I, O] extends TransformationPoly[Task, I, O] {
  def apply(inputs: I*): Task[O]
}
