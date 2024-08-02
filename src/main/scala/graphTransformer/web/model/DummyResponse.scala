package graphTransformer.web.model

import java.time.Instant

import io.circe.Decoder
import io.circe.Encoder
import sttp.tapir.Schema

case class DummyResponse(
    value: String,
    nested: Nested
)
object DummyResponse {
  // TODO: make macro for this
  given Decoder[DummyResponse] = Decoder.derived
  given Encoder[DummyResponse] = Encoder.derived
  given Schema[DummyResponse] = Schema.derived
}

case class Nested(
    value: Instant
)

object Nested {
  given Decoder[Nested] = Decoder.derived
  given Encoder[Nested] = Encoder.derived
  given Schema[Nested] = Schema.derived
}
