;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

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

(common/wrap-with-linked-source
(defn meetups-list-widget [skip]
  (widgets/with-widget "meetups-list-widget"
    (let [skip (or skip (sess/get :meetups-list-skip) 0)
          admin-id (middleware/get-admin-id)
          cnt (meetup/count-list admin-id)]
      [:div
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
        ;this function is used solely for demonstration purposes
        (common/show-action-source-link "meetups-list")
        [:ul.nav.nav-list
        (for [mu (meetup/retrieve-list admin-id skip LIMIT)]
          [:li
           (widgets/action-link "meetup-edit" {:id (:_id mu)} {}
             [:span {:class (str "meetup-title-" (:_id mu))}
              (hiccup.util/escape-html (:title mu))]
             [:small {:style "margin-left: 5px; color: #666666"} (str (:create-time mu))])])]
        ;this function is used solely for demonstration purposes
        (common/show-action-source-link "meetup-edit")
        ]))))

(defn update-list-widget-operation [skip]
  (ui-operations/html "#meetups-list-widget"
    (meetups-list-widget skip)))

(common/register-action-meta
(actions/defjsonaction "meetups-list" [skip]
  (let [skip (Integer/parseInt skip)]
    (sess/put! :meetups-list-skip skip)
    (update-list-widget-operation skip))))
