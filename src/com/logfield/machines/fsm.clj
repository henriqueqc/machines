(ns com.logfield.machines.fsm
  (:require [clojure.spec.alpha :as spec]))

(spec/def ::transition-fn ifn?)

(spec/def ::transition (spec/keys :req-un [::transition-fn]
                                  :opt-un [::from-state
                                           ::input-sym
                                           ::to-state]))

(defn add-transition
  [fsm {:keys [from-state input-sym] :as transition}]
  (assoc-in fsm [:states from-state :transitions input-sym] transition))

(defn add-transition-helper
  [fsm from-state input-sym to-state transition-fn]
  (add-transition fsm {:from-state from-state
                       :input-sym input-sym
                       :to-state to-state
                       :transition-fn transition-fn}))

(defn set-output
  [fsm state output]
  (assoc-in fsm [:states state :output] output))

(defn set-output-helper
  [fsm state output-sym output-fn]
  (set-output fsm state {:output-sym output-sym
                         :output-fn output-fn}))

(spec/def ::dispatching-fn ifn?)

(spec/def ::fsm (spec/keys :req-un [::dispatching-fn]
                           :opt-un [::current-state
                                    ::states
                                    ::context]))

;;(spec/fdef transition-fn
;;           :args (spec/cat :fsm ::fsm :to-state :sym))

(defn ->fsm
  [initial-state context input->input-sym]
  {:current-state initial-state
   :context context
   :input->input-sym input->input-sym})

(defn machine-fn
  [{:keys [current-state states input->input-sym context]
    :as fsm}
   input]
  (let [input-sym (input->input-sym input)
        transition (get-in states [current-state :transitions input-sym])]
    (if transition
      (let [{:keys [from-state to-state transition-fn]} transition
            context (transition-fn context from-state input to-state)
            {:keys [output-sym output-fn]} (get-in states [to-state :output])
            output (when output-sym
                     {:output-sym output-sym
                      :output-data (when output-fn
                                     (output-fn context to-state output-sym))})]
        (assoc fsm
               :current-state to-state
               :context context
               :output output))
      (do
        (println "Unhandled input sym" input-sym
                 "in state" current-state)
        fsm))))
