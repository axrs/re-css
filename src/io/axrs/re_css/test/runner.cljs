(ns io.axrs.re-css.test.runner
  (:require
   [cljs.test :as ct]
   [pjstadig.humane-test-output]
   [shadow.dom :as dom]
   [shadow.test :as st]))

(enable-console-print!)

(defonce log-node (dom/by-id "log"))

(defn log [evt]
  (let [msg (.-message evt)
        file (.-filename evt)
        line (.-lineno evt)]
    (dom/append log-node (str "ERROR: " msg "(" file ":" line ")\n"))))

(when log-node
  (set-print-fn! (fn [s] (dom/append log-node (str s "\n"))))
  (js/window.addEventListener "error" log))

(defmethod ct/report [::ct/default :begin-test-ns] [_])

(defn start [] (st/run-all-tests))

(defn stop [done] (set! (.-innerText log-node) "") (done))

(defn ^:export init [] (start))

