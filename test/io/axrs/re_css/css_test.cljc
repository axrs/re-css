(ns io.axrs.re-css.css-test
  (:require
   [clojure.test :refer [deftest testing is]]
   [io.axrs.re-css.css :as css]
   [clojure.string :as string]))

(defn css-str [& lines]
  (string/join \newline lines))

(deftest ->css-test
  (testing "includes global styles"
    (is (= {"body" ["body" (css-str
                            "body {"
                            "  padding: 0;"
                            "}")]
            :form  ["form-test" (css-str
                                 ".form-test {"
                                 "  font-weight: bold;"
                                 "}")]}
           (css/->css "test" [["body" {:padding 0}]
                              [:form {:font-weight "bold"}]])))))
