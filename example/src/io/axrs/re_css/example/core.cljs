(ns io.axrs.re-css.example.core
  (:refer-clojure :exclude [count])
  (:require
    [io.axrs.re-css.core :refer [defui styled]]
    [reagent.core :as r :refer-macros [with-let]]
    [garden.stylesheet :as ss]
    [clojure.string :as string]))

(def ^:private show? (r/atom false))
(def ^:private count (r/atom (rand-int 5)))
(def ^:private css-updated (r/atom nil))

;; CSS STYLES --------------------------------------------------------------------------------

; A basic component style vector showing CSS properties which will eventually be attached
; to an inline stylesheet as a generated `button-x` class
(def ^:private button-style
  [:button {:background-color "#4CAF50"
            :border           "none"
            :color            "white"
            :display          "block"
            :font-size        "16px"
            :padding          "15px 32px"
            :text-align       "center"
            :text-decoration  "none"}
   (ss/at-media {:max-width "769px"}
     [:&:hover {:font-weight 'bold}])])

; An example extension of the default button-style changing the background color to blue
(def ^:private blue-button-style
  (assoc-in button-style [1 :background-color] "#008CBA"))

(def ^:private black-button-style
  (assoc-in button-style [1 :background-color] "#555555"))

(def ^:private red-button-style
  (assoc-in button-style [1 :background-color] "#ED2939"))

(def ^:private code-style
  [:code {:background-color "#f7f7f7"
          :padding          "15px"
          :border-left      "3px solid #7b7b7b"}])

;; Form 1  ----------------------------------------------------------------------------------

(defn render-button
  "A basic reagent button component"
  [attrs text]
  [:button (styled attrs [:button])
   text " | count " @count])

(defui form-1-symbol [blue-button-style] render-button)

(defui form-1 [blue-button-style] [attrs text]
  [:button (styled attrs [:button])
   text " | count " @count])

;; Form 2  ----------------------------------------------------------------------------------

(defn form-2-symbol-def
  "A basic form-2 reagent button component"
  [initial-attrs text]
  (let [initial-count @count]
    (fn [attrs text]
      [:button (styled initial-attrs attrs [:button])
       text " | count " @count
       " (initial was " initial-count ")"])))

(defui form-2-symbol [black-button-style] form-2-symbol-def)

(defui form-2 [black-button-style] [attrs text]
  (let [initial-count @count]
    (fn [attrs text]
      [:button (styled attrs [:button])
       text " | count " @count
       " (initial was " initial-count ")"])))

;; Form 3  ----------------------------------------------------------------------------------

(def form-3-symbol-def
  {:component-will-unmount (fn [this] (js/console.log "Form 3 unmounted"))
   :component-did-mount    (fn [this] (js/console.log "Form 3 mounted"))
   :reagent-render         (fn [attrs text]
                             [:button (styled attrs [:button])
                              text " | count " @count])})

(defui form-3-symbol [red-button-style] form-3-symbol-def)

(defui form-3 [red-button-style]
  {:component-will-unmount (fn [this] (js/console.log "Form 3 unmounted"))
   :component-did-mount    (fn [this] (js/console.log "Form 3 mounted"))
   :reagent-render         (fn [attrs text]
                             [:button (styled attrs [:button])
                              text " | count " @count])})

;; VIEWS  ----------------------------------------------------------------------------------

(defui code [code-style] [attrs data]
  [:pre (styled attrs [:code])
   data])

(defn- show-styles
  "Fetches the latest inline stylesheets from the document head and outputs their data into a pre block"
  []
  (let [styles (js/document.getElementsByTagName "style")]
    @css-updated
    [code {}
     [:<>
      "// ATTACHED Inline Stylesheets"
      \newline
      (string/join \newline
        (map #(aget % "firstChild" "data") (array-seq styles)))]]))

(defn view
  "A simple Reagent app view to track the number of button clicks"
  []
  (let [inc-counter #(swap! count inc)
        include-form-3? (odd? @count)]
    [:<>
     [render-button {:on-click #(swap! show? not)} "Toggle Buttons"]
     (when @show?
       [:<>
        [form-1-symbol {:on-click inc-counter} "Form 1 Symbol"]
        [form-1 {:on-click inc-counter} "Form 1"]
        [form-2-symbol {:on-click inc-counter} "Form 2 Symbol"]
        [form-2 {:on-click inc-counter} "Form 2"]
        (when include-form-3?
          [form-3 {:on-click inc-counter} "Form 3"])
        (when include-form-3?
          [form-3-symbol {:on-click inc-counter} "Form 3 Symbol"])])
     [show-styles]]))

;; BOOTSTRAP  ------------------------------------------------------------------------------

(defn mount-root
  []
  ; Note: Do NOT watch the sheets storage in production.
  ; It is only included here for debugging purposes to update what styles
  ; have been appended to the HEAD of the page
  (add-watch io.axrs.re-css.dom/attached :css-debug
    (fn [& _] (reset! css-updated (js/Date.now))))
  (r/render [view]
    (js/document.getElementById "example")))

(defn ^:export init
  []
  (mount-root))
