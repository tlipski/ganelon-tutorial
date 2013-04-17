;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns ganelon.tutorial.middleware
  (:require [ganelon.web.dyna-routes :as dyna-routes]
            [ganelon.tutorial.pages.common :as common]
            [ganelon.tutorial.services.meetup :as meetup]
            [noir.cookies :as cookies]
            [crypto.random :as rnd]))

(def ^:dynamic *admin-id* nil)

(defn get-or-set-admin-id! []
  (if-let [admin-id (cookies/get :meetup-admin-id )]
    admin-id
    (let [admin-id (rnd/url-part 20)]
      (cookies/put! :meetup-admin-id {:value admin-id :path "/" :expires "Thu, 01-Jan-2099 00:00:00 GMT"})
      admin-id)))

(dyna-routes/setmiddleware! :admin-id (fn [handler]
                                        (fn [request]
                                          (binding [*admin-id* (get-or-set-admin-id!)]
                                            (handler request)))))

(defmacro with-admin-id-from-meetup! [meetup-id & body]
  `(let [meetup# (meetup/retrieve ~meetup-id)]
     (cookies/put! :meetup-admin-id (:admin-id meetup#))
     (binding [*admin-id* (:admin-id meetup#)]
       ~@body)))

(defn get-admin-id []
  *admin-id*)
