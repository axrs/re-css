(ns io.axrs.re-css.dom
  (:require
   [oops.core :refer [oget]]
   [clojure.string :as string]
   [com.rpl.specter :as sp]
   [garden.core :refer [css]]))

(defonce ^:private attached (atom {}))
(def ^:private ^:dynamic *document* js/document)

(defonce ^:private transform-fns (atom {}))

(defn- ^:dynamic ^js document-head []
  (aget (.getElementsByTagName *document* "head") 0))

(defn add-transformation [prop f]
  (swap! transform-fns update prop conj f))

(defn remove-transformation [prop f]
  (let [fns (seq (remove #{f} (get @transform-fns prop)))]
    (if fns
      (swap! transform-fns assoc prop fns)
      (swap! transform-fns dissoc prop))))

(defn- eval-styles [edn-style]
  (let [transform-fns @transform-fns
        transform-props (set (keys transform-fns))
        prop-to-transform (comp (partial contains? transform-props) first)]
    (->> edn-style
         (sp/transform (sp/walker fn?) #(%))
         (sp/transform [(sp/walker map?) sp/ALL prop-to-transform] (fn transform-prop [[k v]]
                                                                     (let [f (apply comp (get transform-fns k))]
                                                                       [k (f v)]))))))

(defn- attach-style
  "Takes a single style, generates the css, and attaches it to the head of the document"
  [[_ [style-identifier garden-data]]]
  (if-let [exist (get @attached style-identifier)]
    (do
      (swap! attached update-in [style-identifier 1] inc)
      (first exist))
    (let [style (.createElement *document* "style")
          css-str (css (eval-styles garden-data))]
      (.appendChild ^js style (.createTextNode *document* css-str))
      (.appendChild (document-head) style)
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

(defonce ^:private hash-key :io.axrs.re-css.core/classes)

(defn classes
  ([{css hash-key :as attrs} classes]
   (if css
     (css attrs classes)
     attrs))

  ([{css-styles hash-key :as style} {:keys [class] :as attrs} classes]
   (if css-styles
     (css-styles attrs classes)
     (let [class-names (->> classes
                            (map #(get-in style [% 0]))
                            (string/join \space))]
       {:class (str class-names \space class)}))))

(defn- ->str [v]
  (cond
    (keyword? v) (name v)
    (symbol? v) (str v)
    :else v))

(defn- polyfill-supports? [css-property css-value]
  (let [s (.-style (.createElement *document* "div"))]
    (set! (.-cssText s) (str css-property \: css-value))
    (not (string/blank? (.-cssText s)))))

(defn supports? [css-property css-value]
  (let [[css-property css-value] (map ->str [css-property css-value])]
    (if-let [native-check (oget js/window ["?CSS" "?supports"])]
      (native-check css-property css-value)
      (polyfill-supports? css-property css-value))))
