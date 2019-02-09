(ns rom-query.core
  (:require [rom-query.crawler :as crawler]
            [rom-query.db :as db]
            [rom-query.best-lv :as blv]))

(defn update-monsters-file []
  (-> crawler/all-monsters
      db/store-monsters!))

(def monsters (db/fetch-monsters!))

(blv/best monsters 85)

(blv/fast monsters 85)

