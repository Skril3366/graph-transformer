package graphTransformer.transformation

import scala.deriving.Mirror
import co.blocke.scala_reflection.RType
import co.blocke.scala_reflection.RTypeRef
import dotty.tools.dotc.core.Types.Type
// import scala.reflect.ClassTag

class MyData[T](value: T) extends TData[T] {
  override def TypeName: String = "aboba"
}

// class InitTransformation[T](value: T) extends TransformationLinearPure[Nothing, MyData[T]] {
//   override def name: String           = "InitTransformation"
//   override def pureApply(_n: Nothing) = MyData(value)
// }

class AddOneTransgression extends TransformationLinearPure[MyData[Int], MyData[Int]] {
  override def name: String               = "AddOneTransgression"
  override def pureApply(input: Int): Int = input + 1
}

class ToListTransformation[T] extends TransformationLinearPure[T, List[T]] {
  override def name: String        = "ToListTransformation"
  override def pureApply(input: T) = List(input)
}

class ConcatListsTransformation[T] extends TransformationPolyPure[List[T], List[T]] {
  override def name: String                    = "ConcatListsTransformation"
  override def pureApply(inputs: Seq[List[T]]) = inputs.flatten.toList
}

// def testAboba[T](c: Type): Unit =

@main def temp(): Unit = {
  // val tInit   = new InitTransformation(5)
  val tAddOne = new AddOneTransgression
  val tToList = new ToListTransformation[Int]
  val tConcat = new ConcatListsTransformation[Int]

  // println(ClassTag(tInit.getClass).runtimeClass)

  // val tg = transitionGraph(
  //   Set(
  //     tInit,
  //     tAddOne,
  //     tToList,
  //     tConcat
  //   )
  // )
  println("Hello, world!")
  println("Hello, world!")
}
