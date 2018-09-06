(ns io.axrs.re-css.css
  (:refer-clojure :exclude [class > + & next])
  (:require
   [clojure.walk :refer [postwalk]]
   [clojure.string :as string]))

(defn- css-identifier [prefix style]
  (cond
    (string? prefix) (str prefix)
    (keyword? prefix) (str (name prefix) "-" (hash style))))

(defn ->attr [[k v]]
  (str (name k) ": "
       (cond
         (number? v) v
         :else v)
       ";"))

(defn class-type [[k v :as kv]]
  (or (some-> k namespace keyword)
      :attrs))

(def comp-class (comp (partial = "&") namespace))
(def sub-class (comp (partial = ">") namespace))
(def child-class string?)

(declare ->css)

(defn- ->nested [root separator [child attrs]]
  (when child
    (get-in (->css [(str root separator (name child)) attrs]) [1 1])))

(defn- ->css
  [[class style]]
  (let [{:keys [descendant pseudo next compound child attrs adjacent]} (group-by class-type style)
        root (css-identifier class style)]
    (let [css (string/join
               \newline
               (remove nil?
                       (concat
                        [(str "." root "{" (apply str (map ->attr attrs)) "}")]
                        (map (partial ->nested root "::") pseudo)
                        (map (partial ->nested root "") compound)
                        (map (partial ->nested root " ~ ") next)
                        (map (partial ->nested root " + ") adjacent)
                        (map (partial ->nested root " > ") child)
                        (map (partial ->nested root " ") descendant))))]
      [class [root css]])))

(defn css [style-m]
  (->> style-m
       (map ->css)
       (reduce (fn [r [k v]] (assoc r k v)) {})))

(def ^:dynamic *parent* nil)

(defn class [& [m k v :as classes-and-attrs]]
  (let [base? (map? m)]
    (->> classes-and-attrs
         ((if base? rest identity))
         (apply hash-map)
         (into (if base? m (sorted-map))))))

(defn nest [n k m]
  (fn [c p]
    (assoc-in c [p (keyword n (name k))] m)))

(def pseudo (partial nest "pseudo"))
(def descendent (partial nest "descendant"))
(def nested descendent)
(def child (partial nest "child"))
(def > (partial nest "child"))
(def adjacent (partial nest "adjacent"))
(def + adjacent)
(def next (partial nest "next"))
(def compound (partial nest "compound"))
(def & compound)

(defn with [m v & fns]
  (reduce #(%2 %1 v) m fns))

(defn hash-of [m c]
  (str (name c) "-" (hash (c m))))
