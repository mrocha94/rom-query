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

(def pag1 (open-page 1))
(def content (read-html pag1))
(def monster-list (html/select content [:body :div.monster-list :div.monster-template]))

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

(defn get-lv [monster]
  (-> monster
      (html/select [:div.m-attributes :table (html/nth-child 1) :> [:td (html/nth-child 2)] html/text])
      first))

(defn get-hp [monster]
  (-> monster
      (html/select [:div.m-attributes :table (html/nth-child 2) :> [:td (html/nth-child 2)] html/text])
      first))

(defn get-base [monster]
  (-> monster
      (html/select [:div.m-attributes :table (html/nth-child 3) :> [:td (html/nth-child 2)] html/text])
      first))

(defn get-job [monster]
  (-> monster
      (html/select [:div.m-attributes :table (html/nth-child 4) :> [:td (html/nth-child 2)] html/text])
      first))

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

