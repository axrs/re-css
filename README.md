# re-css

JSS integration with [reagent][1] (for use in SPA frameworks like [re-frame][2])

## Table of Contents

-   [Overview](#overview)

-   [Getting Started](#getting-started)

    -   [Example](#example)

-   [Code Quality](#code-quality)

## Overview

**re-css** provides a simplistic macro to independently style reagent components (Form-1, Form-2, and Form-3) in [re-frame][2]
or [reagent][1] applications. Ultimately removing the pains of cascading styles, name collisions and code/style separation.

**re-css** leverages [JSS][3] to inject individual inline stylesheets (defined with the component as EDN structures) into
the HEAD of the document JUST before the component is mounted (and removes the relevant styles once the
component has been removed).

**re-css** aims to:

-   Bring CSS and component code together
-   Reduce unnecessary CSS declaration (by only defining what the component needs, not what it has to override)
-   Remove a potential build tool (SASS/LESS)
-   Eliminate class name collisions
-   Provide a clean path moving forward (assist moving away from legacy styles)

## Getting Started

**re-css** is currently not ready for production use and may be considered experimental at this stage.

> TODO: Update Getting started once tests are complete and deploying to Clojars

### Example

```clojure
(ns io.axrs.re-css.example.core
  (:require
    [io.axrs.re-css.core :refer [defui styled]]))

;; CSS styles defined as EDN  ---------------------------------------------------------------

(def ^:private button-style
  {:button {:background-color "#4CAF50"
            :border           "none"
            :color            "white"
            :display          "block"
            :font-size        "16px"
            :padding          "15px 32px"
            :text-align       "center"
            :text-decoration  "none"}})

(def ^:private blue-button-style
  (assoc-in button-style [:button :background-color] "#008CBA"))

;; Form 1  ----------------------------------------------------------------------------------

(defn render-button [attrs text]
  [:button (styled attrs ["button"])
   text " | count " @count])

(defui form-1-symbol blue-button-style render-button)

(defui form-1 blue-button-style [attrs text]
  [:button (styled attrs ["button"])
   text " | count " @count])

;; Form 2  ----------------------------------------------------------------------------------

(defn form-2-symbol-def [initial-attrs text]
  (let [initial-count @count]
    (fn [attrs text]
      [:button (styled initial-attrs attrs ["button"])
       text " | count " @count
       " (initial was " initial-count ")"])))

(defui form-2-symbol button-style form-2-symbol-def)

(defui form-2 button-style [attrs text]
  (let [initial-count @count]
    (fn [attrs text]
      [:button (styled attrs ["button"])
       text " | count " @count
       " (initial was " initial-count ")"])))

;; Form 3  ----------------------------------------------------------------------------------

(def form-3-symbol-def
  {:component-will-unmount (fn [this] (js/console.log "Form 3 unmounted"))
   :component-did-mount    (fn [this] (js/console.log "Form 3 mounted"))
   :reagent-render         (fn [attrs text]
                             [:button (styled attrs ["button"])
                              text " | count " @count])})

(defui form-3-symbol button-style form-3-symbol-def)

(defui form-3 button-style
  {:component-will-unmount (fn [this] (js/console.log "Form 3 unmounted"))
   :component-did-mount    (fn [this] (js/console.log "Form 3 mounted"))
   :reagent-render         (fn [attrs text]
                             [:button (styled attrs ["button"])
                              text " | count " @count])})

(defn view []
  [:<>
   [render-button {:on-click #(swap! show? not)} "Toggle Buttons"]
   [form-1-symbol {:on-click inc-counter} "Form 1 Symbol"]
   [form-1 {:on-click inc-counter} "Form 1"]
   [form-2-symbol {:on-click inc-counter} "Form 2 Symbol"]
   [form-2 {:on-click inc-counter} "Form 2"]
   [form-3 {:on-click inc-counter} "Form 3"])
   [form-3-symbol {:on-click inc-counter} "Form 3 Symbol"])])
```

[View Full Example][4]

> You can run the example project using [Shadow-CLJS][7] by

```bash
git clone git@github.com:axrs/re-css.git
cd re-css/example
npm install
npx shadow-cljs watch example
# Open a browser to http://localhost:8080
```

## Code Quality

**re-css** uses several code quality tools (like linting) to ensure consistency in formatting between editors and developers.
These tools should be automatically applied before each commit through the use of the [`pre-commit` hook][12].

| Source            | Tool   | Website                                    |
| ----------------- | ------ | ------------------------------------------ |
| Markdown          | Remark | [https://remark.js.org/][5]                |
| CLJ/CLJC/CLJS/EDN | cljfmt | [https://github.com/weavejester/cljfmt][6] |

[1]: https://github.com/reagent-project/reagent

[2]: https://github.com/Day8/re-frame

[3]: https://github.com/cssinjs/jss

[4]: example/src/io/axrs/re_css/example/core.cljs

[5]: https://remark.js.org/

[6]: https://github.com/weavejester/cljfmt

[7]: http://shadow-cljs.org/

[8]: #overview

[9]: #getting-started

[10]: #example

[11]: #code-quality

[12]: githooks/pre-commit
