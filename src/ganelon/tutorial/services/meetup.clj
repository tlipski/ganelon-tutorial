;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

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



