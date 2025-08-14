(ns backend.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.json :as json]
            [backend.core :as core]
            [clojure.string])
  (:import [java.net URLDecoder]))

(defn add-cors-headers [response]
  (assoc response
         :headers (merge (:headers response {})
                         {"Access-Control-Allow-Origin" "*"
                          "Access-Control-Allow-Methods" "GET, POST, OPTIONS"
                          "Access-Control-Allow-Headers" "Content-Type"})))

(defn safe-call [url]
  (try
    (core/call url)
    (catch Exception e
      (println "Failed to fetch:" url "Error:" (.getMessage e))
      nil)))

(defn safe-extract-timestamp [doc]
  (try
    (when doc
      (let [meta-elements ((core/select  "time[datetime]") doc)]
        (when (seq meta-elements)
          ((core/attribute "datetime") (first meta-elements)))))
    (catch Exception e
      (println "Failed to extract timestamp:" (.getMessage e))
      nil)))

(defn search-timestamps [query]
  (try
    (->> (core/fetch-links (core/search-url query))
         (map safe-call)
         (filter some?)
         (map safe-extract-timestamp)
         (filter some?)
         (vec))
    (catch Exception e
      (println "Error in search-timestamps:" (.getMessage e))
      [])))

(defn handler [request]
  (let [uri (:uri request)]
    (cond
      (= uri "/search")
      (let [query-string (:query-string request)
            query (when query-string
                    (-> query-string
                        (URLDecoder/decode "UTF-8")
                        (clojure.string/replace #"^q=" "")))]
        (add-cors-headers
         {:status 200
          :headers {"Content-Type" "application/json"}
          :body (search-timestamps (or query ""))}))

      :else
      (add-cors-headers
       {:status 404
        :headers {"Content-Type" "text/plain"}
        :body "Not found"}))))

(defn -main []
  (println "Starting server on port 3000...")
  (jetty/run-jetty (json/wrap-json-response handler)
                   {:port 3000 :join? false})
  (println "Server running at http://localhost:3000"))