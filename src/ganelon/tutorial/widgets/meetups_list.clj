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

(defn meetups-list-widget [skip]
  (let [skip (or skip (sess/get :meetups-list-skip) 0)
        admin-id (middleware/get-admin-id)
        cnt (meetup/count-list admin-id)]
    (widgets/with-widget "meetups-list-widget"
      [:h2 "Your recent meetups"]
      [:p "Total: " [:b cnt] [:br]
       (widgets/action-button "meetups-list" {:skip 0}
         {:class "btn" :disabled (when (<= skip 0) "1")} [:i.icon-fast-backward ])
       (widgets/action-button "meetups-list" {:skip (- skip LIMIT)}
         {:class "btn" :disabled (when (< (- skip LIMIT) 0) "1")} [:i.icon-backward ])
       (widgets/action-button "meetups-list" {:skip (+ skip LIMIT) }
         {:class "btn" :disabled (when (<= cnt (+ skip LIMIT)) "1")} [:i.icon-forward ])
       (widgets/action-button "meetups-list" {:skip (* LIMIT (quot cnt LIMIT))}
         {:class "btn" :disabled (when (<= cnt (+ skip LIMIT)) "1")} [:i.icon-fast-forward ])]
      [:ul.nav.nav-list
      (for [mu (meetup/retrieve-list admin-id skip LIMIT)]
        [:li
         (widgets/action-link "meetup-edit" {:id (:_id mu)} {}
           [:span {:class (str "meetup-title-" (:_id mu))}
            (hiccup.util/escape-html (:title mu))]
           [:small {:style "margin-left: 5px; color: #666666"} (str (:create-time mu))])])])))

(defn update-list-widget-operation [skip]
  (ui-operations/fade "#meetups-list-widget"
    (meetups-list-widget skip)))

(actions/defjsonaction "meetups-list" [skip]
  (let [skip (Integer/parseInt skip)]
    (sess/put! :meetups-list-skip skip)
    (update-list-widget-operation skip)))
