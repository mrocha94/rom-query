(ns rom-query.db
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defn- store! [file data]
  (->> data
       pr-str
       (spit (io/resource file))))

(def store-monsters! (partial store! "monsters.edn"))

(defn- fetch! [file]
  (->> file
       io/resource
       slurp
       read-string))

(def fetch-monsters! (partial fetch! "monsters.edn"))
