(ns app.core)

(println "Hello from ClojureScript!")


(defn create-query-form []
  "<div>
   <h1>Autoreload Repl Search Results Sorted By Date Timestamp Query App</h1>
   <input type='text' id='query-input' placeholder='Enter search query'>
   <button onclick='app.core.handle_search()'>Search date sorted</button>
   <div id='results'></div>
   </div>")

(defn parse-timestamp [timestamp-str]
  (js/Date. timestamp-str))

(defn create-plot [timestamps]
  (let [indices (range 1 (inc (count timestamps)))
        dates (map parse-timestamp timestamps)
        trace (clj->js {:x indices
                        :y dates
                        :type "scatter"
                        :mode "lines+markers"
                        :name "Search Results"
                        :line {:color "blue"}
                        :marker {:size 8}})
        layout (clj->js {:title "Search Results: Time vs Index"
                         :xaxis {:title "Result Index"}
                         :yaxis {:title "Timestamp"}
                         :width 800
                         :height 500
                         :showlegend true
                         :margin {:l 80 :r 40 :t 60 :b 80}})]
    (js/Plotly.newPlot "chart" #js [trace] layout)))

(defn display-results [timestamps]
  (let [results-div (.getElementById js/document "results")]
    (if (empty? timestamps)
      (set! (.-innerHTML results-div) "<p>No timestamps found</p>")
      (do
        (set! (.-innerHTML results-div)
              (str "<h3>Found " (count timestamps) " timestamps:</h3>"
                   "<div id='chart' style='width: 100%; height: 500px; margin: 20px 0;'></div>"
                   "<h4>Timestamps list:</h4><ul>"
                   (apply str (map-indexed #(str "<li>" (inc %1) ". " %2 "</li>") timestamps))
                   "</ul>"))
        (js/setTimeout #(create-plot timestamps) 100)))))

(defn fetch-timestamps [query callback]
  (let [url (str "http://localhost:3000/search?q=" (js/encodeURIComponent query))]
    (-> (js/fetch url)
        (.then #(.json %))
        (.then callback)
        (.catch #(js/console.error "Error fetching time stamps: " %)))))

(defn handle-search []
  (let [query (.-value (.getElementById js/document "query-input"))
        results-div (.getElementById js/document "results")]
    (if (empty? query)
      (set! (.-innerHTML results-div) "<p>Please enter a search query</p>")
      (do (set! (.-innerHTML results-div) "<p>Loading timestamps...</p>")
          (fetch-timestamps query display-results)))))

(defn init []
  (let [app-element (.getElementById js/document "app")]
    (set! (.-innerHTML app-element) (create-query-form))))

(set! (.-handle_search js/app.core) handle-search)

(init)