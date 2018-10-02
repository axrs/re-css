(ns io.axrs.re-css.css-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [io.axrs.re-css.css :as css]
   [clojure.string :as string]))

(defn css-str [& lines]
  (string/join \newline lines))

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
                                 [:span {:display 'none}]]]))))))
