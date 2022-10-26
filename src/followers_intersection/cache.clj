(ns followers-intersection.cache
  (:require [followers-intersection.joda-time-reader :as j]
            [clojure.java.io :as io]
            [clj-time.core :as t]
            [clojure.data.json :as json]
            [babashka.fs :as fs]))

(def ^:dynamic cache-filename (.toString
                                (fs/path
                                  (io/file (System/getProperty "user.home")
                                           ".flwrs-cache"))))

(defonce cache-atom (atom {}))

(def age-margin (t/days 7))

(defn load-cache! []
  (if-not (.exists (io/file cache-filename))
    (spit cache-filename {}))

  (reset! cache-atom
          (json/read-str
            (slurp cache-filename)
            :key-fn keyword
            :value-fn (fn [k v] (if (= k :last-updated)
                                  (j/read-instant-date-time v)
                                  v)))))

(defn save-cache! []
  (spit cache-filename
        (json/write-str @cache-atom)))

(defn cached-followers? [userid]
  (and
    ((complement nil?)
     (get-in @cache-atom [:followers userid :data]))
    (t/after?
      (get-in @cache-atom [:followers userid :last-updated])
      (t/minus (t/now) age-margin))))

(defn followers [userid]
  (get-in @cache-atom [:followers userid :data]))

(defn user-id [username]
  (get-in @cache-atom [:ids username]))

(defn cached-id? [username]
  ((complement nil?)
   (get-in @cache-atom [:ids username])))

(defn cache-followers! [userid followers]
  (swap! cache-atom assoc-in [:followers userid :data] followers)
  (swap! cache-atom assoc-in [:followers userid :last-updated] (t/now)))

(defn cache-id! [username userid]
  (swap! cache-atom assoc-in [:ids username] userid))



