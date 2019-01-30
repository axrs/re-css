(ns io.axrs.re-css.css-test
  #?(:clj
     (:require
      [garden.def :refer [defkeyframes]]))
  #?(:cljs
     (:require-macros [garden.def :refer [defkeyframes]]))
  (:require
   [clojure.test :refer [deftest testing is]]
   [io.axrs.re-css.css :as css]
   [clojure.string :as string]))

(defn css-str [& lines]
  (string/join \newline lines))

(defkeyframes test-animation
  [:from {:opacity 0}]
  [:to {:opacity 1}])

(deftest ->css-test
  (testing "generates unique classes for top level :keywords"

    (testing "leaving string identifiers unchanged"
      (is (= {"body" ["body" ["body" {:padding 0}]]
              :form  ["form-test" [".form-test" {:font-weight "bold"}]]}
             (css/->css "test" [["body" {:padding 0}]
                                [:form {:font-weight "bold"}]]))))

    (testing "leaving nested/classes unchanged"
      (is (= {:button ["button-test" [".button-test" {:padding 0}
                                      [:span {:display 'none}]]]}
             (css/->css "test" [[:button {:padding 0}
                                 [:span {:display 'none}]]]))))

    (testing "supports keyframe animations"

      (is (= {"keyframes-test-animation" ["keyframes-test-animation" [test-animation]]
              "body"                     ["body" ["body" {:animation [["test-animation" "2s" :infinite :alternate]]}]]
              :form                      ["form-test" [".form-test" {:animation [["test-animation" "2s" :infinite :alternate]]}]]}
             (css/->css "test" [test-animation
                                ["body" {:animation [["test-animation" "2s" :infinite :alternate]]}]
                                [:form {:animation [["test-animation" "2s" :infinite :alternate]]}]]))))))
