(defproject io.axrs/re-css "0.0.1-SNAPSHOT"
  :description "CSS-in-JS integration with reagent (for use in SPA frameworks like re-frame)"
  :url "https://github.com/axrs/re-css.git"
  :license {:name "MIT"}
  :min-lein-version "2.8.1"
  :source-paths ["src"]
  :test-paths ["test"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target" "test-target/js"]
  :dependencies [[thheller/shadow-cljs "2.6.6"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript
                  "1.10.238"
                  :exclusions
                  [com.google.javascript/closure-compiler-unshaded]]
                 [reagent "0.8.1"]]
  :plugins [[lein-cljfmt "0.6.0"]]
  :cljfmt {:indents {do-all   [[:inner 0]]
                     doto     [[:block 0]]
                     .use     [[:block 0]]
                     :require [[:block 0]]
                     require  [[:block 0]]}}
  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.10"]
                                  [pjstadig/humane-test-output "0.8.3"]]}}
  :deploy-repositories [["releases" {:sign-releases false :url "https://clojars.org/repo"}]
                        ["snapshots" {:sign-releases false :url "https://clojars.org/repo"}]])
