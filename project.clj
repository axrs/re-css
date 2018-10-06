(defproject io.axrs/re-css "0.1.2-SNAPSHOT"
  :description "CSS-in-JS integration with reagent using garden (for use in SPA frameworks like re-frame)"
  :url "https://github.com/axrs/re-css.git"
  :license {:name "MIT"}
  :min-lein-version "2.8.1"
  :source-paths ["src"]
  :test-paths ["test"]
  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target" "test-target/js"]
  :dependencies [[garden "1.3.6"]
                 [reagent "0.8.1"]
                 [com.rpl/specter "1.1.1"]]

  :profiles {:dev {:plugins      [[lein-cljfmt "0.6.0"]]
                   :dependencies [[thheller/shadow-cljs "2.6.10"]
                                  [org.clojure/clojure "1.9.0"]
                                  [org.clojure/clojurescript "1.10.238" :exclusions [com.google.javascript/closure-compiler-unshaded]]
                                  [binaryage/devtools "0.9.10"]
                                  [pjstadig/humane-test-output "0.8.3"]]}}

  :cljfmt {:indents {do-all   [[:inner 0]]
                     doto     [[:block 0]]
                     println  [[:inner 0]]
                     chain    [[:block 0]]
                     .use     [[:block 0]]
                     :require [[:block 0]]
                     require  [[:block 0]]}}

  :deploy-repositories [["clojars" {:url           "https://clojars.org/repo"
                                    :username      :env/clojars_username
                                    :password      :env/clojars_password
                                    :sign-releases false}]])
