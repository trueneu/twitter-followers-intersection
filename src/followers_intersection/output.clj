(ns followers-intersection.output
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]
            [clojure.data.json :as json]
            [clj-yaml.core :as yaml]))

(def output-vals-fn
  (juxt :username
        :id
        #(get-in % [:public_metrics :followers_count])
        :created_at))

(def output-kv-fn
  #(hash-map
     :username (:username %)
     :id (:id %)
     :followers_count (get-in % [:public_metrics :followers_count])
     :created_at (:created_at %)))

(defmulti output :format)

(defmethod output :csv [data]
  (let [users (-> data :users)]
    (with-out-str
      (with-open [writer (io/writer *out*)]
        (csv/write-csv writer (map output-vals-fn
                                   users))))))

(defmethod output :json [data]
  (let [users (-> data :users)]
    (json/write-str (map output-kv-fn users))))

(defmethod output :yaml [data]
  (let [users (-> data :users)]
    (yaml/generate-string (map output-kv-fn users)
                          :dumper-options {:flow-style :block})))

