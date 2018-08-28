(ns io.axrs.re-css.example.core
  (:refer-clojure :exclude [count])
  (:require
    [io.axrs.re-css.core :refer [defui styled]]
    [reagent.core :as r :refer-macros [with-let]]))

(def ^:private count (r/atom (rand-int 5)))
(def ^:private jss-updated (r/atom nil))

;; CSS STYLES --------------------------------------------------------------------------------

; A basic component style map showing CSS properties which will eventually be attached
; to an inline stylesheet as a generated `button-x-x-x` class
(def ^:private button-style
  {:button {:background-color "#4CAF50"
            :border           "none"
            :color            "white"
            :display          "inline-block"
            :font-size        "16px"
            :padding          "15px 32px"
            :text-align       "center"
            :text-decoration  "none"}})

; An example extension of the default button-style changing the background color to blue
(def ^:private blue-button-style
  (assoc-in button-style [:button :background-color] "#008CBA"))

(def ^:private black-button-style
  (assoc-in button-style [:button :background-color] "#555555"))

(def ^:private red-button-style
  (assoc-in button-style [:button :background-color] "#ED2939"))

(def ^:private code-style
  {:code {:background-color "#f7f7f7"
          :padding          "15px"
          :border-left      "3px solid #7b7b7b"}})

;; UI COMPONENTS  ----------------------------------------------------------------------------

(defn render-button
  "A basic reagent button component"
  [attrs text]
  [:button (styled attrs ["button"])
   text " | count " @count])

; A styled reagent button component using the basic component
; (Note: Form-0 is not really a thing. It's really a just a symbol)
(defui form-0 button-style [attrs text] render-button)

; Another styled reagent button component mirroring a Form 1
(defui form-1 blue-button-style [attrs text]
  [:button (styled attrs ["button"])
   text " | count " @count])

; Another styled reagent button component mirroring a Form 2 (state capturing)
(defui form-2 black-button-style [attrs text]
  (let [initial-count @count]
    (fn [attrs text]
      [:button (styled attrs ["button"])
       text " | count " @count
       " (initial was " initial-count ")"])))

; Another styled reagent button component mirroring a Form 3 (lifecycle capturing)
(defui form-3 red-button-style [attrs text]
  {:component-will-unmount (fn [this] (js/console.log "Form 3 unmounted"))
   :component-did-mount    (fn [this] (js/console.log "Form 3 mounted"))
   :reagent-render         (fn [attrs text]
                             [:button (styled attrs ["button"])
                              text " | count " @count])})


(defui code code-style [attrs data]
  [:pre (styled attrs ["code"])
   data])

;; VIEWS  ----------------------------------------------------------------------------------

(defn- show-styles
  "Fetches the latest inline stylesheets from the document head and outputs their data into a pre block"
  []
  (let [styles (js/document.getElementsByTagName "style")]
    @jss-updated
    [code {}
     (map #(aget % "firstChild" "data") (array-seq styles))]))

(defn view
  "A simple Reagent app view to track the number of button clicks"
  []
  (let [inc-counter #(swap! count inc)]
    [:div
     [render-button {:on-click inc-counter} "Default"]
     [form-0 {:on-click inc-counter} "Form 0"]
     [form-1 {:on-click inc-counter} "Form 1"]
     [form-2 {:on-click inc-counter} "Form 2"]
     (when (zero? (mod @count 5))
       [form-3 {:on-click inc-counter} "Form 3"])
     [show-styles]]))

;; BOOTSTRAP  ------------------------------------------------------------------------------

(defn mount-root
  []
  ; Note: Do NOT watch the sheets storage in production.
  ; It is only included here for debugging purposes to update what styles
  ; have been appended to the HEAD of the page
  (add-watch io.axrs.re-css.jss/sheets :jss-debug
    (fn [& _] (reset! jss-updated (js/Date.now))))
  (r/render [view]
    (js/document.getElementById "example")))

(defn ^:export init
  []
  (mount-root))
