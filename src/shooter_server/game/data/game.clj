(ns shooter-server.game.data.game
  (:require [ring.util.response :as response]
            [com.rpl.specter :refer [select transform]])
  (:use [clojure.data.json :only [write-str read-str]]))


(defrecord Game [players])

(defrecord Player [name socket])

(extend-protocol compojure.response/Renderable
  Game
  (render [game _]
    (let [body (write-str game :value-fn (fn [n v] (if (not= n :socket) v nil)))]
      (-> (response/response body)
          (response/content-type "text/html; charset=utf-8")))))

(def game (atom (->Game {})))

(defn state [] @game)

(defn player-join-game [player]
  (let [player-name (:name player)]
    (swap! game assoc-in [:players player-name] player)))

(defn player-leave-game [player]
  (let [player-name (:name player)]
    (swap! game update :players dissoc player-name)))