;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

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

(common/wrap-with-linked-source
(defn invitation-details-widget [id]
  (widgets/with-div
    (if-let [inv (invitation/retrieve id)]
      (let [meetup (meetup/retrieve (:meetup-id inv))]
        [:div
         [:div [:h2 (hiccup.util/escape-html (:title meetup))]
         [:p "Located at: " [:b (hiccup.util/escape-html (:place meetup))]]
         [:p "Sent to: " [:b (hiccup.util/escape-html (:name inv))]]]
         [:h2 "Confirm your presence"]
         [:p "Please mark times & dates that are best for you:"]
         (meetup-times/meetup-times-list-widget (:meetup-id inv) [id])])
      [:div.alert.alert-error [:h2 "Invitation not found"]
       [:p "Invitation with a supplied id has not been found. Please make sure that the link is correct."]]))))
