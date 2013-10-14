;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns ganelon.tutorial.widgets.meetup-add
  (:require [ganelon.web.helpers :as web-helpers]
            [ganelon.web.widgets :as widgets]
            [ganelon.web.actions :as actions]
            [ganelon.web.ui-operations :as ui-operations]
            [noir.session :as sess]
            [hiccup.util]
            [hiccup.core :as hiccup]
            [ganelon.tutorial.pages.common :as common]
            [ganelon.tutorial.services.meetup :as meetup]
            [ganelon.tutorial.widgets.meetup-edit :as meetup-edit]))

(defn new-meetup-widget []
  (widgets/with-div
    [:h1 "New meetup"]
    [:p "Please provide meetup details:"]
    (widgets/action-form "meetup-create" {} {:class "form well"}
      [:div.control-group [:label.control-label {:for "inputTitle"} "Title"]
       [:div.controls
        [:input#inputTitle {:placeholder "Title for a meetup" :type "text"
                            :name "title"
                            :required "1"}]]]
      [:div.control-group [:label.control-label {:for "inputPlace"} "Place"]
       [:div.controls
        [:input#inputPlace {:placeholder "Place for a meetup" :type "text"
                            :name "place"
                            :required "1"}]]]
      [:div.control-group
       [:div.controls
        [:button.btn.btn-primary.btn-large {:type "submit"} "Create"]]])))

(actions/defjsonaction "meetup-create" [title place]
  (let [id (meetup/create! title place)]
    [(ui-operations/open-page (str "/meetup/edit/" id))]))