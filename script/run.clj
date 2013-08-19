;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns run
  (:require [ganelon.tutorial]
            [ring.adapter.jetty :as jetty]))

(defonce SERVER (atom nil))

(defn start-demo [port]
  (jetty/run-jetty ganelon.tutorial/handler {:port port :join? false}))

(ganelon.tutorial/initialize)

(let [port (Integer. (get (System/getenv) "PORT" "3000"))]
  (swap! SERVER (fn [s] (when s (.stop s)) (start-demo port))))