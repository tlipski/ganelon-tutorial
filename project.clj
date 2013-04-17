;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(defproject ganelon-tutorial "0.9-SNAPSHOT"
  :description "Ganelon tutorial"
  :url "http://ganelon.tomeklipski.com"
  :dependencies [[ganelon "0.9-SNAPSHOT"]
                 [congomongo "0.4.1"]
                 [crypto-random "1.1.0"]]
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"}
  :aot :all
  :plugins [[lein-ring "0.8.3"]]
  :ring {:handler ganelon.tutorial/handler :init ganelon.tutorial/initialize}
  :main ganelon.tutorial)
