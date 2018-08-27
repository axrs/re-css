# re-css

JSS integration with [reagent][1] (for use in SPA frameworks like [re-frame][2])

## Table of Contents

-   [Overview](#overview)

-   [Getting Started](#getting-started)

    -   [Example](#example)

-   [Code Quality](#code-quality)

## Overview

**re-css** provides a simplistic macro to independently style reagent components (Form-1, Form-2, and Form-3) in re-frame
or [reagent][1] applications. Ultimately removing the pains of cascading styles, name collisions and code/style separation.

**re-css** leverages [JSS][3] to inject individual inline stylesheets (defined with the component as EDN structures) into
the HEAD of the document JUST before the component is mounted (and removes the relevant styles once the
component has been removed).

**re-css** aims to:

-   Bring CSS and component code together
-   Reduce unnecessary CSS declaration (by only defining what the component needs, not what it needs to override)
-   Remove a potential build tool (SASS/LESS)
-   Eliminate class name collisions

## Getting Started

TODO:

-   Add Karma Unit Tests for `defui` and JSS wrapper
-   Setup CI integration
-   Clojar deployment

### Example

```clojure
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

(defn render-button
  "A basic reagent button component"
  [{:keys [css] :as attrs} text]
  [:button (merge attrs (when css (css "button")))
   text " | count " @count])

; A styled reagent button component using the basic component
; (Note: Form-0 is not really a thing. It's really a just a symbol)
(defui form-0 button-style [attrs text] render-button)

; Another styled reagent button component mirroring a Form 1
(defui form-1 button-style [attrs text]
  [:button (merge attrs (css "button"))
   text " | count " @count])

; Another styled reagent button component mirroring a Form 2 (state capturing)
(defui form-2 button-style [attrs text]
  (let [initial-count @count]
    (fn [attrs text]
      [:button (merge attrs (css "button"))
       text " | count " @count
       " (initial was " initial-count ")"])))

; Another styled reagent button component mirroring a Form 3 (lifecycle capturing)
(defui form-3 button-style [attrs text]
  {:component-will-unmount (fn [this] (js/console.log "Form 3 unmounted"))
   :component-did-mount    (fn [this] (js/console.log "Form 3 mounted"))
   :reagent-render         (fn [attrs text]
                             [:button (merge attrs (css "button"))
                              text " | count " @count])})
```

[View Source][4]

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
