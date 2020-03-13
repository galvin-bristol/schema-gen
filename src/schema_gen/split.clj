(ns schema-gen.split
  (:require [clojure.data.json :as json]
            [clojure.pprint :as pp]
            [clojure.java.io :as io]))

(defn read-file
  [path]
  (json/read-str (slurp path)
                 :key-fn keyword))

(defn read-schema
  [flow]
  (read-file (str "resources/schemas/" flow ".json")))

(defn split
  [flowName]
  (let [flow (first (read-schema flowName))
        header (:headerSchema flow)]
    (pp/pprint
     (json/write-str header)
     (io/writer (str "resources/schemas/generated/" (:groupCode header) "-" flowName ".json")))))