(ns ganelon.tutorial.middleware
  (:require [ganelon.web.dyna-routes :as dyna-routes]
            [ganelon.tutorial.pages.common :as common]
            [noir.cookies :as cookies]))

(def ^:dynamic *admin-id* nil)

(defn get-or-set-admin-id! []
  (if-let [admin-id (cookies/get :meetup-admin-id )]
    admin-id
    (let [admin-id (java.util.UUID/randomUUID)]
      (cookies/put! :meetup-admin-id admin-id)
      admin-id)))

(dyna-routes/setmiddleware! :admin-id (fn [handler]
                                        (fn [request]
                                          (binding [*admin-id* (get-or-set-admin-id!)]
                                            (handler request)))))

(defmacro with-admin-id-from-meetup! [meetup-id & body]
  `(let [meetup# (db/fetch-one :meetups :where {:_id (db/object-id ~meetup-id)})]
     (cookies/put! :meetup-admin-id (:admin-id meetup#))
     (binding [*admin-id* (:admin-id meetup#)]
       ~@body)))

(defn get-admin-id []
  *admin-id*)
