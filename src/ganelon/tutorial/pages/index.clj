(ns ganelon.tutorial.pages.login
  (:require
    [ganelon.web.dyna-routes :as dyna-routes]
    [ganelon.web.helpers :as web-helpers]
    [ganelon.web.widgets :as widgets]
    [ganelon.web.actions :as actions]
    [ganelon.web.ui-operations :as ui-operations]
    [noir.session :as sess]
    [hiccup.util]
    [ganelon.tutorial.pages.common :as common]
    [somnium.congomongo :as db]))

(defn new-meetup-widget [& msg]
  (widgets/with-div
    [:h1 "New meetup"]
    [:p "Please provide meetup details:"]
    msg
    (widgets/action-form "meetup-create" {} {:class "form-horizontal"}
      [:div.control-group [:label.control-label {:for "inputTitle"} "Title"]
       [:div.controls [:input#inputTitle {:placeholder "Title for a meetup" :type "text"
                                          :value (web-helpers/get-request-parameter "title")
                                          :name "title"
                                          :required "1"}]]]
      [:div.control-group [:label.control-label {:for "inputPlace"} "Place"]
       [:div.controls [:input#inputPlace {:placeholder "Place for a meetup" :type "text"
                                          :value (web-helpers/get-request-parameter "place")
                                          :name "title"
                                          :required "1"}]]]
      [:div.control-group [:div.controls [:button.btn.btn-primary.btn-large {:type "submit"} "Create"]]]
      )))

(defn meetup-details-widget [meetup]
  (widgets/with-div
    [:h1 "Meetup details"]
    (widgets/action-form "meetup-update" {} {:class "form-horizontal"}
      [:div.control-group [:label.control-label {:for "inputTitle"} "Title"]
       [:div.controls [:input#inputTitle {:placeholder "Title for a meetup" :type "text"
                                          :value (web-helpers/get-request-parameter "title")
                                          :onchange "form.submit();"
                                          :name "title"
                                          :required "1"}]]]
      [:div.control-group [:label.control-label {:for "inputPlace"} "Place"]
       [:div.controls [:input#inputPlace {:placeholder "Place for a meetup" :type "text"
                                          :value (web-helpers/get-request-parameter "place")
                                          :onchange "form.submit();"
                                          :name "title"
                                          :required "1"}]]])

  ))
(actions/defwidgetaction "meetup-create" [title place]


  (let [id (:_id (db/insert! :meetups {:title title :place place :times [{}]}))]
    (ui-operations/open-page (str "/meetup/" id)))
  )

(dyna-routes/defpage "/" []
  (common/layout
    (new-meetup-widget)))

