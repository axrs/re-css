(defproject io.axrs/re-css "0.0.1-SNAPSHOT"
  :min-lein-version "2.8.1"
  :source-paths ["src"]
  :test-paths ["test"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target" "test-target/js"]
  :dependencies [[thheller/shadow-cljs "2.6.3"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript
                  "1.10.238"
                  :exclusions
                  [com.google.javascript/closure-compiler-unshaded]]]
  :plugins [[lein-cljfmt "0.6.0"]]
  :cljfmt {:indents {do-all   [[:inner 0]]
                     doto     [[:block 0]]
                     .use     [[:block 0]]
                     :require [[:block 0]]
                     require  [[:block 0]]}}
  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.10"]
                                  [pjstadig/humane-test-output "0.8.3"]]}})