# Timestamp Visualization App

A full-stack Clojure/ClojureScript application that scrapes search results from SWR.de and visualizes the timestamps to prove that results are sorted by date.

## Features

- **ClojureScript Frontend**: Interactive web interface for entering search queries
- **Clojure Backend**: Web scraper that extracts timestamps from search results
- **Data Visualization**: Interactive chart showing timestamp vs result index
- **Real-time Validation**: Proves whether search results are properly sorted by date

## Architecture

- **Frontend**: ClojureScript with Plotly.js for charting
- **Backend**: Clojure with Ring, Jetty, and Jsoup for web scraping
- **Communication**: HTTP API between frontend and backend

## Running the Application

### Prerequisites

- Java 11+
- Clojure CLI tools - [Installation Instructions](https://clojure.org/guides/install_clojure)

### Start Backend Server

```bash
clj -M:backend
```

Server runs on http://localhost:3000

### Start Frontend Development Server

```bash
clj -M:frontend
```

Frontend development server runs on http://localhost:9000

### Usage

1. Start both servers (backend first, then frontend)
2. Open browser and go to http://localhost:9000
3. Enter a search query (e.g., "stuttgart", "balingen", "oktober")
4. Click "Search" button
5. View the interactive chart showing timestamp vs result index
6. Analyze the trend: A downward trending line proves results are sorted by date (newest first)
7. Review the data: The list below the chart shows the actual timestamps extracted

### Project Structure

```bash
src/
├── app/
│   └── core.cljs          # ClojureScript frontend
└── backend/
    ├── core.clj           # Web scraping logic
    └── server.clj         # HTTP API server
```

### Technologies Used

- Clojure & ClojureScript
- Ring (HTTP server)
- Jsoup (HTML parsing)
- Plotly.js (Data visualization)
- Jetty (Web server)
