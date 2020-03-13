(ns schema-gen.core
  (:require [clojure.data.json :as json]
            [clojure.pprint :as pp]
            [clojure.java.io :as io]
            [schema-gen.split :as split]
            [cheshire.core :as chesh]))

(defn read-file
  [path]
  (json/read-str (slurp path)
                 :key-fn keyword))

(defn read-flow
  [flow]
  (read-file (str "resources/flows/" flow ".json")))

(defn read-group
  [code]
  (read-file  (str "resources/groups/" code ".json")))

(defn schema-file
  [flow]
  (str "resources/generated/" (:flowName flow) ".json"))

(defn populate
  [group]
  (assoc (read-group (:code group))
         :childrenGroupsSchemas
         (mapv populate (:children group))))

(defn generate
  [flowName]
  (let [flow (read-flow flowName)]
    (pp/pprint
     (chesh/generate-string [{:flowName         (:flowName flow)
                              :headerSchema     (populate (:header flow))
                              :footerSchema     (populate (:footer flow))
                              :rootGroupSchemas (mapv populate (:root flow))}]
                            {:pretty true})
     (io/writer (schema-file flow)))))

(defn schema-split
  [flowName]
  (split/split flowName))

(defn -main
  [& args]
  (generate "ASP"))