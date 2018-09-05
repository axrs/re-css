(ns io.axrs.re-css.css-test
  (:require
   [clojure.string :as string]
   [clojure.test :refer [deftest testing is]]
   [io.axrs.re-css.css :as css]))

(def button (-> (css/class :button {:display          "block"
                                    :background-color "red"})))
(def button-hash (css/hash-of button :button))

(def nested (-> button
                (css/class :hero {:width "100%"})
                (css/with :button
                          (css/& :.loading {:color "white"})
                          (css/nested :span {:padding "10px"})
                          (css/nested :.normal {:font-size "12px"})
                          (css/> :.action {:display "none"})
                          (css/> :div {:margin "10px"})
                          (css/+ :button {:background "none"})
                          (css/pseudo ::before {:position "absolute"})
                          (css/next :label {:line-height "20px"}))
                (css/with :hero
                          (css/& :.fade {:opacity 0.5}))))

(def nested-button-hash (css/hash-of nested :button))
(def nested-button-class (str "." nested-button-hash))
(def nested-hero-hash (css/hash-of nested :hero))
(def nested-hero-class (str "." nested-hero-hash))

(def button-css (str "." button-hash "{display: block;background-color: red;}"))
(def nested-button-css #{(str nested-button-class "{background-color: red;display: block;}")
                         (str nested-button-class "::before{position: absolute;}")
                         (str nested-button-class ".loading{color: white;}")
                         (str nested-button-class " ~ label{line-height: 20px;}")
                         (str nested-button-class " + button{background: none;}")
                         (str nested-button-class " > div{margin: 10px;}")
                         (str nested-button-class " > .action{display: none;}")
                         (str nested-button-class " .normal{font-size: 12px;}")
                         (str nested-button-class " span{padding: 10px;}")})

(def hero-css #{(str nested-hero-class "{width: 100%;}")
                (str nested-hero-class ".fade{opacity: 0.5;}")})

(def expected-button {:button [button-hash button-css]})

(defn assert-css [style class class-hash class-css]
  (let [actual (css/css style)
        [hash css-str] (get actual class)]
    (testing "css hash and classes"
      (is (= class-hash hash))
      (is (= class-css (set (string/split css-str #"\n")))))))

(deftest css-test
  (testing "simple css attributes"
    (is (= {:button [button-hash button-css]}
           (css/css button))))

  (testing "nested classes"
    (testing "button"
      (assert-css nested :button nested-button-hash nested-button-css)
      (assert-css nested :hero nested-hero-hash hero-css))))

(clojure.test/run-tests)
