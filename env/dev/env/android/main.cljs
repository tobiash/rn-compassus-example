(ns ^:figwheel-no-load env.android.main
  (:require [om.next :as om]
            [compassus.core :as compassus]
            [rn-compassus.android.core :as core]
            [rn-compassus.state :as state]
            [figwheel.client :as figwheel :include-macros true]))

(enable-console-print!)

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3449/figwheel-ws"
  :heads-up-display false
  :jsload-callback #(compassus/mount! core/app 1))

(core/init)

(def root-el (core/app-root))
