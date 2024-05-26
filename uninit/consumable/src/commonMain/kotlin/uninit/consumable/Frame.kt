package uninit.consumable

interface Frame<T> {
    /**
     * This represents whether the frame can be averaged or taken between two
     * frames.
     */
    val takeBetween: Boolean
}