(ns ganelon.tutorial.pages.login
  (:require
    [ganelon.web.dyna-routes :as dyna-routes]
    [ganelon.web.helpers :as web-helpers]
    [ganelon.web.widgets :as widgets]
    [ganelon.web.actions :as actions]
    [ganelon.web.ui-operations :as ui-operations]
    [noir.session :as sess]
    [somnium.congomongo :as m]
    [hiccup.util]
    [ganelon.tutorial.pages.common :as common]))

(defn collections-list []
 [:div.span3
  [:ul.nav.nav-list
   [:li.nav-header "Available collections:"]
   (map (fn [x] [:li [:a {:href (str "/collections/" x)} (hiccup.util/escape-html x)]])
     (m/collections))]])

(defn collection-widget [name & {:keys }]

)

(dyna-routes/defpage "/collections" []
  (common/layout
    [:div.row
      (collections-list)
      [:div.span9
        [:h1 "Collections"]
        [:p "Please select a collection from a list to the left."]]]))

(dyna-routes/defpage "/collections/:name/skip/:skip" [name]
  (common/layout
    [:div.row
     (collections-list)
     [:div.span9
      [:h1 (hiccup.util/escape-html name)]
     (collection-widget name)
     ]]))