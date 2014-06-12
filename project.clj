(defproject syncserver "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :java-source-paths ["java"]
  :aot [syncserver.core]
  :repositories [["snapshots" {:url "http://cobra.cs.uni-duesseldorf.de/artifactory/"
                            :username :env/leiningen_username
                            :passphrase :env/leiningen_password}]]  
    )
