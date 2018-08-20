(ns io.axrs.re-css.core-test
  (:require
    [clojure.test :refer [deftest testing is]]
    [io.axrs.re-css.core :as core]))

(deftest failing-test
  (testing "should fail"
    (is (false? true))))
