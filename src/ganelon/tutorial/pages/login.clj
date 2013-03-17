(ns ganelon.tutorial.pages.login
  (:require
    [ganelon.web.dyna-routes :as dyna-routes]
    [ganelon.web.helpers :as web-helpers]
    [ganelon.web.widgets :as widgets]
    [ganelon.web.actions :as actions]
    [ganelon.web.ui-operations :as ui-operations]
    [noir.session :as sess]
    [hiccup.util]
    [ganelon.tutorial.pages.common :as common]))


(defn login-widget [& msg]
  (widgets/with-div
    [:div.row [:div.span4 msg]
     [:div.span8
      [:h1 "Connect"]
      (widgets/action-form "login" {} {:class "form-horizontal well"}
       [:div.control-group [:label.control-label {:for "inputUser"} "Username"]
        [:div.controls [:input#inputUser {:placeholder "MongoDB username" :type "text"
                                          :value (web-helpers/get-request-parameter "inputUser")
                                          :name "inputUser"}]]]
       [:div.control-group [:label.control-label {:for "inputPassword"} "Password"]
        [:div.controls [:input#inputPassword {:type "password" :value "" :name "inputPassword"}]]]
       [:div.control-group [:label.control-label {:for "inputHost"} "Username"]
        [:div.controls [:input#inputHost {:placeholder "MongoDB host" :type "text"
                                          :name "inputHost"
                                          :value (or (web-helpers/get-request-parameter "inputHost") "127.0.0.1")}]]]
       [:div.control-group [:label.control-label {:for "inputPort"} "Port"]
        [:div.controls [:input#inputPort {:placeholder "MongoDB port" :type "text" :name "inputPort"
                           :value (or (web-helpers/get-request-parameter "inputPort") "27017")}]]]
       [:div.control-group [:label.control-label {:for "inputDB"} "Database"]
        [:div.controls [:input#inputDB {:placeholder "MongoDB database" :type "text" :name "inputDB"
                           :value (web-helpers/get-request-parameter "inputDB")}]]]
       [:div.control-group [:div.controls [:button.btn.btn-primary {:type "submit"} "Login"]]])]]))

(defn login-page-content []
  (login-widget
    (if (sess/get :user)
      [:div.alert.alert-success
       [:h3 "Connected!"]
       [:p [:strong (sess/get :user) "@" (sess/get :host) ":" (sess/get :port) "/" (sess/get :db)]]
       [:p "You can use the form to the right to reconnect to another mongodb instance"]]
      [:div.alert
       [:h4 "Please sign in"]
       [:p "Please enter mongodb connection params in the form to the right and press <b>Login</b>."]])))

(dyna-routes/defpage "/" []
  (common/layout (login-page-content)))

(actions/defwidgetaction "login" [inputUser inputPassword inputHost inputPort inputDB]
  (try
    (common/make-mongo-connection inputUser inputPassword inputHost (Integer/parseInt inputPort) inputDB)
    (sess/put! :user inputUser)
    (sess/put! :password inputPassword)
    (sess/put! :host inputHost)
    (sess/put! :port (Integer/parseInt inputPort))
    (sess/put! :db inputDB)
    (actions/put-operation! (ui-operations/replace-with "#navbar" (common/navbar)))
    (login-widget
      [:div.alert.alert-success
       [:h4 "Logged in"]
       [:p "You can use the form to the right to reconnect to another mongodb instance"]])
    (catch Exception e
      (login-widget
        [:div.alert.alert-danger
          [:h4 "Login failed"]
          [:p "Failed to connect to MongoDB: "]
          [:pre (hiccup.util/escape-html (str (.getClass e) ": " (.getMessage e)))]]))))