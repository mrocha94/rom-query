(ns rom-query.best-lv
  (:require [rom-query.crawler :as crawler]
            [clojure.pprint :refer [pprint]]))

(def monsters (crawler/get-all-monsters 1 22))

(def gap-penalty {-11  0.8
                  -21  0.5
                  10   1.0
                  15   0.8
                  20   0.6
                  25   0.4
                  30   0.2
                  100  0.1})

(defn level-penalty [char-level monster-level]
  (->> gap-penalty
       keys
       sort
       (drop-while #(> (- char-level monster-level) %))
       first
       (get gap-penalty)))

(defn assoc-xp-by-hp [char-level {:keys [hp base level] :as monster}]
  (assoc monster :xp-by-hp (/ (* (level-penalty char-level level) base) hp)))

((filter #(> (:hp %) 5000) monsters))

(def best (->> monsters
               (filter #(> (:hp %) 5000))
               (map #(assoc-xp-by-hp 83 %))
               (sort-by :xp-by-hp)
               reverse
               (take 20)))


