(ns io.axrs.re-css.css-test
  (:require
   [clojure.string :as string]
   [clojure.test :refer [deftest testing is]]
   [io.axrs.re-css.css :as css]))

(def suffix (rand-int 100))

(def styles (-> (css/class :button {:display          "block"
                                    :background-color "red"})))

(def nested (-> styles
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

(def hero-class (str ".hero-" suffix))
(def button-class (str ".button-" suffix))

(def button-css (str button-class "{display: block;background-color: red;}"))
(def nested-button-css #{(str button-class "{background-color: red;display: block;}")
                         (str button-class "::before{position: absolute;}")
                         (str button-class ".loading{color: white;}")
                         (str button-class " ~ label{line-height: 20px;}")
                         (str button-class " + button{background: none;}")
                         (str button-class " > div{margin: 10px;}")
                         (str button-class " > .action{display: none;}")
                         (str button-class " .normal{font-size: 12px;}")
                         (str button-class " span{padding: 10px;}")})

(def hero-css #{(str hero-class "{width: 100%;}")
                (str hero-class ".fade{opacity: 0.5;}")})

(def expected-button {:button [button-class button-css]})

(defn assert-css [style class class-hash class-css]
  (let [actual (css/css suffix style)
        [hash css-str] (get actual class)]
    (testing "css hash and classes"
      (is (= (apply str (rest class-hash)) hash))
      (is (= class-css (set (string/split css-str #"\n")))))))

(deftest css-test
  (testing "simple css attributes"
    (is (= {:button [(str "button-" suffix) button-css]}
           (css/css suffix styles))))

  (testing "nested classes"
    (testing "button"
      (assert-css nested :button button-class nested-button-css)
      (assert-css nested :hero hero-class hero-css))))

(clojure.test/run-tests)
