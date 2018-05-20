(ns com.logfield.machines.fsm.core)

(defn transition
  [{:keys [:definition :environment :state :context :->input-symbol
           :transition-context :->output]
    :or {->input-symbol :input
         transition-context (constantly nil)
         ->output (constantly nil)}
    :as fsm}
   input]
  (let [input-symbol (->input-symbol {:environment environment
                                      :state state
                                      :context context
                                      :input input})
        to-state (get-in definition [state :transitions input-symbol])
        context (transition-context context {:environment environment
                                             :state state
                                             :to-state to-state
                                             :input-symbol input-symbol
                                             :input input})
        output-symbol (get-in definition [to-state :output-symbol])
        output (when output-symbol
                 (->output {:environment environment
                            :state to-state
                            :context context
                            :output-symbol output-symbol}))]
    (assoc fsm
           :state to-state
           :context context
           :output-symbol output-symbol
           :output output)))
