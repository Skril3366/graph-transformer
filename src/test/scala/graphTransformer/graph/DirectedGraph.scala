package graphTransformer.graph

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DirectedGraphSpec extends AnyFlatSpec with Matchers {

  // .------------------------------.
  // |          .----------------.  |
  // v          v                |  |
  // Alice --> Bob --> David     |  |
  // |                    |      |  |
  // v                    v      |  |
  // Charlie ----------> Eva --> Frank
  val samplePeopleGraph = {
    val nodes: List[Node[String]] =
      List("Alice", "Bob", "Charlie", "David", "Eva", "Frank").map(Node(_))

    val edges: Set[DirectedEdge[String, Int]] = Set(
      ("Alice", "Bob", 1),
      ("Alice", "Charlie", 2),
      ("Bob", "David", 3),
      ("Charlie", "Eva", 4),
      ("David", "Eva", 5),
      ("Eva", "Frank", 6),
      ("Frank", "Alice", 7),
      ("Frank", "Bob", 8)
    ).map((from, to, n) => DirectedEdge(Node(from), Node(to), n))
    DirectedGraph[String, Int](nodes.toSet, edges)
  }

  it should "add a nodes correctly" in {
    val graph: DirectedGraph[Int, Int] = DirectedGraph(Set.empty, Set.empty)
    val nodes = (1 to 10).map(Node(_))
    val newGraph = graph.addNodes(nodes*)
    newGraph.nodes shouldEqual nodes.toSet
  }

  it should "not add edges that have non-existing nodes" in {
    DirectedGraph(Set.empty, Set.empty).addEdges(
      DirectedEdge(Node(1), Node(2), 3)
    ) shouldEqual Left(
      NoSuchNodesInGraph(Seq(Node(1), Node(2)))
    )
  }
  it should "should have correct adjacency matrix" in {
    def checkMatrix(name1: String, name2: String, n: Int) =
      samplePeopleGraph.adjacencyMatrix(Node(name1))(
        Node(name2)
      ) should contain only (
        DirectedEdge(Node(name1), Node(name2), n)
      )

    samplePeopleGraph.edges.map(edge =>
      checkMatrix(edge.from.value, edge.to.value, edge.value)
    )
  }

  it should "build correct line graph" in {
    def edge(name: String, from: Int, to: Int) =
      UndirectedEdge(Node(from), Node(to), name)
    val correctLineGraph = UndirectedGraph(
      samplePeopleGraph.edges.map(e => Node(e.value)),
      Set(
        edge("Alice", 7, 1),
        edge("Alice", 7, 2),
        edge("Bob", 1, 3),
        edge("Bob", 8, 3),
        edge("Charlie", 2, 4),
        edge("David", 3, 5),
        edge("Eva", 4, 6),
        edge("Eva", 5, 6),
        edge("Frank", 6, 7),
        edge("Frank", 6, 8)
      )
    )

    samplePeopleGraph.lineGraph shouldEqual correctLineGraph
  }

  it should "traverse graph correctly" in {

    val smallGraph = DirectedGraph(
      Set(Node("A"), Node("B"), Node("C"), Node("D")),
      Set(
        DirectedEdge(Node("A"), Node("B"), 1),
        DirectedEdge(Node("C"), Node("D"), 2)
      )
    )

    smallGraph.reachableNodes(Node("A")) shouldEqual Set(
      Node("A"),
      Node("B")
    )

    smallGraph.reachableNodes(Node("B")) shouldEqual Set(Node("B"))

    smallGraph.reachableNodes(Node("C")) shouldEqual Set(
      Node("C"),
      Node("D")
    )

    smallGraph.reachableNodes(Node("D")) shouldEqual Set(Node("D"))
  }

  it should "flatMapNodes should correctly work with single node graph" in {
    val singleNodeGraph = DirectedGraph(Set(Node(1)), Set.empty)
    val intoGraph = DirectedGraph(Set(Node(1), Node(2)), Set.empty)

    singleNodeGraph.flatMapNodes(_ => intoGraph) shouldEqual intoGraph
  }

  it should "flatMapNodes should correctly work with multi node graph" in {
    val multiNodeGraph = DirectedGraph(
      Set(Node(1), Node(2)),
      Set(DirectedEdge(Node(1), Node(2), 3))
    )

    val flatMapped = multiNodeGraph.flatMapNodes(n =>
      DirectedGraph(
        Set(Node(n * 2 + 1), Node(n * 2 + 2)),
        Set(DirectedEdge(Node(n * 2 + 1), Node(n * 2 + 2), n))
      )
    )

    val correctlyFlatMapped = DirectedGraph(
      Set(Node(3), Node(4), Node(5), Node(6)),
      Set(
        DirectedEdge(Node(3), Node(4), 1),
        DirectedEdge(Node(5), Node(6), 2),
        DirectedEdge(Node(3), Node(5), 3),
        DirectedEdge(Node(3), Node(6), 3),
        DirectedEdge(Node(4), Node(5), 3),
        DirectedEdge(Node(4), Node(6), 3),
      )
    )

    flatMapped shouldEqual correctlyFlatMapped
  }

}
