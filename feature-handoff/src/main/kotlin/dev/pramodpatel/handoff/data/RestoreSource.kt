package dev.pramodpatel.handoff.data

/** Where the session state the reader opened with came from, if anywhere. */
enum class RestoreSource {
    /** Fresh start: no local save, no incoming handoff extras. */
    NONE,

    /** Real on-device persistence: restored from [SessionStateStore] after a
     *  process death or app restart on this same device. */
    LOCAL_STORE,

    /** Restored from Intent extras shaped like a real Continue On handoff, via
     *  this sample's local "simulate incoming handoff" button. See README for
     *  what this does and does not prove. */
    SIMULATED_HANDOFF
}
