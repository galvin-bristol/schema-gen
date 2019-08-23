(ns schema-gen.core
  (:require [clojure.data.json :as json]))

(defn read-file
  [path]
  (json/read-str (slurp path)
                 :key-fn keyword))

(defn read-flow
  [flow]
  (read-file (str "resources/flows/" flow ".json")))

(defn read-group
  [group]
  (read-file  (str "resources/groups/" group ".json")))

(defn populate
  [group]
  {:group (read-group (:group group))
   :children (populate (:children group))})

(defn generate
  [flowName]
  (let [flow (read-flow flowName)
        footer (read-group (-> flow :footer :group))
        header (read-group (-> flow :header :group))
        groups (-> flow :hierarchy)]
    (clojure.pprint/pprint (map populate groups))
    (println footer)
    (println header)
    (println groups)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println (read-flow "ASP")))