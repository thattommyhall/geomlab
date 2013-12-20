(ns escher.core)

(def canvas (.getElementById js/document "frame"))
(def context (.getContext canvas "2d"))

(def frame {:origin [0 0]
            :e1 [(.-width canvas) 0]
            :e2 [0 (.-height canvas)]})

(defn draw-line [[x1 y1] [x2 y2] context]
  (doto context
    (.moveTo x1 y1)
    (.lineTo x2 y2))
  (set! (.-strokeStyle context) "#000" )
  (.stroke context))

(defn load-image [image-name callback]
  (let [image (js/Image.)]
    (set! (.-src image) (str "image/" image-name))
    (set! (.-onload image) #(callback image))))

(defn image-painter [image-name context]
  (fn [{:keys [origin e1 e2]}]
    (let [[ox oy] origin
          [e1x e1y] e1
          [e2x e2y] e2]
      (load-image image-name
                  (fn [image]
                    (let [width (.-width image)
                          height (.-height image)]
                      (doto context
                        (.save)
                        (.translate ox oy)
                        (.transform (/ e1x width)
                                    (/ e1y height)
                                    (/ e2x width)
                                    (/ e2y height)
                                    0 0)
                        (.drawImage image 0 0)
                        (.restore))))))))

((image-painter "man.png" context) frame)
(draw-line [0 100] [100 0] context)
(defn foo [a b]
  (+ a b))

(. js/console (log "Hello world!" (foo 1 2)))
