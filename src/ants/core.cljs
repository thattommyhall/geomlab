(ns escher.core)

(def canvas (.getElementById js/document "frame"))
(def context (.getContext canvas "2d"))

(def frame {:origin [0 0]
            :e1 [(.-width canvas) 0]
            :e2 [0 (.-height canvas)]})

(defn new-canvas
  ([width height]
     (let [canvas
           (.createElement js/document "canvas")]
       (set! (.-width canvas) width)
       (set! (.-height canvas) height)
       canvas))
  ([] (new-canvas 0 0)))

(defn get-context [canvas]
  (.getContext canvas "2d"))

(defn image [image-name]
  (let [image (js/Image.)]
    (set! (.-src image) (str "image/" image-name))
    image))

(def PI (.-PI js/Math))

(defn to-rad [degree]
  (* PI (/ degree 180)))

(defn rot [im]
  (let [width (.-width im)
        height (.-height im)
        canvas (new-canvas height width)
        context (get-context canvas)]
    (.translate context height 0)
    (.rotate context (to-rad 90))
    (.drawImage context im 0 0 width height)
    canvas))

(defn rot180 [p]
  (rot (rot p)))

(defn rot270 [p]
  (rot (rot (rot p))))

(defn beside [left right]
  (let [canvas (.createElement js/document "canvas")
        context (.getContext canvas "2d")
        l-height (.-height left)
        l-width (.-width left)
        r-height (.-height right)
        r-width (.-width right)
        biggest (max l-height r-height)
        smallest (min l-height r-height)
        multiple (/ biggest smallest)
        new-l-height (if (= l-height smallest) (* l-height multiple) l-height)
        new-l-width (if (= l-height smallest) (* l-width multiple) l-width)
        new-r-height (if (= r-height smallest) (* r-height multiple) r-height)
        new-r-width (if (= r-height smallest) (* r-width multiple) r-width)
        canvas-width (+ new-l-width new-r-width)
        canvas-height biggest]
    (set! (.-width canvas) canvas-width)
    (set! (.-height canvas) canvas-height)
    (doto context
      (.drawImage left 0 0 new-l-width new-l-height)
      (.drawImage right new-l-width 0 new-r-width new-r-height))
    canvas))

(defn below [p1 p2]
  (rot270 (beside (rot p2)
                  (rot p1))))

(def man (image "man.png"))
(def woman (image "woman.png"))
(def tree (image "tree.png"))

(defn draw [picture]
  (let [pic-width (.-width picture)
        pic-height (.-height picture)
        canvas-width (.-width canvas)
        canvas-height (.-height canvas)
        width-* (/ canvas-width pic-width)
        height-* (/ canvas-height pic-height)
        multiple (min width-* height-*)]
    (.drawImage context picture 0 0 (* pic-width multiple) (* pic-height multiple))))

(defn manstack [n]
  (if (= n 0)
    man
    (below man (manstack (- n 1)))))

(defn manrow [n]
  (if (= n 0)
    man
    (beside man (manrow (- n 1)))))

(defn log [o]
  (.log js/console o))

(defn left [] (below (beside (below man
                                    woman)
                             man)
                     (beside woman tree)))

(defn right [] (beside (below (below man woman)
                              woman)
                       (below man tree)))

(js/setTimeout
 #(draw (below (beside ( beside (left) (right))
                        (manstack 3))
               (manrow 11)))
 500)
