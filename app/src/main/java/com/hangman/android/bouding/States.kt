package com.hangman.android.bounding

/**
 * Describes a state for a data that will need to be fetched or loaded. This is not an action.
 */
sealed interface DataState {
    /**
     * The Data is currently being fetched
     */
    object FETCHING: DataState

    /**
     * The requested data is not available. This is not an error but a way to represent a data
     * that does not exist
     */
    object NOTAVAILABLE: DataState

    /**
     * An error occured while fetching the requested data.
     *
     * @param error The actual error that was thrown.
     */
    data class ERROR(val error: Throwable): DataState

    /**
     * The data has been fetched, and can be retrieved into this state instance.
     *
     * @param data The data that has been fetched.
     */
    data class DATA<T>(val data: T): DataState
}

/**
 * Describes the state of a specific request. This request will represent an action and not the load
 * of some data. For exemple a save. As this is not a request for data, no data will be accessible
 * into this state.
 */
sealed interface RequestState {
    /**
     * The request has not been fired yet.
     */
    object IDLE: RequestState

    /**
     * The request is currently being ran
     */
    object RUNNING: RequestState

    /**
     * An error occured while running this request
     */
    data class ERROR(val error: Throwable): RequestState

    /**
     * The request has successfully finished.
     */
    object SUCCESS: RequestState
}
