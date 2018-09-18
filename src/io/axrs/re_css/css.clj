(ns io.axrs.re-css.css
  (:refer-clojure :exclude [class > + & next])
  (:require
   [garden.core :refer [css]]
   [clojure.walk :refer [postwalk]]))

(defn ->css [suffix style]
  (prn style)
  (css style))

