(ns io.axrs.re-css.example.core
  (:require
    [io.axrs.re-css.core :refer [defui]]
    [reagent.core :as r :refer-macros [with-let]]))

(def ^:private button-style
  {:button {
            :background-color "#4CAF50"
            :border           "none"
            :color            "white"
            :display          "inline-block"
            :font-size        "16px"
            :padding          "15px 32px"
            :text-align       "center"
            :text-decoration  "none"}})

(def ^:private blue-button-style
  (assoc-in button-style [:button :background-color] "#008CBA"))

(def ^:private black-button-style
  (assoc-in button-style [:button :background-color] "#555555"))

(defn render [css]
  (fn [attrs text]
    [:button (merge attrs (css "button")) text]))

(defui button button-style [attrs text]
  (render css))

(defui blue-button blue-button-style [attrs text]
  [:button (merge attrs (css "button")) text])

(defui black-button black-button-style [attrs text]
  (fn [attrs text]
    [:button (merge attrs (css "button")) text]))

(defn view []
  [:div
   [:button {:on-click #(js/alert "Default Button")} "Default Button"]
   [button {:on-click #(js/alert "Styled Button")} "Styled Button"]
   [blue-button {:on-click #(js/alert "Blue Styled Button")} "Blue Styled Button"]
   [black-button {:on-click #(js/alert "Blue Styled Button")} "Black Styled Button"]])

(defn mount-root
  []
  (r/render [view]
    (js/document.getElementById "example")))

(defn ^:export init
  []
  (mount-root))
