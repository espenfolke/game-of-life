(ns ^:figwheel-hooks life.grid
  (:require
    [clojure.set :as s]))

(defn add-cell
  [state key]
  (update-in @state [:cells] conj key))

(defn remove-cell
  [state key]
  (update-in @state [:cells] disj key))

(defn get-cell
  [state key]
  (if (empty? (s/intersection #{key} (:cells @state)))
    ^{:key key} [:span.cell
                {:on-click #(reset! state (add-cell state key))}]
    ^{:key key} [:span.cell.alive
                 {:on-click #(reset! state (remove-cell state key))}]))

(defn get-historical-cell
  [cells key]
  (if (empty? (s/intersection #{key} cells))
    ^{:key key} [:span.cell]
    ^{:key key} [:span.cell.alive]))

(defn get-cells
  [state current]
  (doall
   (for [row (range 0 (:size @state))]
     ^{:key row}
     [:div
      (doall
       (for [col (range 0 (:size @state))]
         (if current
           (get-cell state [row col])
           (get-historical-cell (:cells @state) [row col]))))])))

(defn get-grid
  ([state] (get-grid state true))
  ([state current]
   (let [round (:round @state)]
     [:div
      [:div.round
       [:a {:name round :href (str "#" round)} "Round: " round]]
      [:div (get-cells state current)]])))

(defn get-step
  [history step]
  (if (< step (count @history))
    (get-grid (nth @history step) false)
    nil))