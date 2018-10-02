(ns io.axrs.re-css.css
  (:require
   [garden.core :refer [css]]
   [com.rpl.specter :as sp]
   [clojure.walk :refer [postwalk]]))

(defn- identify [suffix key]
  (if (keyword? key)
    (str (name key) "-" suffix)
    key))

(defn- mappify
  "Extracts the first level keys and assocs the top level classes into a map of structure
  {:lookup-value [\"generated-class-name\" [\".generated-class-name\" {:class 'attributes}]]}"
  [suffix style]
  (let [keys (sp/select [sp/ALL (complement coll?)] style)
        identify (partial identify suffix)
        identities (map identify keys)
        styled (sp/transform [sp/ALL keyword?] #(str "." (identify %)) style)]
    (into {} (map #(vector %1 [%2 %3]) keys identities (repeat styled)))))

(defn ->css [suffix style]
  (apply merge (mapv mappify (repeat suffix) style)))

