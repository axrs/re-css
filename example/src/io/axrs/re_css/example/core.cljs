(ns io.axrs.re-css.example.core
  (:require
    [io.axrs.re-css.core :refer [defui]]
    [reagent.core :as r :refer-macros [with-let]]))

(def now (r/atom nil))

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

(defn render-fn [{:keys [css] :as attrs} text]
  [:button (merge attrs (css "button")) (str text @now)])

(defui button button-style [attrs text] render-fn)

(defui blue-button blue-button-style [attrs text]
  [:button (merge attrs (css "button")) (str text @now)])

(defui black-button black-button-style [attrs text]
  (fn [attrs text]
    [:button (merge attrs (css "button")) (str text @now)]))

(defn view []
  [:div
   [:button {:on-click #(js/alert "Default Button")} "Default Button"]
   [button {:on-click #(js/alert "Styled Button")} "Styled Button"]
   [blue-button {:on-click #(js/alert "Blue Styled Button")} "Blue Styled Button"]
   [black-button {:on-click #(js/alert "Blue Styled Button")} "Black Styled Button"]])

(defn mount-root
  []
  (js/setInterval #(reset! now (js/Date.now)) 1000)
  (r/render [view]
    (js/document.getElementById "example")))

(defn ^:export init
  []
  (mount-root))
