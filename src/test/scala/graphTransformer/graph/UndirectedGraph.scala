package graphTransformer.graph

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class UndirectedGraphSpec extends AnyFlatSpec with Matchers {

  // .------------------------------.
  // |          .----------------.  |
  // |          |                |  |
  // Alice --> Bob --> David     |  |
  // |                    |      |  |
  // |                    |      |  |
  // Charlie ----------- Eva --- Frank
  val samplePeopleGraph = {
    val nodes =
      List("Alice", "Bob", "Charlie", "David", "Eva", "Frank").map(Node(_))

    val edges = List(
      ("Alice", "Bob", 1),
      ("Alice", "Charlie", 2),
      ("Bob", "David", 3),
      ("Charlie", "Eva", 4),
      ("David", "Eva", 5),
      ("Eva", "Frank", 6),
      ("Frank", "Alice", 7),
      ("Frank", "Bob", 8)
    ).map((from, to, n) => UndirectedEdge(Node(from), Node(to), n))
    UndirectedGraph(nodes.toSet, edges.toSet)
  }

  it should "add a nodes correctly" in {
    val graph: UndirectedGraph[Int, Int] = UndirectedGraph(Set.empty, Set.empty)
    val nodes = (1 to 10).map(Node(_))
    val newGraph = graph.addNodes(nodes*)
    newGraph.nodes shouldEqual nodes.toSet
  }

  it should "not add edges that have non-existing nodes" in {
    UndirectedGraph(Set.empty, Set.empty).addEdges(
      UndirectedEdge(Node(1), Node(2), 3)
    ) shouldEqual Left(
      NoSuchNodesInGraph(Seq(Node(1), Node(2)))
    )
  }

  it should "should have correct adjacency matrix" in {
    def checkMatrix(name1: String, name2: String) = {
      val n1 = Node(name1)
      val n2 = Node(name2)
      List(
        UndirectedEdge(n1, n2, ()),
        UndirectedEdge(n2, n1, ())
      ) should contain
      samplePeopleGraph.adjacencyMatrix(n1)(n2)
    }

    samplePeopleGraph.edges.map(edge =>
      checkMatrix(edge.nodeOne.value, edge.nodeTwo.value)
      checkMatrix(edge.nodeTwo.value, edge.nodeOne.value)
    )
  }

  it should "build correct line graph" in {
    def edge(name: String, edges: List[Int]) = for {
      from <- edges
      to <- edges
      edge <-
        if from != to then List(UndirectedEdge(Node(from), Node(to), name))
        else List.empty
    } yield edge

    val correctLineGraph = UndirectedGraph(
      samplePeopleGraph.edges.map(e => Node(e.value)),
      Set(
        edge("Alice", List(1, 2, 7)),
        edge("Bob", List(1, 3, 8)),
        edge("Charlie", List(2, 4)),
        edge("David", List(3, 5)),
        edge("Eva", List(4, 5, 6)),
        edge("Frank", List(6, 8, 7))
      ).flatten
    )

    samplePeopleGraph.lineGraph shouldEqual correctLineGraph
  }

  it should "compare correctly undirected edges" in {
    val edge1 = UndirectedEdge(Node(1), Node(2), 3)
    val edge2 = UndirectedEdge(Node(2), Node(1), 3)
    val edge3 = UndirectedEdge(Node(2), Node(3), 3)
    val edge4 = UndirectedEdge(Node(1), Node(2), 4)
    val int = 3
    edge1 should equal(edge2)
    edge1 shouldNot equal(edge3)
    edge1 shouldNot equal(edge3)
    edge1 shouldNot equal(int)
  }
}
