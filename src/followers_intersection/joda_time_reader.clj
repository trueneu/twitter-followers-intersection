;; kudos to https://gist.github.com/ragnard/4738185

(ns followers-intersection.joda-time-reader
  (:require [clojure.instant :as i]
            [clojure.data.json :as json]
            [clj-time.coerce :as coerce])
  (:import org.joda.time.DateTime
           java.io.StringWriter))


(extend org.joda.time.DateTime json/JSONWriter
  {:-write (fn [in ^StringWriter out _] (.write out (str "\"" (coerce/to-string in) "\"")))})

(defn construct-date-time [years months days hours minutes seconds nanoseconds
                           offset-sign offset-hours offset-minutes]
  (DateTime. (.getTimeInMillis (#'i/construct-calendar years months days
                                 hours minutes seconds 0
                                 offset-sign offset-hours offset-minutes))))

(def read-instant-date-time
  "To read an instant as an org.joda.time.DateTime, bind *data-readers* to a
map with this var as the value for the 'inst key."
  (partial i/parse-timestamp (i/validated construct-date-time)))

