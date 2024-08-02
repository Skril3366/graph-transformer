package graphTransformer.transformation

import zio.Task
import zio.UIO
import zio.ZIO

/// Common type of transformations that take one input and produce one output.
/// They could be easily composed into a pipeline.
abstract class TransformationLinear[F[_], I, O](input_example: I, output_example: O)
    extends Transformation[F, I, O](input_example, output_example) {
  def apply(input: I): F[O]
}

abstract class TransformationLinearPure[I, O](input_example: I, output_example: O)
    extends TransformationLinear[UIO, I, O](input_example, output_example) {
  def apply(input: I): UIO[O] = ZIO.succeed(pureApply(input))

  def pureApply(input: I): O
}

abstract class TransformationLinearIO[I, O](input_example: I, output_example: O)
    extends TransformationLinear[Task, I, O](input_example: I, output_example: O) {
  def apply(input: I): Task[O]
}
