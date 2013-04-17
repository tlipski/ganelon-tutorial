;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

(ns ganelon.tutorial.services.meetup-time
  (:require [somnium.congomongo :as db]
            [crypto.random :as rnd]))

(defn retrieve [id]
  (db/fetch-one :meetup-times :where {:_id (db/object-id id)}))

(defn retrieve-list [meetup-id]
  (db/fetch :meetup-times :where {:meetup-id meetup-id} :sort {:date 1 :time 1}))

(defn add-time! [meetup-id date time]
  (when (empty? (db/fetch :meetup-times :where {:meetup-id meetup-id :date date :time time}))
   (db/insert! :meetup-times {:meetup-id meetup-id :date date :time time :create-time (java.util.Date.) :accepted []})))

(defn remove-time! [id]
  (db/destroy! :meetup-times {:_id (db/object-id id)}))

(defn accept-time! [id invitation-id]
  (let [time (retrieve id)]
    (db/update! :meetup-times time (assoc time :accepted (into [] (into #{} (conj (:accepted time) invitation-id)))))))

(defn reject-time! [id invitation-id]
  (let [time (retrieve id)]
    (println (filter (complement #{invitation-id}) (:accepted time)))
    (db/update! :meetup-times time (assoc time :accepted (filter (complement #{invitation-id}) (:accepted time))))))