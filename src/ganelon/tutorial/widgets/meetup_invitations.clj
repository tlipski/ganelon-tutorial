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
    [:p (widgets/action-button "meetup-create-invitation" {:meetup-id meetup-id}
          {:class "btn btn-primary"} "Create new invitation")]
    (for [inv (meetup/retrieve-invitations meetup-id)]
      [:p "Link: " [:a {:href (str (web-helpers/current-request-host-part) "/invites/" (:_id inv))}
                    (str (web-helpers/current-request-host-part) "/invites/" (:_id inv))]])))

(actions/defwidgetaction "meetup-create-invitation" [meetup-id]
  (meetup/create-invitation! meetup-id)
  (meetup-invitations-widget meetup-id))