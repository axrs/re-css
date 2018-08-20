(defproject io.axrs.re-css/example "0.0.1-SNAPSHOT"
  :min-lein-version "2.8.1"
  :source-paths ["src" "../src"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]
  :dependencies [[thheller/shadow-cljs "2.4.33"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript
                  "1.10.238"
                  :exclusions
                  [com.google.javascript/closure-compiler-unshaded]]
                 [reagent "0.8.1"]])
