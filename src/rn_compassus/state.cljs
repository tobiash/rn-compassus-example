(ns rn-compassus.state
  (:require [om.next :as om]
            [compassus.core :as compassus]
            [re-natal.support :as sup]))

(defonce app-state (atom {:app/msg "Hello Clojure in iOS and Android!"}))

(defmulti read om/dispatch)
(defmethod read :default
           [{:keys [state]} k _]
           (let [st @state]
                (if-let [[_ v] (find st k)]
                        {:value v}
                        {:value :not-found})))

(defonce reconciler
         (om/reconciler
           {:state        app-state
            :parser       (compassus/parser {:read read})
            :root-render  sup/root-render
            :root-unmount sup/root-unmount}))
