(ns ganelon.tutorial.services.meetup
  (:require [somnium.congomongo :as db]))

(defn mkid [id]
  (cond
    (instance? org.bson.types.ObjectId) id
    (string? id) (db/object-id id)
    :else (mkid (str id))))

(defn retrieve [id]
  (db/fetch-one :meetups :where {:_id (mkid id)}))

(defn retrieve-list [admin-id skip]
  (db/fetch :meetups :where {:admin-id admin-id} :skip skip :sort {:_id -1}))

(defn create! [title place admin-id]
  (db/insert! :meetups {:title title :place place :times []
                        :admin-id admin-id
                        :create-time (java.util.Date.)}))

(defn update! [id & attrs]
  (let [meetup (retrieve id)
        new-meetup (merge meetup (apply hash-map attrs))]
    (db/update! :meetups meetup new-meetup)
    new-meetup))

(defn create-invitation! [meetup-id]
  (db/insert! :meetup-invites {:meetup-id (mkid meetup-id) :create-time (java.util.Date.)}))

(defn retrieve-invitations [meetup-id]
  (db/fetch :meetup-invites :where {:meetup-id (mkid meetup-id)} :sort {:_id -1}))

(defn add-time! [id date time]
  (let [mu (retrieve id)]
    (when (not (some (fn [x] (and (= (:date x) date) (= (:time x) time))) (:times mu)))
      (update! id :times (conj (:times mu) {:date date :time time :accepted []})))))

(defn remove-time! [id date time]
  (update! id :times
    (filter (fn [x] (not (and (= (:date x) date) (= (:time x) time)))) (:times (retrieve id)))))
