(ns rn-compassus.android.core
  (:require [om.next :as om :refer-macros [defui]]
            [re-natal.support :as sup]
            [compassus.core :as compassus]
            [rn-compassus.state :as state]))

(set! js/window.React (js/require "react"))
(def ReactNative (js/require "react-native"))

(defn create-element [rn-comp opts & children]
      (apply js/React.createElement rn-comp (clj->js opts) children))

(def app-registry (.-AppRegistry ReactNative))
(def view (partial create-element (.-View ReactNative)))
(def text (partial create-element (.-Text ReactNative)))
(def image (partial create-element (.-Image ReactNative)))
(def touchable-highlight (partial create-element (.-TouchableHighlight ReactNative)))

(def logo-img (js/require "./images/cljs.png"))

(defn alert [title]
      (.alert (.-Alert ReactNative) title))

(defui AppRoot
       static om/IQuery
       (query [this]
              '[:app/msg])
       Object
       (render [this]
               (let [{:keys [app/msg]} (om/props this)]
                    (view {:style {:flexDirection "column" :margin 40 :alignItems "center"}}
                          (text {:style {:fontSize 30 :fontWeight "100" :marginBottom 20 :textAlign "center"}} msg)
                          (image {:source logo-img
                                  :style  {:width 80 :height 80 :marginBottom 30}})
                          (touchable-highlight {:style   {:backgroundColor "#999" :padding 10 :borderRadius 5}
                                                :onPress #(alert "HELLO!")}
                                               (text {:style {:color "white" :textAlign "center" :fontWeight "bold"}} "press me"))))))

(defn route-button
  [this route name color]
  (touchable-highlight {:style {:backgroundColor color :padding 10 :borderRadius 5}
                        :onPress #(compassus/set-route! this route)}
                       (text {:style {:color "white" :fontWeight "bold"}} name)))


(defui Wrapper
  Object
  (render [this]
    (let [{:keys [owner factory props]} (om/props this)
          route (compassus/current-route this)]
      (view {:style {:flexDirection "column" :alignItems "stretch"}}
        (text {:style {:fontSize 30 :fontWeight "100" :textAlign "center"}} "Compassus Demo")
        (text nil (str "Current Route is " (pr-str (compassus/current-route this))))
        (view {:style {:flexDirection "row" :justifyContent "space-around"}}
            (route-button this :a "A" "green")
            (route-button this :b "B" "blue")
            (route-button this :c "C" "orange"))
        (factory props)))))


(defui A
  Object
  (render [this]
          (text {:style {:fontSize 30}} "Screen A")))

(defui B
  Object
  (render [this]
          (text {:style {:fontSize 30}} "Screen B")))

(defui C
  Object
  (render [this]
          (text {:style {:fontSize 30}} "Screen C")))

(defonce RootNode (sup/root-node! 1))
(defonce app-root (om/factory RootNode))

(def app
  (compassus/application
   {:routes {:a A :b B :c C}
    :index-route :a
    :reconciler state/reconciler
    :mixins [(compassus/wrap-render Wrapper)]}))

(defn init []
      (compassus/mount! app 1)
      (.registerComponent app-registry "RnCompassus" (fn [] app-root)))
