;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns ganelon.tutorial.widgets.meetup-edit
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
    [ganelon.tutorial.widgets.meetup-times :as meetup-times]
    [ganelon.tutorial.widgets.meetup-invitations :as meetup-invitations]))

(defn meetup-edit-form-widget [meetup]
  (widgets/with-div
    [:h1 "Meetup details"]
    (let [url (str (web-helpers/current-request-host-part)  "/meetup/edit/"
                (:_id meetup))]
      [:p "Meetup admin url: " [:a {:href url } url]])
    [:form.form-horizontal.well
     [:div.control-group [:label.control-label {:for "inputTitle"} "Title"]
      [:div.controls [:input#inputTitle.input-xlarge
                      {:placeholder "Title for a meetup" :type "text"
                       :value (:title meetup)
                       :onkeypress "$('#update-title-loader > *').fadeOut();"
                       :onchange (str "GanelonAction.meetup_title_update('"
                                   (:_id meetup)
                                   "', this.value);")
                       :name "title"
                       :required "1"}]
       [:span#update-title-loader]]]
     [:div.control-group [:label.control-label {:for "inputPlace"} "Place"]
      [:div.controls [:input#inputPlace.input-xlarge
                      {:placeholder "Place for a meetup" :type "text"
                       :value (:place meetup)
                       :onkeypress "$('#update-place-loader > *').fadeOut();"
                       :onchange (str "GanelonAction.meetup_place_update('"
                                   (:_id meetup)
                                   "', this.value);")
                       :name "place"
                       :required "1"}]
       [:span#update-place-loader]]]]))

(defn meetup-details-widget [meetup-id]
  (widgets/with-div
    (let [meetup (meetup/retrieve meetup-id)]
      [:div
        [:div {:style "padding-top: 20px;"}
          (meetup-edit-form-widget meetup)]
        (meetup-times/meetup-times-widget meetup-id)
        (meetup-invitations/meetup-invitations-widget meetup-id)])))

(actions/defjsonaction "meetup-edit" [id]
  [(ui-operations/open-page (str "/meetup/edit/" id))])

(actions/defjsonaction "meetup-title-update" [id title]
  (meetup/update! id :title title)
  [(ui-operations/fade (str ".meetup-title-" id)
     (hiccup.util/escape-html title))
   (ui-operations/fade "#update-title-loader"
    (hiccup.core/html [:span {:onmouseover "$(this).fadeOut();"}
                       "&nbsp;" [:i.icon-check] "&nbsp;Saved"]))])

(actions/defjsonaction "meetup-place-update" [id place]
  (meetup/update! id :place place)
  (ui-operations/fade "#update-place-loader"
    (hiccup.core/html [:span {:onmouseover "$(this).fadeOut();"} "&nbsp;" [:i.icon-check] "&nbsp;Saved"])))
