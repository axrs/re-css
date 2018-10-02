(ns io.axrs.re-css.dom
  (:require
   [clojure.string :as string]
   [com.rpl.specter :as sp]
   [garden.core :refer [css]]))

(defonce ^:private attached (atom {}))
(defonce ^:private document (delay js/document))
(defonce ^:private document-head (delay (aget (js/document.getElementsByTagName "head") 0)))

(defn- eval-styles [style]
  (sp/transform (sp/walker fn?) #(%) style))

(defn- attach-style
  "Takes a single style, generates the css, and attaches it to the head of the document"
  [[_ [style-identifier garden-data]]]
  (if-let [exist (get @attached style-identifier)]
    (do
      (swap! attached update-in [style-identifier 1] inc)
      (first exist))
    (let [document @document
          style (.createElement document "style")
          css-str (css (eval-styles garden-data))]
      (.appendChild ^js style (.createTextNode ^js document css-str))
      (.appendChild ^js @document-head style)
      (swap! attached assoc style-identifier [style 1]))))

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
