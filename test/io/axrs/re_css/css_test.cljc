(ns io.axrs.re-css.css-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [io.axrs.re-css.css :refer [css]]))

(def button {:button {:background-color "red"}})
(def button-hash (hash (:button button)))
(def other {:other {:padding 0}})
(def other-hash (hash (:other other)))
(def multi {:multi {:position "absolute"
                    :padding  0}})
(def multi-hash (hash (:multi multi)))

(def nested (-> button
                (assoc-in [:button :&/.loading] {:color "white"})
                (assoc-in [:button :span] {:padding "10px"})
                (assoc-in [:button :.normal] {:font-size "12px"})
                (assoc-in [:button :>/.action] {:display "none"})
                (assoc-in [:button :>/div] {:margin "10px"})))
(def nested-hash (hash (:button nested)))

(def button-css (str ".button-" button-hash "{background-color: \"red\";}"))
(def other-css (str ".other-" other-hash "{padding: 0;}"))
(def multi-css (str ".multi-" multi-hash "{position: \"absolute\";padding: 0;}"))
(def nested-button-name (str ".button-" nested-hash))
(def nested-css (str nested-button-name "{background-color: \"red\";}" \newline
                     nested-button-name ".loading{color: \"white\";}" \newline
                     nested-button-name " > .action{display: \"none\";}" \newline
                     nested-button-name " > div{margin: \"10px\";}" \newline
                     nested-button-name " span{padding: \"10px\";}" \newline
                     nested-button-name " .normal{font-size: \"12px\";}"))

(deftest css-test
  (testing "simple css classes"
    (is (= button-css (css button)))
    (is (= other-css (css other))))

  (testing "multiple attributes"
    (is (= multi-css (css multi))))

  (testing "multiple classes"
    (is (= (str button-css \newline other-css \newline multi-css)
           (css (merge button other multi)))))

  (testing "nested classes"
    (is (= nested-css (css nested)))))
