(ns io.axrs.re-css.css-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [io.axrs.re-css.css :refer [css]]))

(def button {:button {:background-color "red"}})
(def button-hash (str ".button-" (hash (:button button))))
(def other {:other {:padding 0}})
(def other-hash (str ".other-" (hash (:other other))))
(def multi {:multi {:position "absolute"
                    :padding  0}})
(def multi-hash (str ".multi-" (hash (:multi multi))))

(def nested (-> button
                (assoc-in [:button :&/.loading] {:color "white"})
                (assoc-in [:button :span] {:padding "10px"})
                (assoc-in [:button :.normal] {:font-size "12px"})
                (assoc-in [:button :>/.action] {:display "none"})
                (assoc-in [:button :>/div] {:margin "10px"})))
(def nested-hash (str ".button-" (hash (:button nested))))

(def button-css (str button-hash "{background-color: \"red\";}"))
(def other-css (str other-hash "{padding: 0;}"))
(def multi-css (str multi-hash "{position: \"absolute\";padding: 0;}"))
(def nested-css (str nested-hash "{background-color: \"red\";}" \newline
                     nested-hash ".loading{color: \"white\";}" \newline
                     nested-hash " > .action{display: \"none\";}" \newline
                     nested-hash " > div{margin: \"10px\";}" \newline
                     nested-hash " span{padding: \"10px\";}" \newline
                     nested-hash " .normal{font-size: \"12px\";}"))

(def expected-button {:button [button-hash button-css]})
(def expected-other {:other [other-hash other-css]})
(def expected-multi {:multi [multi-hash multi-css]})

(deftest css-test
  (testing "simple css classes"
    (is (= {:button [button-hash button-css]}
           (css button)))
    (is (= {:other [other-hash other-css]}
           (css other))))

  (testing "multiple attributes"
    (is (= {:multi [multi-hash multi-css]}
           (css multi))))

  (testing "multiple classes"
    (is (= (merge expected-button expected-other expected-multi)
           (css (merge button other multi)))))

  (testing "nested classes"
    (is (= {:button [nested-hash nested-css]}
           (css nested)))))
