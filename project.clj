(defproject followers-intersection "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/data.json "2.4.0"]
                 [org.clojure/data.csv "1.0.1"]
                 [org.clojure/tools.cli "1.0.214"]
                 [clj-commons/clj-yaml "1.0.26"]
                 [clj-http "3.12.3"]
                 [clj-time "0.15.2"]

                 [babashka/fs "0.1.11"]]

  :main followers-intersection.core
  :repl-options {:init-ns followers-intersection.core})
