(ns followers-intersection.core
  (:gen-class)
  (:require [followers-intersection.logic :as logic]
            [followers-intersection.output :as output]
            [followers-intersection.cache :as cache]
            [clojure.tools.cli :as cli]
            [babashka.fs :as fs]
            [clojure.java.io :as io]
            [followers-intersection.api :as api]))

(defn file-relative-to-home [filename]
  (.toString
    (fs/path
      (io/file (System/getProperty "user.home")
               filename))))
(def options
  [["-o" "--format FORMAT" "Output format: json|csv|yaml"
    :default :csv
    :parse-fn keyword
    :validate [#{:json :yaml :csv} #(str "Must be json, csv or yaml, read " %)]]
   ["-u" "--username USERNAME" "Usernames to lookup"
    :multi true
    :default []
    :update-fn conj]
   ["-c" "--cache FILENAME" "Path to cache file"
    :default (file-relative-to-home ".flwrs-cache")]
   ["-t" "--token FILENAME" "Path to Twitter API bearer token file. File should contain the token only, no newline at the end"
    :default (file-relative-to-home ".twitter-token")]
   ["-h" "--help"]])

(defn -main [& args]
  (let [opts (cli/parse-opts args options)
        {:keys [options summary errors]} opts]
    (if (:help options)
      (println summary)
      (if errors
        (println summary "\n" errors)
        (if (empty? (:username options))
          (println summary "\n" "At least 1 username should be specified")

          (let [token (slurp (:token options))]
            (binding [cache/cache-filename (:cache options)
                      api/bearer-token token]
              (cache/load-cache!)
              (let [followers (logic/common-followers (:username options))]
                (print
                  (output/output {:format (:format options)
                                  :users followers}))))))))))

