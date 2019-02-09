(ns rom-query.best-lv
  (:require [rom-query.crawler :as crawler]
            [clojure.pprint :refer [pprint]]))

(def properties #{"Poison" "Shadow" "Wind" "Undead" "Fire" "Water" "Earth" "Holy" "Ghost" "Neutral"})

(def damage [{:property "Fire"
              :damage   1000
              :delay    0.9}
             {:property "Water"
              :damage   1000
              :delay    0.9}])

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

(def default-options {:min-hp 1000})

(defn best [monsters char-level & {:as options}]
  (let [{:keys [min-hp]} (merge default-options options)]
    (->> monsters
         (filter #(> (:hp %) min-hp))
         (map #(assoc-xp-by-hp char-level %))
         (sort-by :xp-by-hp)
         reverse)))
