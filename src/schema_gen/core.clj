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
  (assoc (read-group (:group group))
         :childrenGroupsSchemas
         (map populate (:children group))))

(defn generate
  [flowName]
  (let [flow (read-flow flowName)
        footer (read-group (-> flow :footer :group))
        header (read-group (-> flow :header :group))
        groups (map populate (-> flow :hierarchy))]
    (clojure.pprint/pprint {:flowName flowName
                            :headerSchema header
                            :footerSchema footer
                            :rootGroupSchemas groups}
                           (clojure.java.io/writer (str "resources/generated/" flowName ".edn")))))

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