(ns io.axrs.re-css.core
  #?(:cljs (:require-macros io.axrs.re-css.core))
  #?(:cljs
     (:require
       [reagent.core]
       [io.axrs.re-css.jss])))

(defmacro defui [name style args render-fn]
  `(defn ~name ~args
     (let [[~'hash ~'sheet] (io.axrs.re-css.jss/attach ~style)
           ~'classes (.-classes ^js ~'sheet)
           ~'css #(hash-map :className (aget ~'classes %))]
       (reagent.core/create-class
         {:component-will-unmount (fn [this#]
                                    (io.axrs.re-css.jss/detach ~'hash))
          :reagent-render         (if (fn? ~render-fn)
                                    ~render-fn
                                    (fn ~args ~render-fn))}))))
