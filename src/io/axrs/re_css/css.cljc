(ns io.axrs.re-css.css
  (:refer-clojure :exclude [class > + & next])
  (:require
   [garden.core :refer [css]]
   [com.rpl.specter :as sp]
   [clojure.walk :refer [postwalk]]))

(defn- identify [suffix key]
  (str (name key) "-" suffix))

(defn- mappify
  "Extracts the first level keys and assocs the top level classes into a map"
  [suffix style]
  (let [keys (sp/select [sp/ALL keyword?] style)
        identify (partial identify suffix)
        identities (map identify keys)
        gen (css (sp/transform [sp/ALL keyword?] #(str "." (identify %)) style))]
    (into {} (map #(vector %1 [%2 %3]) keys identities (repeat gen)))))

(defn ->css [suffix style]
  (apply merge (mapv mappify (repeat suffix) style)))

