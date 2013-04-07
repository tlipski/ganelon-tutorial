(ns ganelon.tutorial.widgets.meetup-times
  (:require [ganelon.web.helpers :as web-helpers]
            [ganelon.web.widgets :as widgets]
            [ganelon.web.actions :as actions]
            [ganelon.web.ui-operations :as ui-operations]
            [noir.session :as sess]
            [hiccup.util]
            [hiccup.core :as hiccup]
            [ganelon.tutorial.pages.common :as common]
            [ganelon.tutorial.services.meetup :as meetup]
            ))

(defn meetup-times-list-widget [meetup]
  (widgets/with-widget "meetup-times-list-widget"
    (if (not-empty (:times meetup))
      [:table.table.table-striped.table-hover {:style "width: initial"}
       [:thead [:tr [:th ] [:th "Date"] [:th "Time"]]]
       (for [t (:times meetup)]
         [:tr [:td (when (empty? (:accepted t))
                     (widgets/action-link "meetup-remove-time" (assoc t :id (:_id meetup)) {} [:i.icon-remove ]))]
          [:td (:date t)] [:td (:time t)]])]
      [:div.alert [:i "No meetup times defined yet!"]])))

(defn meetup-times-widget [meetup]
  (widgets/with-div
    [:h2 "Possible meetup times"]
    [:div#meetup-add-time-message ]
    (widgets/action-form "meetup-add-time" {:id (:_id meetup)} {:class "form-inline"}
      [:label.control-label {:for "meetup-add-date"} "Date "]
      [:div.controls.input-append.date.datepicker {:data-date-format "dd-mm-yyyy" :data-date ""}
       [:input#meetup-add-date.input-small {:type "text" :required "1" :name "date"}]
       [:span.add-on [:i.icon-calendar "&nbsp;"]]]
      [:label.control-label {:for "meetup-add-date"} "Time "]
      [:div.controls.input-append.bootstrap-timepicker {:style "padding: 4px"}
       [:input#meetup-add-time.input-small {:type "text" :required "1" :name "time"}]
       [:span.add-on [:i.icon-time "&nbsp;"]]]
      [:script "$('#meetup-add-time').timepicker({showMeridian: false});"]
      [:script "$('.datepicker').datepicker();"]
      [:button.btn.btn-success {:type "submit"} "Add"])
    (meetup-times-list-widget meetup)
    ))

(actions/defjsonaction "meetup-add-time" [id date time]
  (if-let [new-mu (meetup/add-time! id date time)]
    ;success
     [(ui-operations/make-empty "#meetup-add-time-message")
      (ui-operations/html "#meetup-times-list-widget" (meetup-times-list-widget new-mu))]
    ;error - such time already exists
     (ui-operations/html "#meetup-add-time-message"
        (hiccup.core/html [:div.alert [:button.close {:type "button" :data-dismiss "alert"} "×"]
                           [:p "Such date & time combination already exists!"]]))))

(actions/defjsonaction "meetup-remove-time" [id date time]
  (ui-operations/html "#meetup-times-list-widget" (meetup-times-list-widget
                                               (meetup/remove-time! id date time))))
