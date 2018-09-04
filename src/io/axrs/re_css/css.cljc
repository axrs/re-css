(ns io.axrs.re-css.css
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
  (cond
    (string? k) :nested-class
    (and (nil? (namespace k)) (map? v)) :nested-node
    (= "&" (namespace k)) :compound
    (= ">" (namespace k)) :direct
    (= "+" (namespace k)) :sibling
    (namespace k) :pseudo
    (keyword? k) :attrs))

(def comp-class (comp (partial = "&") namespace))
(def sub-class (comp (partial = ">") namespace))
(def child-class string?)

(declare ->css)

(defn- ->nested [root separator [child attrs]]
  (when child
    (get-in (->css [(str root separator (name child)) attrs]) [1 1])))

(defn- ->css
  [[class style]]
  (let [{:keys [nested-class pseudo nested-node compound direct attrs sibling]} (group-by class-type style)
        root (css-identifier class style)]
    (let [css (string/join
               \newline
               (remove nil?
                       (concat
                        [(str "." root "{" (apply str (map ->attr attrs)) "}")]
                        (map (partial ->nested root "::") pseudo)
                        (map (partial ->nested root "") compound)
                        (map (partial ->nested root " + ") sibling)
                        (map (partial ->nested root " > ") direct)
                        (map (partial ->nested root " ") nested-class)
                        (map (partial ->nested root " ") nested-node))))]
      [class [root css]])))

(defn css [style-m]
  (->> style-m
       (map ->css)
       (reduce (fn [r [k v]] (assoc r k v)) {})))
