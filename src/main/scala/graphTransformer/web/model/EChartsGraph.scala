package graphTransformer.web.model

import graphTransformer.util.taggedTypes._
import io.circe.Encoder
import io.circe.generic.semiauto._
import io.circe.syntax._

trait NodeIdTag
type NodeId = Int @@ NodeIdTag
given nodeIdEncoder: Encoder[NodeId] = Encoder.encodeInt.contramap(identity)

case class EChartsGraph(
    categories: List[Category],
    nodes: List[Node],
    edges: List[Edge]
) {

  given echartsGraphEncoder: Encoder[EChartsGraph] = deriveEncoder

  def asHtml =
    s"""
   |<h1>Cool graph frontend</h1>
   |<div id="main" style="width: 100%; height: 100vh;"></div>
   |<script src="/scripts/echarts.min.js"></script>
   |<script src="/scripts/jquery.min.js"></script>
   |
   |<script type="module">
   |    var chartDom = document.getElementById('main');
   |    var myChart = echarts.init(chartDom);
   |    var option;
   |    option = {
   |        legend: {
   |            data: ${categories.map(_.name).asJson},
   |        },
   |        series: [
   |            {
   |                type: 'graph',
   |                layout: 'force',
   |                animation: false,
   |                label: {
   |                    position: 'right',
   |                    formatter: '{b}'
   |                },
   |                draggable: true,
   |                data: ${nodes.asJson.noSpaces},
   |                categories: ${categories.asJson.noSpaces},
   |                force: {
   |                    edgeLength: 5,
   |                    repulsion: 20,
   |                    gravity: 0.2
   |                },
   |                edges: ${edges.asJson.noSpaces}
   |            }
   |        ]
   |    };
   |    myChart.setOption(option);
   |    option && myChart.setOption(option);
   |</script>
    """.stripMargin
}

object EChartsGraph {

  val sample = EChartsGraph(
    categories = List(
      Category("HTMLElement", List.empty, "HTMLElement"),
      Category("HTMLElement", List.empty, "HTMLElement"),
      Category("WebGL", List.empty, "WebGLRenderingContext"),
      Category("SVG", List.empty, "SVGElement"),
      Category("CSS", List.empty, "CSSRule"),
      Category("Other", List.empty, "")
    ),
    nodes = List(
      Node(1, "AnalyserNode", 1, 4),
      Node(2, "AudioNode", 1, 4),
      Node(3, "Uint8Array", 1, 4),
      Node(4, "Float32Array", 1, 4),
      Node(5, "ArrayBuffer", 1, 4),
      Node(6, "ArrayBufferView", 1, 4),
      Node(7, "Attr", 1, 4),
      Node(8, "Node", 1, 4),
    ),
    edges = List(
      Edge(0, 1),
      Edge(0, 2),
      Edge(0, 3),
      Edge(4, 4),
    )
  )
}

case class Category(
    name: String,
    keywords: List[String],
    base: String
)

object Category {
  given encoder: Encoder[Category] = deriveEncoder
}

case class Node(
    id: NodeId,
    name: String,
    value: Int,
    category: Int
)
object Node {
  given encoder: Encoder[Node] = deriveEncoder
}

case class Edge(
    source: NodeId,
    traget: NodeId
)

object Edge {
  given encoder: Encoder[Edge] = deriveEncoder
}
