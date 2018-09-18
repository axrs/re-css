(defproject io.axrs.re-css/example "0.0.1-SNAPSHOT"
  :min-lein-version "2.8.1"
  :source-paths ["src" "../src"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
  :plugins [[lein-parent "0.3.4"]]
  :parent-project {:path    "../project.clj"
                   :inherit [:dependencies :repositories [:profiles :dev]]})
