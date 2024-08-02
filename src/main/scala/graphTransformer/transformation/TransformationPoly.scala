package graphTransformer.transformation

import zio.Task
import zio.UIO
import zio.ZIO

/// Special case of transfomation for merging multiple inputs into one output.
/// Allow to compose multiple transformations branches into a single one.
abstract class TransformationPoly[F[_], I](element_example: I)
    extends Transformation[F, I, I](element_example, element_example) {
  def apply(inputs: I*): F[I]
}

abstract class TransformationPolyPure[I](element_example: I) extends TransformationPoly[UIO, I](element_example) {
  def apply(inputs: I*): UIO[I] = ZIO.succeed(pureApply(inputs))

  def pureApply(inputs: Seq[I]): I
}

abstract class TransformationPolyIO[I](element_example: I) extends TransformationPoly[Task, I](element_example: I) {
  def apply(inputs: I*): Task[I]
}
