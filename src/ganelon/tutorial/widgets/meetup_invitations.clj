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
    [ganelon.tutorial.services.meetup :as meetup]
    [ganelon.tutorial.widgets.meetup-times :as meetup-times]))

(defn meetup-invitations-widget [meetup-id]
  (widgets/with-widget "meetup-invitations-widget"
    [:h2 "Meeting invitations"]
    (widgets/action-form "invitation-create" {:meetup-id meetup-id} {:class "form-horizontal well"}
      [:span "Recipient's name:"] "&nbsp;"
      [:input {:type "text" :name "name" :required "1"}] "&nbsp;"
      [:button.btn.btn-primary "Create new invitation"])
    (for [inv (meetup/retrieve-invitations meetup-id)]
      [:p
       [:b (hiccup.util/escape-html (:name inv))] [:br]
       "Link: " [:a {:href (str (web-helpers/current-request-host-part) "/i/" (:_id inv))}
                    (str (web-helpers/current-request-host-part) "/i/" (:_id inv))]])))

(actions/defwidgetaction "invitation-create" [name meetup-id]
  (meetup/create-invitation! meetup-id name)
  (meetup-invitations-widget meetup-id))

(actions/defwidgetaction "invitation-cancel" [id]
  )