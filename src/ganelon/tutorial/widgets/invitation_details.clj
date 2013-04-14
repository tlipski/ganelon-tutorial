(ns ganelon.tutorial.widgets.invitation-details
  (:require [ganelon.web.helpers :as web-helpers]
            [ganelon.web.widgets :as widgets]
            [ganelon.web.actions :as actions]
            [ganelon.web.ui-operations :as ui-operations]
            [noir.session :as sess]
            [hiccup.util]
            [hiccup.core :as hiccup]
            [ganelon.tutorial.pages.common :as common]
            [ganelon.tutorial.services.invitation :as invitation]
            [ganelon.tutorial.services.meetup :as meetup]
            [ganelon.tutorial.widgets.meetup-times :as meetup-times]))

(defn invitation-details-widget [id]
  (common/link-to-source
    (widgets/with-div
      (if-let [inv (invitation/retrieve id)]
        (let [meetup (meetup/retrieve (:meetup-id inv))]
          [:div [:h2 (hiccup.util/escape-html (:title meetup))]
           [:p "Located at: " (hiccup.util/escape-html (:place meetup))]])
        [:div.alert.alert-error [:h2 "Invitation not found"]
         [:p "Invitation with a supplied id has not been found. Please make sure that the link is correct."]]))))
