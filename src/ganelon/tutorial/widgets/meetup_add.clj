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
            [ganelon.tutorial.widgets.meetup-edit :as meetup-edit]
            [ganelon.tutorial.middleware :as middleware]
            ))

(defn new-meetup-widget []
  (widgets/with-div
    [:h1 "New meetup"]
    [:p "Please provide meetup details:"]
    (widgets/action-form "meetup-create" {} {:class "form well"}
      [:div.control-group [:label.control-label {:for "inputTitle"} "Title"]
       [:div.controls [:input#inputTitle {:placeholder "Title for a meetup" :type "text"
                                          :value (web-helpers/get-request-parameter "title")
                                          :name "title"
                                          :required "1"}]]]
      [:div.control-group [:label.control-label {:for "inputPlace"} "Place"]
       [:div.controls [:input#inputPlace {:placeholder "Place for a meetup" :type "text"
                                          :value (web-helpers/get-request-parameter "place")
                                          :name "place"
                                          :required "1"}]]]
      [:div.control-group [:div.controls [:button.btn.btn-primary.btn-large {:type "submit"} "Create"]]])))

(actions/defjsonaction "meetup-create" [title place]
  (let [id (meetup/create! title place (middleware/get-admin-id))]
    [(common/push-state (str "/meetup/edit/" id))
     (ui-operations/html "#contents"
       (meetup-edit/meetup-details-widget id))]))
