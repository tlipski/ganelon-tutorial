(ns ganelon.tutorial.services.invitation
  (:require [somnium.congomongo :as db]
            [crypto.random :as rnd]))

(defn create! [meetup-id name]
  (db/insert! :invitations {:_id (rnd/url-part 7) :name name :meetup-id meetup-id :create-time (java.util.Date.)}))

(defn retrieve [id]
  (db/fetch-one :invitations :where {:_id id}))

(defn retrieve-list [meetup-id]
  (db/fetch :invitations :where {:meetup-id meetup-id} :sort {:create-time -1}))

(defn delete! [id]
  (db/destroy! :invitations {:_id id}))
