# re-css

CSS-in-JS integration with [reagent](https://github.com/reagent-project/reagent) (for use in SPA frameworks like [re-frame](https://github.com/day8/re-frame))

[![Clojars Project](https://img.shields.io/clojars/v/io.axrs/re-css.svg)](https://clojars.org/io.axrs/re-css)
[![CircleCI](https://circleci.com/gh/axrs/re-css.svg?style=svg)](https://circleci.com/gh/axrs/re-css)


## Table of Contents

* [Overview](#overview)

* [Getting Started](#getting-started)

  * [Example](#example)

* [Code Quality](#code-quality)


## Overview

**re-css** provides a simplistic macro to independently style reagent components (Form-1, Form-2, and Form-3) in [re-frame](https://github.com/day8/re-frame)
or [reagent](https://github.com/reagent-project/reagent) applications. Ultimately removing the pains of cascading styles, name collisions and code/style separation.

**re-css** injects individual inline stylesheets (defined adjacent to the component as EDN structures) into
the HEAD of the document JUST before the component is mounted; and subsequently removes the styles once the
component has been removed. All styles are [Garden](https://github.com/noprompt/garden) structures.

**re-css** aims to:

* Bring CSS and component code together
* Reduce unnecessary CSS declaration (by only defining what the component needs, not what it has to override)
* Remove a potential build tool (SASS/LESS)
* Eliminate class name collisions
* Provide a clean path moving forward (assist moving away from legacy styles)


## Getting Started

**re-css** is currently not ready for production use and may be considered experimental at this stage.

> TODO: Update Getting started once tests are complete and deploying to Clojars

### Example

![Example](example/example.gif)

```clojure
(ns io.axrs.re-css.example.core
  (:require
    [io.axrs.re-css.core :refer [defui classes]]
    [garden.stylesheet :as ss]

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
   [:span (fn [] {:eval-before-mount true})] ; Note: This style will be evaluated ONCE before mount
   (ss/at-media {:max-width "769px"}
     [:&:hover {:font-weight 'bold}])])

; An example extension of the default button-style changing the background color to blue
(def ^:private blue-button-style
  (assoc-in button-style [1 :background-color] "#008CBA"))

(def ^:private black-button-style
  (assoc-in button-style [1 :background-color] "#555555"))

(def ^:private red-button-style
  (assoc-in button-style [1 :background-color] "#ED2939"))

;; Form 1  ----------------------------------------------------------------------------------

(defn render-button
  "A basic reagent button component"
  [attrs text]
  [:button (merge attrs (classes attrs [:button])) text])

(defui form-1-symbol [blue-button-style] render-button)

(defui form-1 [blue-button-style] [attrs text]
  [:button (merge attrs (classes attrs [:button])) text])

;; Form 2  ----------------------------------------------------------------------------------

(defn form-2-symbol-def
  "A basic form-2 reagent button component"
  [initial-attrs text]
  (let [initial-count @count]
    (fn [attrs text]
      [:button (merge attrs (classes initial-attrs attrs [:button])) text])))

(defui form-2-symbol [black-button-style] form-2-symbol-def)

(defui form-2 [black-button-style] [attrs text]
  (let [initial-count @count]
    (fn [attrs text]
      [:button (merge attrs (classes attrs [:button])) text])))

;; Form 3  ----------------------------------------------------------------------------------

(def form-3-symbol-def
  {:component-will-unmount (fn [this] (js/console.log "Form 3 unmounted"))
   :component-did-mount    (fn [this] (js/console.log "Form 3 mounted"))
   :reagent-render         (fn [attrs text]
                             [:button (merge attrs (classes attrs [:button])) text])})

(defui form-3-symbol [red-button-style] form-3-symbol-def)

(defui form-3 [red-button-style]
  {:component-will-unmount (fn [this] (js/console.log "Form 3 unmounted"))
   :component-did-mount    (fn [this] (js/console.log "Form 3 mounted"))
   :reagent-render         (fn [attrs text]
                             [:button (merge attrs (classes attrs [:button])) text])})
```

[View Full Example](example/src/io/axrs/re_css/example/core.cljs)

> Running the example using [Shadow-CLJS](http://shadow-cljs.org/):

```bash
git clone git@github.com:axrs/re-css.git
cd re-css/example
npm install
npx shadow-cljs watch example
# Open a browser to http://localhost:8080
```


## Code Quality

**re-css** uses several code quality tools (like linting) to ensure consistency in formatting between editors and developers.
These tools should be automatically applied before each commit through the use of the [`pre-commit` hook](githooks/pre-commit).

| Source            | Tool   | Website                                 |
| ----------------- | ------ | --------------------------------------- |
| Markdown          | Remark | <https://remark.js.org/>                |
| CLJ/CLJC/CLJS/EDN | cljfmt | <https://github.com/weavejester/cljfmt> |
