;; Copyright (c) Tomek Lipski. All rights reserved.  The use
;; and distribution terms for this software are covered by the Eclipse
;; Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file LICENSE.txt at the root of this
;; distribution.  By using this software in any fashion, you are
;; agreeing to be bound by the terms of this license.  You must not
;; remove this notice, or any other, from this software.

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
