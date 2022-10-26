(ns followers-intersection.logic
  (:require [followers-intersection.cache :as cache]
            [followers-intersection.api :as api]
            [clojure.set :as s]))

(defn intersection [vecs]
  (let [sets (map set vecs)]
    (apply s/intersection sets)))

(defn load-followers [userid]
  (let [kw-userid (keyword userid)]
    (if (cache/cached-followers? kw-userid)
      (cache/followers kw-userid)
      (when-let [followers (api/followers! userid)]
        (cache/cache-followers! kw-userid followers)
        (cache/save-cache!)
        followers))))

(defn load-userid [username]
  (let [kw-username (keyword username)]
    (if (cache/cached-id? kw-username)
      (cache/user-id kw-username)
      (when-let [user-id (api/id-by-username! username)]
        (cache/cache-id! kw-username user-id)
        (cache/save-cache!)
        user-id))))

(defn common-followers [usernames]
  (let [user-ids  (doall (map load-userid usernames))
        followers (doall (map load-followers user-ids))]
    (intersection followers)))


