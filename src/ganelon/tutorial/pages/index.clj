(ns ganelon.tutorial.pages.index
  (:require
    [ganelon.web.dyna-routes :as dyna-routes]
    [ganelon.web.helpers :as web-helpers]
    [ganelon.web.widgets :as widgets]
    [ganelon.web.actions :as actions]
    [ganelon.web.ui-operations :as ui-operations]
    [noir.session :as sess]
    [hiccup.util]
    [hiccup.core :as hiccup]
    [ganelon.tutorial.pages.common :as common]
    [somnium.congomongo :as db]))

(defn new-meetup-widget [& msg]
  (widgets/with-div
    [:h1 "New meetup"]
    [:p "Please provide meetup details:"]
    msg
    (widgets/action-form "meetup-create" {} {:class "form-horizontal"}
      [:div.control-group [:label.control-label {:for "inputTitle"} "Title"]
       [:div.controls [:input#inputTitle.input-xlarge {:placeholder "Title for a meetup" :type "text"
                                          :value (web-helpers/get-request-parameter "title")
                                          :name "title"
                                          :required "1"}]]]
      [:div.control-group [:label.control-label {:for "inputPlace"} "Place"]
       [:div.controls [:input#inputPlace.input-xlarge {:placeholder "Place for a meetup" :type "text"
                                          :value (web-helpers/get-request-parameter "place")
                                          :name "place"
                                          :required "1"}]]]
      [:div.control-group [:div.controls [:button.btn.btn-primary.btn-large {:type "submit"} "Create"]]]
      )))

(defn meetup-times-widget [meetup]
  (widgets/with-widget "meetup-times-widget"
    (if (not-empty (:times meetup))
      [:table.table.table-striped.table-hover {:style "width: initial"}
       [:thead [:tr [:th] [:th "Date"] [:th "Time"]] ]
       (for [t (:times meetup)]
        [:tr
         [:td (when (empty? (:accepted t)) (widgets/action-link "meetup-remove-time" (assoc t :id (:_id meetup)) {} [:i.icon-remove]))]
         [:td (:date t)] [:td (:time t)]])]
      [:div.alert [:i "No meetup times defined yet!"]])))

(defn meetup-invitations-widget [meetup]
  (widgets/with-widget "meetup-invitations-widget"
    [:p (widgets/action-button "meetup-create-invitation" {:meetup-id (:_id meetup)} {:class "btn btn-primary"} "Create new invitation")]
    (for [inv (db/fetch :meetup-invites :where {:meetup-id (:_id meetup)} :sort {:_id -1})]
      [:p "Link: " [:a {:href (str (web-helpers/current-request-host-part) "/invites/" (:_id inv))}
                             (str (web-helpers/current-request-host-part) "/invites/" (:_id inv))]])))

(actions/defwidgetaction "meetup-create-invitation" [meetup-id]
  (db/insert! :meetup-invites {:meetup-id (db/object-id meetup-id) :name ""})
  (meetup-invitations-widget (db/fetch-one :meetups :where {:_id (db/object-id meetup-id)})))

(defn meetup-details-widget [meetup]
  (widgets/with-div
    [:h1 "Meetup details"]
    (let [url (str (web-helpers/current-request-host-part)  "/meetup/edit/" (:_id meetup))]
      [:p "Meetup admin url: " [:a {:href url } url]])
    [:form.form-horizontal
      [:div.control-group [:label.control-label {:for "inputTitle"} "Title"]
       [:div.controls [:input#inputTitle.input-xlarge {:placeholder "Title for a meetup" :type "text"
                                          :value (or (web-helpers/get-request-parameter "title")
                                                  (:title meetup))
                                          :onkeypress "$('#update-title-loader > *').fadeOut();"
                                          :onchange (str "GanelonAction.meetup_title_update('" (:_id meetup) "', this.value);")
                                          :name "title"
                                          :required "1"}]
        [:span#update-title-loader]]]
      [:div.control-group [:label.control-label {:for "inputPlace"} "Place"]
       [:div.controls [:input#inputPlace.input-xlarge {:placeholder "Place for a meetup" :type "text"
                                          :value (or (web-helpers/get-request-parameter "place")
                                                  (:place meetup))
                                          :onkeypress "$('#update-place-loader > *').fadeOut();"
                                          :onchange (str "GanelonAction.meetup_place_update('" (:_id meetup) "', this.value);")
                                          :name "place"
                                          :required "1"}]
        [:span#update-place-loader]]]]
    [:h2 "Possible meetup times"]
    [:div#meetup-add-time-message]
    (widgets/action-form "meetup-add-time" {:id (:_id meetup)} {:class "form-inline"}
      [:label.control-label {:for "meetup-add-date"} "Date "]
      [:div.controls.input-append.date.datepicker {:data-date-format "dd-mm-yyyy" :data-date ""}
       [:input#meetup-add-date.input-small {:type "text" :required "1" :name "date"} ]
       [:span.add-on [:i.icon-calendar "&nbsp;"]]]
      [:label.control-label {:for "meetup-add-date"} "Time "]
      [:div.controls.input-append.bootstrap-timepicker {:style "padding: 4px"}
       [:input#meetup-add-time.input-small {:type "text" :required "1" :name "time"} ]
       [:span.add-on [:i.icon-time "&nbsp;"]]]
      [:script "$('#meetup-add-time').timepicker({showMeridian: false});"]
      [:script "$('.datepicker').datepicker();"]
      [:button.btn.btn-success {:type "submit"} "Add"])
    (meetup-times-widget meetup)
    [:h2 "Meeting invitations"]
    (meetup-invitations-widget meetup)))

(actions/defjsonaction "meetup-add-time" [id date time]
  (let [mu (db/fetch-one :meetups :where {:_id (db/object-id id)})]
    (if (not (some (fn [x] (and (= (:date x) date) (= (:time x) time))) (:times mu)))
      (let [new-mu (assoc mu :times (conj (:times mu) {:date date :time time :accepted []}))]
        (db/update! :meetups mu new-mu)
        [(ui-operations/make-empty "#meetup-add-time-message")
         (ui-operations/html "#meetup-times-widget" (meetup-times-widget new-mu))])
      (ui-operations/html "#meetup-add-time-message"
        (hiccup.core/html [:div.alert
                           [:button.close {:type "button" :data-dismiss "alert"} "&times;"]
                           [:p "Such date & time combination already exists!"]])))))

(actions/defjsonaction "meetup-remove-time" [id date time]
  (let [mu (db/fetch-one :meetups :where {:_id (db/object-id id)})
        new-mu (assoc mu :times (filter (fn [x] (not (and (= (:date x) date) (= (:time x) time)))) (:times mu)))]
    (db/update! :meetups mu new-mu)
    (ui-operations/html "#meetup-times-widget" (meetup-times-widget new-mu))))

(actions/defjsonaction "meetup-title-update" [id title]
  (let [mu (db/fetch-one :meetups :where {:_id (db/object-id id)})]
    (db/update! :meetups mu (assoc mu :title title)))
  (ui-operations/html "#update-title-loader"
    (hiccup.core/html [:span {:onmouseover "$(this).fadeOut();"} "&nbsp;" [:i.icon-check] "&nbsp;Saved"])))

(actions/defjsonaction "meetup-place-update" [id place]
  (let [mu (db/fetch-one :meetups :where {:_id (db/object-id id)})]
    (db/update! :meetups mu (assoc mu :place place)))
  (ui-operations/html "#update-place-loader"
    (hiccup.core/html [:span {:onmouseover "$(this).fadeOut();"} "&nbsp;" [:i.icon-check] "&nbsp;Saved"])))

(actions/defwidgetaction "meetup-create" [title place]
  (let [id (:_id (db/insert! :meetups {:title title :place place :times []}))]
    (actions/put-operation!
      (ui-operations/html "#notification-area"
        (hiccup/html
          [:div.alert.alert-success
            [:h1 "Meetup created"]
            [:p "The meetup " [:b (hiccup.util/escape-html title)] " has been created."]]))
      (common/push-state (str "/meetup/edit/" id)))
    (meetup-details-widget (db/fetch-one :meetups :where {:_id id}))))

(dyna-routes/defpage "/" []
  (common/layout
    (new-meetup-widget)))

(dyna-routes/defpage "/meetup/edit/:id" [id]
  (common/layout
    [:div.row
     [:div.span3
      [:h2 "Some information"]
      [:p "Basic information provided here."]
      ]
     [:div.span9 (meetup-details-widget
      (db/fetch-one :meetups :where {:_id (db/object-id id)}))]]))
