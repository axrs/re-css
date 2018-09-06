(ns io.axrs.re-css.dom
  (:require
   [clojure.string :as string]))

(defonce ^:private attached (atom {}))
(defonce ^:private document (delay js/document))
(defonce ^:private document-head (delay (aget (js/document.getElementsByTagName "head") 0)))

(defn- attach-style [[_ [identifier css-str]]]
  (if-let [exist (get @attached identifier)]
    (do
      (swap! attached update-in [identifier 1] inc)
      (first exist))
    (let [document @document
          style (.createElement document "style")]
      (.appendChild style (.createTextNode document css-str))
      (.appendChild @document-head style)
      (swap! attached assoc identifier [style 1]))))

(defn- detach-style [[_ [identifier _]]]
  (let [[element count] (get @attached identifier)]
    (case count
      nil nil
      1 (do
          (.remove ^js element)
          (swap! attached dissoc identifier))
      (swap! attached update-in [identifier 1] dec))))

(defn attach [styles]
  (mapv attach-style styles))

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
