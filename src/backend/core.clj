(ns backend.core
  (:import [org.jsoup Jsoup]
           [java.io FileWriter]))

(def last-modified "last-modified")

(defn search-url [query]
  (format "https://www.swr.de/swr-suche-100.html?swx_q=%s&swx_sort=date" query))

(defn save-to-file [filename content]
  (with-open [w (FileWriter. filename)]
    (.write w content)))

(defn call [url]
  (.get (Jsoup/connect url)))

(defn select [selector]
  #(.select %1 selector))

(defn attribute [attr]
  #(.attr %1 attr))

(defn links [elements]
  (map (attribute "href") elements))

(defn fetch-links [url]
  (take 10 (links ((select "h2.hgroup a") (call url)))))

(defn is-sorted? [coll]
  (= (reverse coll) (sort compare coll)))

(defn workflow []
  (->> (fetch-links (search-url "balingen"))
       (map call)
       (map (select (format "meta[name=\"%s\"]" last-modified)))
       (map (attribute "content"))))

(->> (fetch-links (search-url "balingen"))
     (map call)
     (map (select (format "meta[name=\"%s\"]" "date")))
     (map (attribute "content"))
     (is-sorted?))

(defn -main []
  (println (workflow)))

(.attr (.select
        (.get (Jsoup/connect "https://www.swr.de/swraktuell/baden-wuerttemberg/stars-konzerte-festivals-2025-100.html"))
        "time[datetime]") "datetime")

(.text (.select (.get (Jsoup/connect "https://www.swr.de/swraktuell/baden-wuerttemberg/stars-konzerte-festivals-2025-100.html"))
                "dt[class=\"meta-updated\"]"))