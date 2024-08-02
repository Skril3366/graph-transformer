package graphTransformer.transformation
import zio.UIO

class InitTransformation[T](value: T) extends TransformationLinearPure[Unit, T]((), value) {
  override def name: String        = "InitTransformation"
  override def pureApply(_n: Unit) = value
}

class AddOneTransgression extends TransformationLinearPure[Int, Int](0, 0) {
  override def name: String               = "AddOneTransgression"
  override def pureApply(input: Int): Int = input + 1
}

class ToListTransformation[T](element_example: T)
    extends TransformationLinearPure[T, List[T]](element_example, List(element_example)) {
  override def name: String        = "ToListTransformation"
  override def pureApply(input: T) = List(input)
}

class ConcatListsTransformation[T](element_example: T) extends TransformationPolyPure[List[T]](List(element_example)) {
  override def name: String                    = "ConcatListsTransformation"
  override def pureApply(inputs: Seq[List[T]]) = inputs.flatten.toList
}

// def testAboba[T](c: Type): Unit =

@main def temp(): Unit = {
  val tInit   = new InitTransformation(5)
  val tAddOne = new AddOneTransgression
  val tToList = new ToListTransformation[Int](5)
  val tConcat = new ConcatListsTransformation[Int](5)

  // println(ClassTag(tInit.getClass).runtimeClass)

  val tg = transitionGraph(
    Set(
      tInit,
      tAddOne,
      tToList,
      tConcat
    )
  )
  println("Hello, world!")
  println("Hello, world!")
}
