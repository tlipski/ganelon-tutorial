;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns ganelon.tutorial.pages.common
  (:require [ganelon.web.dyna-routes :as dyna-routes]
            [ganelon.web.widgets :as widgets]
            [ganelon.web.actions :as actions]
            [ganelon.web.ui-operations :as ui]
            [ganelon.web.helpers :as webhelpers]
            [hiccup.page :as hiccup]
            [hiccup.core :as h]
            [hiccup.util]
            [noir.response :as resp]
            [noir.request :as req]
            [noir.session :as sess]
            [compojure.core :as compojure]))

(defn navbar []
  (h/html
    [:div.navbar.navbar-inverse.navbar-fixed-top {:style "opacity: 0.9;"}
     [:div.navbar-inner
      [:div.container
       [:a.brand {:href "/"} "Ganelon interactive tutorial"]]]]))

(defn layout [& content]
  (hiccup/html5
    [:head [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
     [:title "Ganelon tutorial - Meetups"]
     ;real life site should use CDN/minify to serve static resources
     (hiccup/include-css "/ganelon/css/bootstrap.css")
     (hiccup/include-css "/ganelon/css/bootstrap-responsive.css")
     ]
    [:body.default-body [:div#navbar (navbar)]
     [:div.container {:style "padding-top: 70px"}
      content]
     [:footer {:style "opacity:0.9; text-align: center; padding: 30px 0; margin-top: 70px; border-top: 1px solid #E5E5E5; color: #f6f6f6; background-color: #161616;"}
      [:div.container [:p "The Ganelon framework has been designed, created and is maintained by " [:a {:href "http://twitter.com/tomeklipski"} "@tomeklipski"] "."]
       [:p "The code is available under " [:a {:href "http://opensource.org/licenses/eclipse-1.0.php"} "Eclipse Public License 1.0"] "."]
       [:p [:a {:href "http://github.com/tlipski/ganelon-tutorial"} "View the sources on GitHub."]]
       [:p "This interactive tutorial runs on " [:a {:href "http://cloudbees.com"} "CloudBees"]
        " and " [:a {:href "http://mongohq.com"} "MongoHQ"] "."]
       ]]]))