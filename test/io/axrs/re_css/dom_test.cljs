(ns io.axrs.re-css.dom-test
  (:require
   [clojure.test :refer [deftest testing is use-fixtures]]
   [io.axrs.re-css.dom :as dom]))

(defonce css-str ".button{background-color: red;}")
(defonce button [:button ["button-test" css-str]])
(defonce append-captor (atom []))
(defonce css-captor (atom []))
(defonce remove-captor (atom false))
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
    (dom/detach-style button)
    (is (empty? @dom/attached)))

  (testing "decrements the style count when already loaded in the dom"
    (is (empty? @dom/attached))
    (dom/attach-style button)
    (dom/attach-style button)
    (is (= 2 (get-in @dom/attached ["button-test" 1])))
    (dom/detach-style button)
    (is (= 1 (get-in @dom/attached ["button-test" 1])))
    (is (= 1 (count @append-captor)))
    (is (= [css-str] @css-captor))

    (testing "detaches the style if no more elements require the style"
      (is (= 1 (get-in @dom/attached ["button-test" 1])))
      (dom/detach-style button)
      (is (= {} @dom/attached))
      (is (true? @remove-captor)))))

(deftest attach-style-test
  (testing "attaches the style to the head of the document if not defined"
    (dom/attach-style button)
    (is (= 1 (get-in @dom/attached ["button-test" 1])))
    (is (= 1 (count @append-captor)))
    (is (= [css-str] @css-captor))

    (testing "increments the count if the style is already attached"
      (dom/attach-style button)
      (is (= 2 (get-in @dom/attached ["button-test" 1])))
      (is (= 1 (count @append-captor)))
      (is (= [css-str] @css-captor)))))
