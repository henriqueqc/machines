(ns com.logfield.machines.runner
  (:require
    [clojure.core.async :as async]))

(defn start!
  ([transition-fn initial-state]
   (start! transition-fn initial-state nil))
  ([transition-fn initial-state transition-callback]
   (let [input-chan (async/chan)
         running-chan (async/go-loop
                          [message(async/<! input-chan)
                           state initial-state]
                        (when message
                          (let [new-state (transition-fn state message)
                                ;; TODO: add spec for a transition
                                transition {:transition-fn transition-fn
                                            :from-state state
                                            :message message
                                            :to-state new-state}]
                            (when transition-callback
                              (transition-callback transition))
                            (recur (async/<! input-chan)
                                   new-state))))]
     ;; TODO: give this a name and add a spec
     {:transition-fn transition-fn
      :initial-state initial-state
      :transition-callback transition-callback
      :input-chan input-chan
      :running-chan running-chan})))

(defn stop!
  [{input-chan :input-chan :as runner}]
  (async/close! input-chan))

(defn send!
  ([{input-chan :input-chan :as runner} message]
   (async/>!! input-chan message)))
