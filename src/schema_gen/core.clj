(ns schema-gen.core
  (:require [clojure.data.json :as json
             clojure.pprint :as pp
             clojure.java.io :as io]))

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
  (let [children (:children group)]
    (assoc (read-group (:group group))
           :childrenGroupsSchemas
           (map populate children))))

(defn generate
  [flowName]
  (let [flow (read-flow flowName)]
    (pp/pprint
     {:flowName flowName
      :headerSchema (populate (-> flow :header))
      :footerSchema (populate (-> flow :footer))
      :rootGroupSchemas (map populate (-> flow :hierarchy))}
     (io/writer (str "resources/generated/" flowName ".edn")))))

(defn -main
  [& args]
  (generate "ASP"))

(comment
  {:group (read-group (:group group))
   :childrenGroupsSchemas (map populate (:children group))}

  (clojure.pprint/pprint
   (spit (str "resources/generated/" flowName ".edn")
         {:flowName flowName
          :headerSchema header
          :footerSchema footer
          :rootGroupSchemas groups})))