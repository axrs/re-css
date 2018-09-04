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
  (let [{:keys [nested-class pseudo nested-node compound direct attrs]} (group-by class-type style)
        root (css-identifier class style)]
    (let [css (string/join
               \newline
               (remove nil?
                       (concat
                        [(str "." root "{" (apply str (map ->attr attrs)) "}")]
                        (map (partial ->nested root "::") pseudo)
                        (map (partial ->nested root "") compound)
                        (map (partial ->nested root " > ") direct)
                        (map (partial ->nested root " ") nested-class)
                        (map (partial ->nested root " ") nested-node))))]
      [class [root css]])))

(defn css [style-m]
  (->> style-m
       (map ->css)
       (reduce (fn [r [k v]] (assoc r k v)) {})))

(defonce ^:private attached (atom nil))

(def ^:private document-head (delay (aget (js/document.getElementsByTagName "head") 0)))

(defn- append-style [[k [identifier css-str]]]
  (if-let [exist (get @attached identifier)]
    (do
      (swap! attached update-in [identifier 1] inc)
      (first exist))
    (let [head @document-head
          style (js/document.createElement "style")]
      (.appendChild style (js/document.createTextNode css-str))
      (.appendChild head style)
      (swap! attached assoc identifier [style 1]))))

(defn attach [styles]
  (mapv append-style styles))

(defn detach-style [[k [identifier css-str]]]
  (let [[element count] (get @attached identifier)]
    (if (= 1 count)
      (do
        (.remove ^js element)
        (swap! attached dissoc identifier))
      (swap! attached update-in [identifier 1] dec))))

(defn detach [styles]
  (mapv detach-style styles))

(defonce ^:private hash-key :io.axrs.re-css.core/styled)

(defn styled
  ([{css hash-key :as attrs} classes]
   (if css
     (css attrs classes)
     attrs))

  ([{css-styles hash-key :as style} {:keys [class] :as attrs} classes]
   (if css-styles
     (css-styles attrs classes)
     (let [class-names (->> classes
                            (map #(get-in style [% 0]))
                            (string/join " "))]
       (-> attrs
           (assoc :class (str class-names " " class))
           (dissoc :io.axrs.re-css.core/styled))))))
