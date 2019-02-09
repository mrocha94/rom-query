(ns rom-query.crawler
  (:require [clojure.java.io :as io]
            [net.cgrand.enlive-html :as html]
            [clj-http.client :as http]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]))

(def base-url "https://www.romwiki.net/monsters?page=")

(defn open-page [page]
  (-> base-url
      (str page)
      http/get
      :body))

(defn read-html [content]
  (-> content
      java.io.StringReader.
      html/html-resource))

(defn get-monster-list [content]
  (html/select content [:body :div.monster-list :div.monster-template]))

(def pag1 (open-page 1))
(def content (read-html pag1))
(def monster-list (get-monster-list content))

(defn get-name [monster]
  (-> monster
      (html/select [:div :div :div :a.m-name html/text])
      first))

(defn get-race [monster]
  (-> monster
      (html/select [:p.race-property :a html/text])
      first))

(defn get-property [monster]
  (-> monster
      (html/select [:p.race-property :a html/text])
      second))

(defn get-size [monster]
  (-> monster
      (html/select [:p.race-property :a html/text])
      last))

(defn to-int [string]
  (Integer. (str/replace string "," "")))

(defn get-lv [monster]
  (-> monster
      (html/select [:div.m-attributes :table (html/nth-child 1) :> [:td (html/nth-child 2)] html/text])
      first
      to-int))

(defn get-hp [monster]
  (-> monster
      (html/select [:div.m-attributes :table (html/nth-child 2) :> [:td (html/nth-child 2)] html/text])
      first
      to-int))

(defn get-base [monster]
  (-> monster
      (html/select [:div.m-attributes :table (html/nth-child 3) :> [:td (html/nth-child 2)] html/text])
      first
      to-int))

(defn get-job [monster]
  (-> monster
      (html/select [:div.m-attributes :table (html/nth-child 4) :> [:td (html/nth-child 2)] html/text])
      first
      to-int))

(defn get-info [monster]
  {:name (get-name monster)
   :race (get-race monster)
   :property (get-property monster)
   :size (get-size monster)
   :level (get-lv monster)
   :hp (get-hp monster)
   :base (get-base monster)
   :job (get-job monster)})

(mapv get-info monster-list)



(defn get-all-monsters [init end]
  (->> (range init end)
       (map open-page)
       (map read-html)
       (map get-monster-list)
       (reduce concat)
       (map get-info)))

(def all-monsters (get-all-monsters 1 22))
