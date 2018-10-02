(ns io.axrs.re-css.dom-test
  (:require
   [clojure.string :as string]
   [clojure.test :refer [deftest testing is use-fixtures]]
   [io.axrs.re-css.dom :as dom]))

(defn css-str [& lines]
  (string/join \newline lines))

(def form [:form ["form-test" [".form-test" {:font-weight 'bold}
                               [:span {:display 'none}]]]])
(def form-css-str (css-str
                   ".form-test {"
                   "  font-weight: bold;"
                   "}"
                   ""
                   ".form-test span {"
                   "  display: none;"
                   "}"))

(def fn-style [:fn ["fn-test" [".fn-test" (fn [] {:background-color 'red})
                               [:span (fn [] {:color 'white})]]]])
(def fn-css-str (css-str
                 ".fn-test {"
                 "  background-color: red;"
                 "}"
                 ""
                 ".fn-test span {"
                 "  color: white;"
                 "}"))

(def append-captor (atom []))
(def css-captor (atom []))
(def remove-captor (atom false))
(defonce script #js {"remove"      #(reset! remove-captor true)
                     "appendChild" #(swap! css-captor conj %)})
(defonce document #js {"createTextNode" identity
                       "createElement"  (constantly script)})

(defn reset-attached [f]
  (reset! append-captor [])
  (reset! css-captor [])
  (reset! remove-captor false)
  (is (= {} @dom/attached) "ARC atom is not empty")
  (with-redefs [dom/document (atom document)
                dom/document-head (atom #js {"appendChild" #(swap! append-captor conj %)})]
    (f))
  (reset! dom/attached {}))

(use-fixtures :each reset-attached)

(deftest detach-style-test
  (testing "does nothing if the style is not loaded"
    (dom/detach-style form)
    (is (empty? @dom/attached)))

  (testing "decrements the style count when already loaded in the dom"
    (is (empty? @dom/attached))
    (dom/attach-style form)
    (dom/attach-style form)
    (is (= 2 (get-in @dom/attached ["form-test" 1])))
    (dom/detach-style form)
    (is (= 1 (get-in @dom/attached ["form-test" 1])))
    (is (= 1 (count @append-captor)))
    (is (= [form-css-str] @css-captor))

    (testing "detaches the style if no more elements require the style"
      (is (= 1 (get-in @dom/attached ["form-test" 1])))
      (dom/detach-style form)
      (is (= {} @dom/attached))
      (is (true? @remove-captor)))))

(deftest attach-style-test
  (testing "attaches the style to the head of the document if not defined"
    (dom/attach-style form)
    (is (= 1 (get-in @dom/attached ["form-test" 1])))
    (is (= 1 (count @append-captor)))
    (is (= [form-css-str] @css-captor)))

  (testing "increments the count if the style is already attached"
    (dom/attach-style form)
    (is (= 2 (get-in @dom/attached ["form-test" 1])))
    (is (= 1 (count @append-captor)))
    (is (= [form-css-str] @css-captor)))

  (testing "evaluates functions in style before attach"
    (dom/attach-style fn-style)
    (is (= 1 (get-in @dom/attached ["fn-test" 1])))
    (is (= [form-css-str fn-css-str] @css-captor))))
