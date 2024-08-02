package graphTransformer.transformation

import zio.UIO
import zio.Task
import zio.ZIO

/// Common type of transformations that take one input and produce one output.
/// They could be easily composed into a pipeline.
trait TransformationLinear[F[_], I <: TData[I], O <: TData[O]] extends Transformation[F, I, O] {
  def apply(input: I): F[O]
}

trait TransformationLinearPure[I <: TData[I], O <: TData[O]] extends TransformationLinear[UIO, I, O] {
  def apply(input: I): UIO[O] = ZIO.succeed(pureApply(input))

  def pureApply(input: I): O
}

trait TransformationLinearIO[I <: TData[I], O <: TData[O]] extends TransformationLinear[Task, I, O] {
  def apply(input: I): Task[O]
}
