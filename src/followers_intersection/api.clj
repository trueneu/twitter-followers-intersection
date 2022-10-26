(ns followers-intersection.api
  (:require [clj-http.client :as http]
            [clojure.data.json :as json])

  (:import (org.apache.http.impl.cookie RFC6265CookieSpecProvider RFC6265CookieSpecProvider$CompatibilityLevel)
           (org.apache.http.conn.util PublicSuffixMatcherLoader)))

(def url-base "https://api.twitter.com")
(def url-id-by-username "/2/users/by/username/")
(def url-followers-1 "/2/users/")
(def url-followers-2 "/followers")

(def ^:dynamic bearer-token)

(defn auth-headers [token]
  {"Authorization" (str "Bearer " token)})

(defn println-err [& args]
  (binding [*out* *err*]
    (apply println args)))


(defn cookie-spec [http-context]
  (.create
    (RFC6265CookieSpecProvider.
      RFC6265CookieSpecProvider$CompatibilityLevel/RELAXED
      (PublicSuffixMatcherLoader/getDefault))
    http-context))

(defn followers! [user-id]
  (println-err "Fetching followers for" user-id)
  (loop [res        []
         next-token nil]
    (let [next-param (if next-token {"pagination_token" next-token} {})
          query-res  (->
                      (http/get (str url-base url-followers-1 user-id url-followers-2)
                                {:accept       :json
                                 :query-params (merge {"user.fields" "id,username,public_metrics,created_at" "max_results" "1000"} next-param)
                                 :headers      (auth-headers bearer-token)
                                 :cookie-spec  cookie-spec
                                 :throw-exceptions false}))

          status     (:status query-res)]
      (condp = status
        200
        (let [body  (-> (:body query-res) (json/read-str :key-fn keyword))
              token (get-in body [:meta :next_token])
              data  (:data body)]
          (if token (recur (concat res data)
                           token)
                    (concat res data)))
        429
        (do
          (println-err "Too many requests. Throttling...")
          (Thread/sleep ^long (* 1000 60))
          (recur res next-token))
        (throw (Exception. (str "Unexpected status" status ", bailing")))))))


(defn id-by-username! [username]
  (->
    (http/get (str url-base url-id-by-username username)
              {:accept      :json
               :headers     (auth-headers bearer-token)
               :cookie-spec cookie-spec})
    :body
    (json/read-str :key-fn keyword)
    :data
    :id))

