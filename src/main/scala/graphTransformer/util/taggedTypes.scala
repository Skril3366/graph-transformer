package graphTransformer.util.taggedTypes

opaque type Tagged[+V, +Tag] = V

type @@[+V, +Tag] = V & Tagged[V, Tag]

def tag[Tag]: [V] => V => V @@ Tag =
  [V] => (v: V) => v.asInstanceOf[V @@ Tag]

implicit def convertToTagged[V, Tag](v: V): V @@ Tag = v.asInstanceOf[V @@ Tag]
