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
            [ganelon.tutorial.services.meetup-time :as meetup-time]
            [ganelon.tutorial.services.invitation :as invitation]
            ))

(defn toggle-meetup-times-button [t inv editable-invitation-ids]
  (let [accepted? (some #{(:_id inv)} (:accepted t))]
    (widgets/with-div
      [:div {:style (str "padding: 8px; " (if accepted? "background-color: #5bb75b" "background-color: #9da0a4"))}
       (if (some #{(:_id inv)} editable-invitation-ids)
         (widgets/action-loader-link "meetup-times-toggle-invitation"
           {:invitation-id (:_id inv)
            :id (:_id t)
            :value (not accepted?)} {}
           (if accepted? [:i.icon-thumbs-up ] [:i.icon-thumbs-down ]))
         (if accepted? [:i.icon-thumbs-up ] [:i.icon-thumbs-down ]))])))

(defn meetup-times-list-widget
  ([meetup-id] (meetup-times-list-widget meetup-id nil))
  ([meetup-id editable-invitation-ids]
    (common/link-to-source
      (widgets/with-widget "meetup-times-list-widget"
        (let [times (meetup-time/retrieve-list meetup-id)]
          (if (not-empty times)
            (let [invitations (invitation/retrieve-list meetup-id)
                  editable-invitation-ids (or editable-invitation-ids (map :_id invitations))]
              [:table.table.table-striped.table-hover {:style "width: initial"}
               [:thead [:tr (when-not editable-invitation-ids [:th ]) [:th "Date"] [:th "Time"]
                        (for [inv invitations]
                          [:th [:small (hiccup.util/escape-html (:name inv))]])]]
               (for [t times]
                 [:tr (when-not editable-invitation-ids
                        [:td (widgets/action-link "meetup-remove-time" {:id (:_id t)} {} [:i.icon-remove ])])
                  [:td (:date t)] [:td (:time t)]
                  (for [inv invitations]
                    [:td {:style "text-align: center; padding:0px; border-left: 1px solid #ccc"}
                     (toggle-meetup-times-button t inv editable-invitation-ids)])])])
          [:div.alert [:i "No meetup times defined yet!"]]))))))

(actions/defwidgetaction "meetup-times-toggle-invitation" [id invitation-id value]
  (if (boolean (Boolean. value))
    (meetup-time/accept-time! id invitation-id)
    (meetup-time/reject-time! id invitation-id))
  (toggle-meetup-times-button (meetup-time/retrieve id) (invitation/retrieve invitation-id) [invitation-id]))

(defn refresh-meetup-times-list-widget-operations [meetup-id]
  (ui-operations/fade "#meetup-times-list-widget"
    (meetup-times-list-widget meetup-id)))

(defn meetup-times-widget [meetup-id]
  (common/link-to-source
    (widgets/with-widget "meetup-times-widget"
      [:h2 "Possible meetup times"]
      [:div#meetup-add-time-message ]
      (widgets/action-form "meetup-add-time" {:id meetup-id} {:class "form-inline"}
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
      (meetup-times-list-widget meetup-id))))

(actions/defjsonaction "meetup-add-time" [id date time]
  (if-let [new-mu (meetup-time/add-time! id date time)]
    ;success
     [(ui-operations/make-empty "#meetup-add-time-message")
      (ui-operations/fade "#meetup-times-list-widget" (meetup-times-list-widget id))]
    ;error - such time already exists
     (ui-operations/fade "#meetup-add-time-message"
        (hiccup.core/html [:div.alert [:button.close {:type "button" :data-dismiss "alert"} "×"]
                           [:p "Such date & time combination already exists!"]]))))

(actions/defjsonaction "meetup-remove-time" [id]
  (let [time (meetup-time/retrieve id)]
    (meetup-time/remove-time! id)
    (ui-operations/fade "#meetup-times-list-widget"
      (meetup-times-list-widget (:meetup-id time)))))
