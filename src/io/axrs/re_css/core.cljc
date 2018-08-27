(ns io.axrs.re-css.core
  #?(:cljs
     (:require-macros [io.axrs.re-css.core]))
  #?(:cljs
     (:require
       [reagent.core]
       [io.axrs.re-css.jss])))

(defmacro defui [name style args render-fn]
  (let [form (cond
               (symbol? render-fn) :form-0
               (vector? render-fn) :form-1
               (list? render-fn) :form-2
               (map? render-fn) :form-3)
        props (when (= :form-3 form) render-fn)]
    `(defn ~name ~args
       (let [[~'hash ~'sheet] (io.axrs.re-css.jss/attach ~style)
             ~'classes (.-classes ^js ~'sheet)
             ~'css #(hash-map :className (aget ~'classes %))]
         (reagent.core/create-class
           (merge ~props
             {:component-will-unmount (case ~form
                                        :form-3
                                        (fn [this#]
                                          (io.axrs.re-css.jss/detach ~'hash)
                                          (some-> ~props :component-will-unmount (apply [this#])))

                                        (fn [this#]
                                          (io.axrs.re-css.jss/detach ~'hash)))
              :reagent-render         (case ~form
                                        :form-0
                                        (fn ~args
                                          (apply ~render-fn (update-in ~args [0] assoc :css ~'css)))

                                        :form-1
                                        (fn ~args
                                          ~render-fn)

                                        :form-2 ~render-fn

                                        :form-3
                                        (fn ~args
                                          (apply (:reagent-render ~props) (update-in ~args [0] assoc :css ~'css))))}))))))
