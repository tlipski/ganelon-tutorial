;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns ganelon.tutorial.widgets.meetup-invitations
  (:require
    [ganelon.web.helpers :as web-helpers]
    [ganelon.web.widgets :as widgets]
    [ganelon.web.actions :as actions]
    [ganelon.web.ui-operations :as ui-operations]
    [noir.session :as sess]
    [hiccup.util]
    [hiccup.core :as hiccup]
    [ganelon.tutorial.pages.common :as common]
    [ganelon.tutorial.services.invitation :as invitation]
    [ganelon.tutorial.widgets.meetup-times :as meetup-times]))

(common/wrap-with-linked-source
(defn meetup-invitations-widget [meetup-id]
  (widgets/with-widget "meetup-invitations-widget"
    [:h2 "Meeting invitations"]
    (widgets/action-form "invitation-create" {:meetup-id meetup-id} {:class "form-horizontal well"}
      [:span "Recipient's name:"] "&nbsp;"
      [:input {:type "text" :name "name" :required "1"}] "&nbsp;"
      [:button.btn.btn-primary "Create new invitation"]
      ;this function is used solely for demonstration purposes
      (common/show-action-source-link "invitation-create")
      )
    (let [invitations (invitation/retrieve-list meetup-id)]
      (if (empty? invitations)
        [:div.alert "No invitations created yet. Please use the form above to add some!"]
        (for [inv invitations]
          [:p
           [:b (hiccup.util/escape-html (:name inv))] [:br]
           "Link: " [:a {:href (str (web-helpers/current-request-host-part) "/i/" (:_id inv))}
                        (str (web-helpers/current-request-host-part) "/i/" (:_id inv))]
           (widgets/action-link "invitation-cancel" {:meetup-id meetup-id :id (:_id inv)} {:class "pull-right"}
             [:i.icon-remove] " Cancel")])))
    ;this function is used solely for demonstration purposes
    [:div.pull-right (common/show-action-source-link "invitation-cancel")] [:div {:style "height: 30px"}] ;add 30px for .pull-right
    )))
(common/register-action-meta
(actions/defwidgetaction "invitation-create" [name meetup-id]
  (invitation/create! meetup-id name)
  (actions/put-operation! (meetup-times/refresh-meetup-times-list-widget-operations meetup-id))
  (meetup-invitations-widget meetup-id)))

(common/register-action-meta
(actions/defwidgetaction "invitation-cancel" [meetup-id id]
  (invitation/delete! id)
  (actions/put-operation! (meetup-times/refresh-meetup-times-list-widget-operations meetup-id))
  (meetup-invitations-widget meetup-id)))