(require
  '[cljs.pprint :refer [cl-format]])
(def ^:private util (js/require "util"))
(def ^:private shx (js/require "shelljs"))
(def ^:private exec (util.promisify shx.exec))

(def ^:private resource-dir "./resources/public/")
(def ^:private js-dir (str resource-dir "js"))

(defn- left-pad
  ([s len] (left-pad s len " "))
  ([s len ch] (cl-format nil (str "~" len ",'" ch "d") (str s))))

(defn- print-args
  [arglist]
  (let [longest (apply max (map (comp count str) arglist))]
    (println "\t" "(" arglist ")")
    (doseq [arg arglist]
      (let [{:keys [tag]} (meta arg)]
        (println "\t\t" (left-pad arg longest)
                 ": " (or tag "<undocumented>"))))))

(defn- print-doc
  [var]
  (let [{:keys [doc name arglists]} (meta var)]
    (when doc
      (println name)
      (println "\t" doc)
      (when (seq (first arglists)) (mapv print-args arglists)))))

(def ^:private rm-rf
  #(some->> %
            clj->js
            (shx.rm "-rf")))

(defn- do-all
  [& promises]
  (js/Promise.all (for [p promises]
                    (.catch (p) js/console.error))))

(defn- chain
  [p & promises]
  (-> (p)
      (.then #(apply chain promises))
      (.catch js/console.error)))

(defn deps
  "Installs or updates all of the projects dependencies"
  []
  (println "Installing Dependencies...")
  (do-all
    #(exec "npm install")
    #(exec "lein deps")))

(defn clean
  "Cleans the project of all compiled/generated sources"
  []
  (println "Cleaning...")
  (rm-rf [js-dir ".shadow-cljs/builds/*" ".cpcache" "target/*"]))

(defn build
  "Builds the application"
  []
  (println "Building...")
  (do-all
    #(exec "npx shadow-cljs release app --source-maps")))

(defn unit-test
  "Runs unit tests"
  []
  (println "Testing...")
  (chain
   #(exec "npx shadow-cljs compile test")
   #(exec "npx karma start --single-run")))

(defn lint
  "Runs various linters over the project files"
  []
  (println "Linting...")
  (do-all
    #(exec "lein cljfmt check")
    #(exec "shellcheck *.sh githooks/*")))

(defn format
  "Formats all project source files in a consistent manner"
  []
  (println "Formatting...")
  (do-all
    #(exec "lein cljfmt fix")
    #(exec "npx remark . --use toc --use bookmarks --use remark-preset-lint-recommended --use remark-reference-links -o")))

(defn help
  "Prints the application usage"
  []
  (let [fns (vals (ns-publics 'cljs.user))]
    (println "DESCRIPTION")
    (println (left-pad "" 80 \-))
    (->> fns
         (sort-by str)
         (map print-doc)
         doall)))

(def ^:private cmd-or-help (fnil symbol "help"))
(defonce ^:private verbose-flag #{"-v"})

(defn -main
  [[cmd & args]]
  (when (some verbose-flag args) (set! shx.config.verbose true))
  (let [args (remove verbose-flag args)
        cmd (cmd-or-help cmd)
        invoke #(apply % args)]
    (->> cmd
         cmd-or-help
         (get (ns-publics 'cljs.user))
         invoke)))

(-main *command-line-args*)
