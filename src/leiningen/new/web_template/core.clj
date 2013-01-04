(ns {{ns-name}}.core
  (:gen-class)
  (:require [compojure.core :as cc]
            [hiccup.core :as hc]
            [clojure.tools.logging :as log])
  (:use [clojure.string :only [upper-case]]
        [clojure.pprint :only [pprint]]
        [ring.middleware
         [params :only [wrap-params]]
         [multipart-params :only [wrap-multipart-params]]
         [resource :only [wrap-resource]]
         [file-info :only [wrap-file-info]]]
        [ring.adapter.jetty :only (run-jetty)]
        [stencil.core :only [render-file]]
        [stencil.loader :only [set-cache]]
        [clojure.core.cache :only [ttl-cache-factory]]))


(defn moustache-rendering
  [headers]
  (let [user-agent (headers "user-agent" "")]
    {:status 200
     :body (render-file "templates/stencil.html"
                        {:user-agent (when (seq user-agent)
                                       user-agent)})}))


(defn hiccup-rendering
  [headers]
  (let [user-agent (headers "user-agent" "")
        user-agent-markup (when (seq user-agent)
                            (vector [:span.user-agent-header "User Agent"]
                                    [:br]
                                    [:span.user-agent user-agent]))
        body-markup (apply vector
                           :div.wrapper
                           [:h1 "Hiccup Rendering"]
                           user-agent-markup)]
    {:status 200
     :body (hc/html [:html
                     [:head
                      [:link {:rel "stylesheet", :type "text/css", :href "/css/screen.css"}]]
                     [:body
                      body-markup]])}))


(cc/defroutes server-routes*
  (cc/GET "/" [] (str "<a href=\"/moustache/\">Moustache Rendering</a>"
                      "<br/>"
                      "<a href=\"/hiccup/\">Hiccup Rendering</a>"))
  (cc/GET "/moustache/" {headers :headers} (moustache-rendering headers))
  (cc/GET "/hiccup/" {headers :headers} (hiccup-rendering headers))
  (cc/GET "*" [] {:status 404
                  :body "Page Not Found"}))


(defn wrap-pprint
  "A middleware to print important things about request"
  [handler]
  (fn [request]
    (let [params (merge (:query-params request)
                        (:form-params request)
                        (:multipart-params request))
          http-method (upper-case (name (:request-method request)))
          uri (:uri request)
          start (System/nanoTime)
          response (handler request)
          elapsed (/ (double (- (System/nanoTime) start)) 1000.0)]
      (log/info (format "%s %s %s ms\n%s"
                        http-method
                        uri
                        elapsed
                        (if (seq params)
                          (with-out-str (pprint params))
                          "")))
      response)))

;;; Attach middlewares to routes
(defn server-routes
  []
  (-> #'server-routes*
      (wrap-resource "static")
      (wrap-file-info)
      wrap-pprint                  ; You can remove this in production
      wrap-multipart-params
      wrap-params))


(defn -main
  "Starts the Web server"
  [& args]
  (let [mode (keyword (or (first args) :dev))]
    (when (= mode :dev)
      ;; Don't cache templates in dev mode
      (set-cache (ttl-cache-factory {})))
    (run-jetty (server-routes) {:port 8080 :join? false})
    (println "Started server, Browse http://localhost:8080/")))
