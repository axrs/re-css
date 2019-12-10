(ns io.axrs.re-css.core
  #?(:cljs
     (:refer-clojure :exclude [apply-to]))
  #?(:clj
     (:require [io.axrs.re-css.css :as css]))
  #?(:cljs
     (:require-macros [io.axrs.re-css.core]))
  #?(:cljs
     (:require
      [io.axrs.re-css.css]
      [io.axrs.re-css.dom :as dom]
      [reagent.core :as r])))

#?(:cljs
   (do
     (def classes dom/classes)
     (def supports? dom/supports?)
     (def add-transformation dom/add-transformation)
     (def remove-transformation dom/remove-transformation)
     (def ^:private debug? ^boolean js/goog.DEBUG)
     (when debug?
       ;TODO create and use NilAtom
       (def css-updated (let [a (r/atom nil)]
                          (reify
                            IReset
                            (-reset! [o new-value]
                              (reset! a new-value)
                              nil)
                            IDeref
                            (-deref [o]
                              @a
                              nil))))
       (add-watch dom/attached :css-debug
                  (fn [& _] (reset! css-updated (js/Date.now)))))))

(defn- apply-to [render-fn k this]
  (some-> render-fn k (apply [this])))

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
                  (apply-to ~render-fn :component-will-unmount this#))

                :component-will-mount
                (fn [this#]
                  (io.axrs.re-css.dom/attach s#)
                  (apply-to ~render-fn :component-will-mount this#))

                :reagent-render
                (fn [& ~'args]
                  (when debug? @css-updated)
                  (apply ~'render (update-in (vec ~'args) [0] assoc ::classes ~'classes))))))))

  ([name style args render-fn]
   (let [form (cond
                (vector? render-fn) :form-1
                (or (fn? render-fn) (list? render-fn)) :form-2)]
     `(defn ~name ~args
        (let [suffix# (gensym "")
              s# (io.axrs.re-css.css/->css suffix# ~style)
              ~'classes (partial io.axrs.re-css.dom/classes s#)
              render# (case ~form
                        :form-1 (fn ~args ~render-fn)
                        :form-2 ~render-fn)]
          (reagent.core/create-class
           {:component-will-unmount
            (fn [this#]
              (io.axrs.re-css.dom/detach s#))

            :component-will-mount
            (fn [this#]
              (io.axrs.re-css.dom/attach s#))

            :reagent-render
            (fn [& ~'args]
              (when debug? @css-updated)
              (apply render# ~'args))}))))))
