(ns ganelon.tutorial.services.meetup
  (:require [somnium.congomongo :as db]
            [crypto.random :as rnd]))

(defn retrieve [id]
  (db/fetch-one :meetups :where {:_id id}))

(defn retrieve-list [admin-id skip limit]
  (db/fetch :meetups :where {:admin-id admin-id} :skip skip :limit limit :sort {:create-time -1}))

(defn count-list [admin-id]
  (db/fetch-count :meetups :where {:admin-id admin-id}))

(defn create! [title place admin-id]
  (let [id (rnd/url-part 6)]
    (db/insert! :meetups {:_id id
                          :title title :place place :times []
                          :admin-id admin-id
                          :create-time (java.util.Date.)})
    id))

(defn update! [id & attrs]
  (let [meetup (retrieve id)
        new-meetup (merge meetup (apply hash-map attrs))]
    (db/update! :meetups meetup new-meetup)
    new-meetup))

(defn create-invitation! [meetup-id name]
  (db/insert! :meetup-invites {:_id (rnd/url-part 7) :name name :meetup-id meetup-id :create-time (java.util.Date.)}))

(defn retrieve-invitations [meetup-id]
  (db/fetch :meetup-invites :where {:meetup-id meetup-id} :sort {:create-time -1}))

(defn add-time! [id date time]
  (let [mu (retrieve id)]
    (when (not (some (fn [x] (and (= (:date x) date) (= (:time x) time))) (:times mu)))
      (update! id :times (conj (:times mu) {:date date :time time :accepted []})))))

(defn remove-time! [id date time]
  (update! id :times (filter (fn [x] (not (and (= (:date x) date) (= (:time x) time))))
                       (:times (retrieve id)))))
