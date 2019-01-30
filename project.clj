(defproject io.axrs/re-css "0.2.1"
  :description "CSS-in-JS integration with reagent using garden (for use in SPA frameworks like re-frame)"
  :url "https://github.com/axrs/re-css.git"
  :license {:name         "Eclipse Public License - v 1.0"
            :url          "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments     "same as Clojure"}
  :min-lein-version "2.8.1"
  :source-paths ["src"]
  :test-paths ["test"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target" "test-target/js"]
  :dependencies [[thheller/shadow-cljs "2.7.20"]
                 [org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.10.439" :exclusions [com.google.javascript/closure-compiler-unshaded]]
                 [garden "1.3.6"]
                 [reagent "0.8.1"]
                 [com.rpl/specter "1.1.2"]]
  :plugins [[lein-cljfmt "0.6.0"]]
  :cljfmt {:indents {do-all   [[:inner 0]]
                     doto     [[:block 0]]
                     .use     [[:block 0]]
                     :require [[:block 0]]
                     require  [[:block 0]]}}
  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.10"]]}}
  :deploy-repositories [["releases" {:sign-releases false :url "https://clojars.org/repo"}]
                        ["snapshots" {:sign-releases false :url "https://clojars.org/repo"}]])
