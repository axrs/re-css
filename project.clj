(defproject io.axrs/re-css "1.1.0"
  :description "CSS-in-JS integration with reagent using garden (for use in SPA frameworks like re-frame)"
  :url "https://github.com/axrs/re-css.git"
  :license {:name         "Eclipse Public License - v1.0"
            :url          "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments     "same as Clojure"}
  :min-lein-version "2.8.1"
  :source-paths ["src"]
  :test-paths ["test"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target" "test-target/js"]
  :dependencies [
                 [thheller/shadow-cljs "2.8.81" :scope "provided"]
                 [garden "1.3.9"]
                 [reagent "0.8.1" :scope "provided"]
                 [binaryage/oops "0.7.0"]
                 [com.rpl/specter "1.1.3"]]
  :plugins [[lein-cljfmt "0.6.0"]]
  :cljfmt {:indents {doto     [[:block 0]]
                     :require [[:block 0]]
                     require  [[:block 0]]}}
  :profiles {:dev {:dependencies [[binaryage/devtools "0.9.11"]]}}
  :deploy-repositories [["releases" {:sign-releases false :url "https://clojars.org/repo"}]
                        ["snapshots" {:sign-releases false :url "https://clojars.org/repo"}]])
