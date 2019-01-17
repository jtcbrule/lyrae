(ns lyrae.core
  (:require [clojupyter.protocol.mime-convertible :as mc]
            [clojure.data.json :as json]))


(def require-string
  "
<div id='uuid-%s'>
<script>
requirejs.config({
  baseUrl: 'https://cdn.jsdelivr.net/npm/',
  paths: {
    'vega-embed':  'vega-embed@3?noext',
    'vega-lib': 'vega-lib?noext',
    'vega-lite': 'vega-lite@2?noext',
    'vega': 'vega@3?noext'
  }
});

require(['vega-embed'], function(vegaEmbed) {
  let spec = %s;
  vegaEmbed('#uuid-%s', spec, {defaultStyle:true}).catch(console.warn);
  }, function(err) {
  console.log('Failed to load');
});
</script>
</div>
  ")


(def example-vega
  {:description "A simple bar chart",
   :width 360,
   :data {:values
          [{"a" "A", "b" 28}, {"a" "B", "b" 55}, {"a" "C", "b" 43},
           {"a" "D", "b" 91}, {"a" "E", "b" 81}, {"a" "F", "b" 53},
           {"a" "G", "b" 19}, {"a" "H", "b" 87}, {"a" "I", "b" 52}]}
   :mark "bar"
   :encoding {:x {:field "a", :type "ordinal"}
              :y {:field "b", :type "quantitative"}
              :tooltip {:field "b", :type "quantitative"}}})


(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn vega->html [v]
  (let [id (uuid)]
    (format require-string id (json/write-str (:spec v)) id)))


(defrecord Vega [spec]
  mc/PMimeConvertible
  (to-mime [this]
     (mc/stream-to-string
      {:text/html (vega->html this)})))

(defn vega
  "Wrap a map as a Vega object"
  [m]
  (->Vega m))


(comment

(extend-protocol mc/PMimeConvertible
  Vega
  (to-mime [this]
    (mc/stream-to-string
      {:text/html (vega->html this)})))

)
