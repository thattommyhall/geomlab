(ns escher.core)

(def canvas (.getElementById js/document "frame"))
(def context (.getContext canvas "2d"))

(def frame {:origin [0 0]
            :e1 [(.-width canvas) 0]
            :e2 [0 (.-height canvas)]})

(def frame1 {:origin [50 50]
             :e1 [0 300]
             :e2 [200 0]})

(defn draw-line [[x1 y1] [x2 y2] context]
  (doto context
    (.moveTo x1 y1)
    (.lineTo x2 y2))
  (set! (.-strokeStyle context) "#000" )
  (.stroke context))

(defn load-image [image-name callback]
  (let [image (js/Image.)
        canvas (.createElement js/document "canvas")
        context (.getContext canvas "2d")
        canvas-size 400]
    (set! (.-width canvas) canvas-size)
    (set! (.-height canvas) canvas-size)
    (set! (.-src image) (str "image/" image-name))
    (set! (.-onload image)
          #(do (.drawImage context image 0 0 canvas-size canvas-size)
               (callback canvas)))))

(defn scale-vec [[x y] s]
  [(* x s) (* y s)])

(defn add-vec [[x1 y1] [x2 y2]]
  [(+ x1 x2) (+ y1 y2)])

(defn sub-vec [[x1 y1] [x2 y2]]
  [(- x1 x2) (- y1 y2)])

(defn frame-coord-map
  [{:keys [origin e1 e2]}]
  (fn [[x y]]
    (add-vec origin
             (add-vec (scale-vec e1 x)
                      (scale-vec e2 y)))))

(defn segment-painter [segment-list context]
  (fn [frame]
    (let [m (frame-coord-map frame)]
      (doseq [[start end] segment-list]
        (draw-line (m start) (m end) context)))))

(defn transform-picture [p origin e1 e2]
  (fn [frame]
    (let [map (frame-coord-map frame)
          new-origin (map origin)]
      (p {:origin new-origin
          :e1 (sub-vec (map e1) new-origin)
          :e2 (sub-vec (map e2) new-origin)}))))

(defn flip-vert [p]
  (transform-picture p [0 1] [1 1] [0 0]))

(defn flip-horiz [p]
  (transform-picture p [1 0] [0 0] [1 1]))

(defn rot [p]
  (transform-picture p [1 0] [1 1] [0 0]))

(defn rot180 [p]
  (rot (rot p)))

(defn rot270 [p]
  (rot (rot (rot p))))

(defn beside [& ps]
  (let [
        division (float (/ 1 (count ps)))
        x-divisions  (take 3 (iterate #(+ division %) 0)) 
        transform-beside (fn [thing index ] (transform-picture thing [(* index division) 0] [(* (inc index) division) 0] [(* index division) 1]))
        transformed (map transform-beside ps (range))]
    (fn [frame]
      (doseq [t transformed] (t frame)))))

(defn below [p1 p2]
  (rot270 (beside (rot p2)
                  (rot p1))))

(defn image-painter [image-name context]
  (fn [{[ox oy] :origin
        [e1x e1y] :e1
        [e2x e2y] :e2}]
    (load-image image-name
                (fn [image]
                  (let [width (.-width image)
                        height (.-height image)]
                    (.save context)
                    (.translate context ox oy)
                    (.transform context
                                (/ e1x width)
                                (/ e1y height)
                                (/ e2x width)
                                (/ e2y height)
                                0
                                0)
                    (.drawImage context image 0 0)
                    (.restore context))))))

(def man (image-painter "man.png" context))
(def woman (image-painter "woman.png" context))
(def tree (image-painter "tree.png" context))

(defn path [& veclist]
  (segment-painter (partition 2 1 veclist) context))

(def p (segment-painter [[[0 0] [0.5 0]]
                         [[0.5] [0.5 0.5]]
                         [[0.5 0.5] [0 0.5]]
                         [[0 0.5] [0 0]]
                         [[0 0] [0 0.5]]]
                        context))

(def box (path [0 0] [0 1] [1 1] [1 0] [0 0]))

(defn draw [picture]
  (picture frame))

(def test-frame {:origin [100 50]
                 :e1 [200 100]
                 :e2 [100 200]})

(defn g [p1 p2]
  (below (beside p1 p2)
         (beside p2 p1)))

(defn f [p]
  (beside p (below p p)))

(defn manrow [n]
  (apply beside (repeat n man)))

;;(draw (beside man man man man))

(draw (manrow 5))
