(ns leiningen.new.web-template
  (:use [leiningen.new.templates :only [renderer name-to-path sanitize-ns ->files]]))

(def render (renderer "lein-web-template"))

(defn lein-web-template
  [name]
  (let [data {:name name
              :ns-name (sanitize-ns name)
              :sanitized (name-to-path name)}]
    (->files data ["src/{{sanitized}}/core.clj" (render "core.clj" data)]
["project.clj" (render "project.clj" data)]
["resources/static/css/screen.css" (render "screen.css")]
["resources/templates/stencil.html" (render "stencil.html")]
)))
