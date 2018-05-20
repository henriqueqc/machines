(ns com.logfield.machines.fsm2
  (:require [clojure.spec.alpha :as spec]
            [logfield.machines.transition :as transition]))

(defn transition
  [{:keys [::definition ::state] :as fsm}
   input-symbol]
  (let [new-state (get-in definition [state ::transitions input-symbol])
        output-symbol (get-in definition [new-state ::output-symbol])]
    (assoc fsm
           ::state new-state
           ::output output-symbol)))
