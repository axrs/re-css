(ns io.axrs.re-css.core
  #?(:clj
     (:require [io.axrs.re-css.css :as css]))
  #?(:cljs
     (:require-macros [io.axrs.re-css.core]))
  #?(:cljs
     (:require
      [io.axrs.re-css.css]
      [io.axrs.re-css.dom :as dom]
      [reagent.core])))

#?(:cljs (def classes dom/classes))
#?(:cljs (def supports? dom/supports?))

(defmacro defui
  ([name style render-fn]
   `(let [suffix# (gensym "")
          s# (io.axrs.re-css.css/->css suffix# ~style)
          ~'classes (partial io.axrs.re-css.dom/classes s#)
          ~'form3? (map? ~render-fn)
          ~'render (if ~'form3? (:reagent-render ~render-fn) ~render-fn)]
      (def ~name
        (reagent.core/create-class
         (assoc (when ~'form3? ~render-fn)
                :component-will-unmount
                (fn [this#]
                  (io.axrs.re-css.dom/detach s#)
                  (some-> ~render-fn :component-will-unmount (apply [this#])))

                :component-will-mount
                (fn [this#]
                  (io.axrs.re-css.dom/attach s#)
                  (some-> ~render-fn :component-will-mount (apply [this#])))

                :reagent-render
                (fn [& ~'args]
                  (apply ~'render (update-in (vec ~'args) [0] assoc ::classes ~'classes))))))))

  ([name style args render-fn]
   (let [form (cond
                (vector? render-fn) :form-1
                (list? render-fn) :form-2)]
     `(defn ~name ~args
        (let [suffix# (gensym "")
              s# (io.axrs.re-css.css/->css suffix# ~style)
              ~'classes (partial io.axrs.re-css.dom/classes s#)]
          (reagent.core/create-class
           {:component-will-unmount
            (fn [this#]
              (io.axrs.re-css.dom/detach s#))

            :component-will-mount
            (fn [this#]
              (io.axrs.re-css.dom/attach s#))

            :reagent-render
            (case ~form
              :form-1 (fn ~args ~render-fn)
              :form-2 ~render-fn)}))))))
