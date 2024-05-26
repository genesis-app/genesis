package uninit.consumable

/**
 * A consumable is a type of object that can be used to provide a stream of frames.
 *
 * The frames can be used to represent a stream of data, e.g. a stream of audio
 * samples.
 *
 * It can be walked back, allowing for seeking of a specific frame based not
 * on frame index but on time.
 */
abstract class BaseConsumable<
        /**
         * The type of the frame that the consumable provides.
         */
        FrameType : Frame<T>,
        /**
         * The type of the value that is stored in a frame. Non-nullable.
         */
        T : Any,
        /**
         * The type of the value used to represent time, e.g. a number of milliseconds.
         */
        DeltaType : Number
        > : Iterator<FrameType> {

    /**
     * The current frame index.
     */
    abstract val frameIndex: Long

    /**
     * The current time.
     */
    abstract val time: DeltaType

    /**
     * Set the methods of the iterator to start at [time].
     *
     * @param time The time to seek to.
     */
    abstract fun seekTo(time: DeltaType)

    /**
     * Set the methods of the iterator to start at [frameIndex].
     *
     * @param frameIndex The frame index to seek to.
     */
    abstract fun seekTo(frameIndex: Long)

    /**
     * Look back in time by [time] and return the frame at that time.
     *
     * @param time The time to look back by.
     * @return The frame at that time, or null if there is no frame at that time.
     */
    abstract fun lookBack(time: DeltaType): FrameType?

    /**
     * Look back in time by [frameIndex] and return the frame at that time.
     *
     * @param frameIndex The frame index to look back by.
     * @return The frame at that time, or null if there is no frame at that time.
     */
    abstract fun lookBack(frameIndex: Long): FrameType?

}