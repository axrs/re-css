(ns io.axrs.re-css.core
  #?(:cljs
     (:require-macros [io.axrs.re-css.core]))
  #?(:cljs
     (:require
      [reagent.core]
      [io.axrs.re-css.jss :as jss])))

#?(:cljs (def styled jss/styled))

(defmacro defui
  ([name style render-fn]
   `(let [~'hash (io.axrs.re-css.jss/load ~style)
          ~'styled (partial io.axrs.re-css.jss/styled ~'hash)
          ~'form3? (map? ~render-fn)
          ~'render (if ~'form3? (:reagent-render ~render-fn) ~render-fn)]
      (def ~name
        (reagent.core/create-class
         (assoc (when ~'form3? ~render-fn)
                :component-will-unmount
                (fn [this#]
                  (io.axrs.re-css.jss/detach ~'hash)
                  (some-> ~render-fn :component-will-unmount (apply [this#])))

                :component-will-mount
                (fn [this#]
                  (io.axrs.re-css.jss/attach ~'hash)
                  (some-> ~render-fn :component-will-mount (apply [this#])))

                :reagent-render
                (fn [& ~'args]
                  (apply ~'render (update-in (vec ~'args) [0] assoc ::styled ~'styled))))))))

  ([name style args render-fn]
   (let [form (cond
                (vector? render-fn) :form-1
                (list? render-fn) :form-2)]
     `(defn ~name ~args
        (let [~'hash (io.axrs.re-css.jss/load ~style)
              ~'styled (partial io.axrs.re-css.jss/styled ~'hash)]
          (reagent.core/create-class
           {:component-will-unmount
            (fn [this#] (io.axrs.re-css.jss/detach ~'hash))
            :component-will-mount
            (fn [this#]
              (io.axrs.re-css.jss/attach ~'hash))

            :reagent-render
            (case ~form
              :form-1 (fn ~args ~render-fn)
              :form-2 ~render-fn)}))))))
