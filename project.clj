(defproject syncserver "0.1.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.codehaus.groovy/groovy-all "2.3.0"]
                 [com.google.guava/guava "14.0.1"]]
  :java-source-paths ["java"]
  :aot [syncserver.core]
  :main syncserver.core
  :repositories [["snapshots" {:url "http://cobra.cs.uni-duesseldorf.de/artifactory/libs-snapshot-local/"
                            :username "leiningen"
                               :password :env}]]
  :profiles {:dev {:dependencies [[midje "1.5.1"]]}}
  )
