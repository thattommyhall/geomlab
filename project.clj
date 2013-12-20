(defproject escher "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2120"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 ]

  :plugins [[lein-cljsbuild "1.0.1"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "none"
              :source-paths ["src"]
              :compiler {
                :output-to "escher.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
