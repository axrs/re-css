(ns io.axrs.re-css.jss
  (:require
   ["jss" :as jss]
   ["jss-nested" :default nested]
   ["jss-vendor-prefixer" :default vendor-prefixer]
   [clojure.string :as string]))

(defn styled
  ([{css :io.axrs.re-css.core/styled :as attrs} classes]
   (if css
     (css attrs classes)
     attrs))

  ([jss-classes {:keys [class] :as attrs} classes]
   (let [classes (->> classes
                      (map (partial aget jss-classes))
                      (string/join " "))]
     (-> attrs
         (assoc :class (str classes " " class))
         (dissoc :styled)))))

(defonce ^:private sheets (atom {}))
(defonce inst (delay
               (doto (jss/create #js {:insertionPoint "jss-insertion-point"})
                     (.use
                       (nested)
                       (vendor-prefixer)))))

(defn attach [style]
  (let [hash (hash style)
        [existing] (get @sheets hash)]
    (if existing
      (do
        (swap! sheets update-in [hash 1] inc)
        [hash existing])
      (let [style (->> style
                       clj->js
                       (.createStyleSheet @inst)
                       (.attach))]
        (swap! sheets assoc hash [style 1])
        [hash style]))))

(defn detach [hash]
  (let [[style count] (get @sheets hash)]
    (if (= 1 count)
      (do
        (.detach style)
        (swap! sheets dissoc hash))
      (swap! sheets update-in [hash 1] dec))))
