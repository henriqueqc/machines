(ns com.logfield.machines.fsm.api)

(defn add-transition
  [fsm state input-symbol to-state transition-context]
  (-> fsm
   (assoc-in [:definition state :transitions input-symbol]
             to-state)
   (assoc-in [:environment state :transitions input-symbol]
             transition-context)))

(defn set-output
  [fsm state output-symbol ->output]
  (-> fsm
   (assoc-in [:definition state :output-symbol] output-symbol)
   (assoc-in [:environment state :->output] ->output)))

(defn- ->context
  [context {:keys [:environment :state :input :input-symbol]
    :as args}]
  (let [f (get-in environment [state :transitions input-symbol])]
    (when f
      (f context input))))

(defn- ->output
  [{:keys [:environment :state :context]
    :as args}]
  (let [f (get-in environment [state :->output])]
    (when f
      (f context))))

(defn ->fsm
  [{:keys [:initial-state :->input-symbol]}]
  {:definition {:initial-state initial-state}
   :state initial-state
   :->input-symbol ->input-symbol
   :transition-context ->context
   :->output ->output})
