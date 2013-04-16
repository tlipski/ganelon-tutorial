(ns ganelon.tutorial.pages.common
  (:require [ganelon.web.dyna-routes :as dyna-routes]
            [ganelon.web.widgets :as widgets]
            [ganelon.web.actions :as actions]
            [ganelon.web.ui-operations :as ui]
            [ganelon.web.helpers :as webhelpers]
            [hiccup.page :as hiccup]
            [hiccup.core :as h]
            [hiccup.util]
            [clojure.pprint :as pprint]
            [noir.response :as resp]
            [noir.request :as req]
            [noir.session :as sess]
            [compojure.core :as compojure]
            [somnium.congomongo :as m]))

(defn navbar []
  (h/html
    [:div.navbar.navbar-inverse.navbar-fixed-top {:style "opacity: 0.9;"}
     [:div.navbar-inner [:div.container [:button.btn.btn-navbar.collapsed {:type "button" :data-target ".nav-collapse" :data-toggle "collapse"}
                                         [:span.icon-bar ]
                                         [:span.icon-bar ]
                                         [:span.icon-bar ]]
                         [:a.brand {:href "/"} "Ganelon tutorial"]
                         [:div.nav-collapse.collapse [:ul.nav [:li [:a {:href "/"} "New meetup!"]]]]]]]))

(defn layout [& content]
  (hiccup/html5
    [:head [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
     [:title "Ganelon tutorial - MongoDB browser"]
     ;real life site should use CDN/minify to serve static resources
     (hiccup/include-css "/ganelon/css/bootstrap.css")
     (hiccup/include-css "/ganelon/css/bootstrap-responsive.css")
     (hiccup/include-css "/ganelon/css/jquery.gritter.css")
     (hiccup/include-css "/datepicker/css/datepicker.css")
     (hiccup/include-css "/timepicker/css/bootstrap-timepicker.min.css")
     (hiccup/include-js "/ganelon/js/jquery-1.8.1.min.js") ;jQuery - required
     (hiccup/include-js "/ganelon/js/bootstrap.js") ;Bootstrap - optional
     (hiccup/include-js "/ganelon/js/ganelon.js") ;basic actions support
     (hiccup/include-js "/ganelon/js/ext/ganelon.ops.bootstrap.js") ;additional Bootstrap related actions
     (hiccup/include-js "/ganelon/js/ext/ganelon.ops.gritter.js") ; growl-style notifications through gritter.js
     (hiccup/include-js "/ganelon/actions.js") ;dynamic actions interface
     (hiccup/include-js "/js/ganelon-tutorial.js") ;additional plugins
     (hiccup/include-js "/datepicker/js/bootstrap-datepicker.js") ;date picker
     (hiccup/include-js "/timepicker/js/bootstrap-timepicker.min.js") ;date picker
     ;code highlighting
     (hiccup/include-css "http://google-code-prettify.googlecode.com/svn/trunk/src/prettify.css")
     (hiccup/include-js "http://google-code-prettify.googlecode.com/svn/trunk/src/prettify.js")
     (hiccup/include-js "http://google-code-prettify.googlecode.com/svn/trunk/src/lang-css.js")
     (hiccup/include-js "http://google-code-prettify.googlecode.com/svn/trunk/src/lang-clj.js")
     ]
    [:body.default-body [:div#navbar (navbar)]
     [:div.container {:style "padding-top: 70px"}
      content]
     [:footer {:style "opacity:0.9; text-align: center; padding: 30px 0; margin-top: 70px; border-top: 1px solid #E5E5E5; color: #f6f6f6; background-color: #161616;"}
      [:div.container [:p "The Ganelon framework has been designed, created and is maintained by " [:a {:href "http://twitter.com/tomeklipski"} "@tomeklipski"] "."]
       [:p "The code is available under " [:a {:href "http://opensource.org/licenses/eclipse-1.0.php"} "Eclipse Public License 1.0"] "."]
       [:p "This demo site runs on " [:a {:href "http://heroku.com"} "heroku"] "."]
       ]]
     [:script "prettyPrint();"]
     ]))

(defn push-state [url]
  (ui/ui-operation "push-state" :url url))

(def GITHUB-URL "https://bitbucket.org/tlipski/ganelon-tutorial/src/master/src/")
(def GITHUB-LINE-PREFIX "#cl-")

(defn split-wrapper-from-body [body]
  (let [body (into [] body)
        pos (first (keep-indexed #(when (coll? %2) %1) body))]
    [(subvec body 0 pos) (subvec body pos)]))

(defn find-matching-paren-pos [str]   ;TODO - try to skip side effects
  (let [cnt (atom 0)
        currpos (atom 0)
        pos (atom nil)]
    (doseq [x str :while (not @pos)]
      (swap! currpos inc)
      (when (= \( x)
        (swap! cnt inc))
      (when (= \) x)
        (swap! cnt dec))
      (when (= @cnt 0)
        (swap! pos (constantly @currpos))))
    @pos))

(defn extract-s-expr [fname start-line]
  (with-open [rdr (clojure.java.io/reader fname)]
    (let [rest-lines (nthnext (line-seq rdr) (dec start-line))
          rest-body (apply str (interpose (System/lineSeparator) rest-lines))
          matching-paren-pos (find-matching-paren-pos rest-body)]
      (.substring rest-body 0 matching-paren-pos))))

(defmacro wrap-with-linked-source [[_ fname args body]]
  (let [[wrapper wrapped] (split-wrapper-from-body body)]
  `(let [id# (java.util.UUID/randomUUID)
         fname# ~*file*
         line#  ~(inc (or (:line (meta &form)) 0))
         source-url# (str GITHUB-URL ~*file* GITHUB-LINE-PREFIX line#)]
     (defn ~fname ~args
       (~@wrapper
           [:div {:style "border-radius: 5px; border: 2px dashed #666; padding: 2px;"}
            [:div
            [:div {:style "float: right; font-size: 9px;"}
             [:a {:href (str "#" id#) :data-toggle "modal"} "show source"]]
            [:div.modal.hide.fade {:role "dialog" :id id# :style "width: 90%; left: 5%; margin: auto auto auto auto; top: 20px;"}
             [:div.modal-header [:button.close {:type "button" :data-dismiss "modal"} "×"]
              [:a
               {:style "float: right; font-size: 9px; margin-right: 6px;"
                :target "_blank" :href source-url#}
               "Source on GitHub (opens new window/tab)"]
              [:h4 (str fname# ":" line#)]
              ]
             [:div.modal-body
;              [:pre.prettyprint.lang-clj ~(with-out-str (pprint/pprint (second &form)))]
              [:pre.prettyprint.lang-clj ~(extract-s-expr *file* (inc (or (:line (meta &form)) 0)))]
              ;           [:iframe {:src source-url# :style "width: 100%; height: 100%l"}]
              ]]]
           ~@wrapped])))))


(defmacro link-to-source [& rest]
  `(h/html
     [:div
      (let [id# (java.util.UUID/randomUUID)
            fname# ~*file*
            line# ~(:line (meta &form))
            source-url# (str GITHUB-URL ~*file* GITHUB-LINE-PREFIX ~(:line (meta &form)))]
       [:div
        [:div {:style "float: right; font-size: 9px;"}
          [:a {:href (str "#" id#) :data-toggle "modal"} "show source"]]
        [:div.modal.hide.fade {:role "dialog" :id id# :style "width: 90%; left: 5%; margin: auto auto auto auto; top: 20px;"}
          [:div.modal-header [:button.close {:type "button" :data-dismiss "modal"} "×"]
           [:a
            {:style "float: right; font-size: 9px; margin-right: 6px;"
             :target "_blank" :href source-url#}
            "Source on GitHub (opens new window/tab)"]
           [:h4 (str fname# ":" line#)]
           ]
          [:div.modal-body
           [:pre.prettyprint.lang-clj ~(with-out-str (pprint/pprint &form))]
;           [:iframe {:src source-url# :style "width: 100%; height: 100%l"}]
           ]]])]
     ~@rest))