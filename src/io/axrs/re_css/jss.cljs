(ns io.axrs.re-css.jss
  (:require
   ["jss" :as jss]
   ["jss-nested" :default nested]
   ["jss-vendor-prefixer" :default vendor-prefixer]
   [clojure.string :as string]))

(defonce ^:private sheets (atom {}))
(defonce inst (delay
               (doto
                 (jss/create #js {:insertionPoint "jss-insertion-point"})
                 (.use
                   (nested)
                   (vendor-prefixer)))))

(defonce ^:private hash-key :io.axrs.re-css.core/styled)

(defn styled
  ([{css hash-key :as attrs} classes]
   (if css
     (css attrs classes)
     attrs))

  ([jss-hash {:keys [class] :as attrs} classes]
   (if (map? jss-hash)
     ((hash-key jss-hash) attrs classes)
     (let [sheet (get-in @sheets [jss-hash :sheet])
           class-names (->> classes
                            (map (partial aget sheet "classes"))
                            (string/join " "))]
       (-> attrs
           (assoc :class (str class-names " " class))
           (dissoc :io.axrs.re-css.core/styled))))))

(defn load
  "Loads a style sheet in preparation for DOM attachment"
  [style]
  (let [hash (hash style)
        existing (get @sheets hash)]
    (when-not existing
      (swap! sheets assoc hash {:style style}))
    hash))

(defn attach [hash]
  (let [cache @sheets]
    (if (get-in cache [hash :count])
      (do
        (swap! sheets update-in [hash :count] inc)
        (get-in cache [hash :sheet]))
      (let [sheet (->> (get-in cache [hash :style])
                       clj->js
                       (.createStyleSheet @inst)
                       (.attach))]
        (swap! sheets update-in [hash] assoc :count 1 :sheet sheet)
        sheet))))

(defn detach [hash]
  (let [{:keys [sheet count]} (get @sheets hash)]
    (if (= 1 count)
      (do
        (.detach ^js sheet)
        (swap! sheets update-in [hash] dissoc :sheet :count))
      (swap! sheets update-in [hash :count] dec))))
