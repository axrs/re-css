# re-css

JSS integration with [reagent][1] (for use in SPA frameworks like [re-frame][2])

## Table of Contents

-   [Overview][3]

-   [Getting Started][4]

    -   [Example][5]

-   [Code Quality][6]

## Overview

_re-css_ provides a simplistic macro to independently style reagent components (Form-1, Form-2, and Form-3) in re-frame
or [reagent][1] applications. Ultimately removing the pains of cascading styles, name collisions and code/style separation.

_re-css_ leverages [JSS][7] to inject individual inline stylesheets (defined with the component as EDN structures) into
the HEAD of the document before the JUST before the component is mounted and removes the relevant styles once the
component has been removed.

_re-css_ aims to:

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

[View Source][8]

You can also see and play with the example code by using Shadow-CLJS to compile and watch the source files for any changes

```bash
git clone git@github.com:axrs/re-css.git
cd re-css/example
npm install
npx shadow-cljs watch example
# Open a browser to http://localhost:8080
```

## Code Quality

_re-css_ uses several code quality tools (like linting) to ensure consistency in formatting between editors and developers.
These tools should be automatically applied before each commit through the use of the `pre-commit` hook.

| Source            | Tool   | Website                                     |
| ----------------- | ------ | ------------------------------------------- |
| Markdown          | Remark | [https://remark.js.org/][9]                 |
| CLJ/CLJC/CLJS/EDN | cljfmt | [https://github.com/weavejester/cljfmt][10] |

[1]: https://github.com/reagent-project/reagent

[2]: https://github.com/Day8/re-frame

[3]: #overview

[4]: #getting-started

[5]: #example

[6]: #code-quality

[7]: https://github.com/cssinjs/jss

[8]: example/src/io/axrs/re_css/example/core.cljs

[9]: https://remark.js.org/

[10]: https://github.com/weavejester/cljfmt
