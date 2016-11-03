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
(def navigator (partial create-element (.-Navigator ReactNative)))
(def touchable-highlight (partial create-element (.-TouchableHighlight ReactNative)))

(def logo-img (js/require "./images/cljs.png"))

(defn alert [title]
      (.alert (.-Alert ReactNative) title))

(defn route->js [route] (clj->js {:name (name route) :ns (namespace route)}))

(defn js->route [js]
  (let [obj (js->clj js :keywordize-keys true)
        rns (:ns obj)
        rn (:name obj)]
    (keyword rns rn)))

(defn route-button
  [navigator route name color]
  (touchable-highlight {:style {:backgroundColor color :padding 10 :borderRadius 5}
                        :onPress #(.push navigator (route->js route))}
                       (text {:style {:color "white" :fontWeight "bold"}} name)))


(defui Wrapper
  Object
  (render [this]
    (let [{:keys [owner factory props]} (om/props this)
          route (compassus/current-route this)]
      (navigator
       {:initialRoute (route->js route)
        :configureScene
          (fn [_ _] ReactNative.Navigator.SceneConfigs.FloatFromBottomAndroid)
        :onWillFocus
          (fn [ js-route]
            (let [focused-route (js->route js-route)]
              (if-not (= route focused-route)
                (compassus/set-route! this focused-route))))
        :renderScene
          (fn [_ navigator]
            (view {:style {:flexDirection "column"
                           :alignItems "stretch"
                           :flex 1
                           :backgroundColor "white"}}
              (text {:style {:fontSize 30 :fontWeight "100" :textAlign "center"}} "Compassus Demo")
              (text nil (str "Current Route is " (pr-str (compassus/current-route this))))
              (view {:style {:flexDirection "row" :justifyContent "space-around"}}
                  (route-button navigator :a "A" "green")
                  (route-button navigator :b "B" "blue")
                  (route-button navigator :c "C" "orange"))
              (factory props)))}))))


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
