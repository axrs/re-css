(ns io.axrs.re-css.dom-test
  (:require-macros
   [garden.def :refer [defkeyframes]])
  (:require
   [clojure.string :as string]
   [clojure.test :refer [deftest testing is use-fixtures]]
   [io.axrs.re-css.dom :as dom]))

(defn- string-clean [s]
  (string/replace s #"[\s]+" \space))

(defn- css-str [& lines]
  (string-clean (string/join \space lines)))

(def form [:form ["form-test" [".form-test" {:font-weight 'bold}
                               [:span {:display 'none}]]]])
(def form-css-str (css-str
                   ".form-test {"
                   "  font-weight: bold;"
                   "}"
                   ".form-test span {"
                   "  display: none;"
                   "}"))

(def fn-style [:fn ["fn-test" [".fn-test" (fn [] {:background-color 'red})
                               [:span (fn [] {:color 'white})]]]])
(def fn-css-str (css-str
                 ".fn-test {"
                 "  background-color: red;"
                 "}"
                 ".fn-test span {"
                 "  color: white;"
                 "}"))

(def append-captor (atom []))
(def css-captor (atom []))
(def remove-captor (atom false))
(defonce script #js {"remove"      #(reset! remove-captor true)
                     "appendChild" #(swap! css-captor conj (string-clean %))})
(defonce document #js {"createTextNode" identity
                       "createElement"  (constantly script)})

(defn reset-attached [f]
  (reset! append-captor [])
  (reset! css-captor [])
  (reset! remove-captor false)
  (is (= {} @dom/attached) "ARC atom is not empty")
  (binding [dom/*document* document
            dom/document-head (constantly #js {"appendChild" #(swap! append-captor conj %)})]
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

(defkeyframes test-animation
  [:from {:opacity 0}]
  [:to {:opacity 1}])

(def animation-str (css-str
                    "@keyframes test-animation {"
                    "  from { "
                    "    opacity: 0;"
                    "  }"
                    "  to { "
                    "    opacity: 1;"
                    "  }"
                    "}"))

(deftest attach-style-animation-test

  (testing "allows attaching animations"
    (dom/attach-style ["keyframes-test-animation" ["keyframes-test-animation" test-animation]])
    (is (= 1 (get-in @dom/attached ["keyframes-test-animation" 1])))
    (is (= [animation-str] @css-captor))))

(deftest supports?-test

  (testing "false if css property is invalid"
    (is (false? (dom/supports? "thisDoesNotExist" "block"))))

  (testing "false if css value is invalid for property"
    (is (false? (dom/supports? "display" "thisDoesNotExist"))))

  (testing "true if value is supported"
    (is (true? (dom/supports? 'display "block")))
    (is (true? (dom/supports? :display "block")))
    (is (true? (dom/supports? "display" "block")))))
