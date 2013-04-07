(ns ganelon.tutorial.widgets.meetups-list
  (:require [ganelon.web.helpers :as web-helpers]
            [ganelon.web.widgets :as widgets]
            [ganelon.web.actions :as actions]
            [ganelon.web.ui-operations :as ui-operations]
            [noir.session :as sess]
            [hiccup.util]
            [hiccup.core :as hiccup]
            [ganelon.tutorial.pages.common :as common]
            [ganelon.tutorial.services.meetup :as meetup]
            [ganelon.tutorial.widgets.meetup-edit :as meetup-edit]
            [ganelon.tutorial.middleware :as middleware]))

(def LIMIT 10)
(defn meetups-list-widget [offset]
  (widgets/with-widget "meetups-list-widget"
    [:h2 "Your recent meetups"]

    [:p
     (widgets/action-button "meetups-list" {:offset 0} {:class "btn"} [:i.icon-fast-backward])
     (widgets/action-button "meetups-list" {:offset (- offset LIMIT) :disabled (< offset LIMIT)} {:class "btn"} [:i.icon-backward])
     (widgets/action-button "meetups-list" {:offset (+ offset LIMIT) :disabled (< offset LIMIT)} {:class "btn"} [:i.icon-backward])
     ]
    )
  )
