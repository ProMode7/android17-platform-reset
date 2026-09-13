package dev.pramodpatel.handoff.data

/**
 * On-device persistence for the reader's session state, independent of any
 * cross-device transport. This is what powers the "resume where I left off"
 * flow after the app process dies or is restarted on the same device — it has
 * nothing to do with Handoff and works with no second device involved at all.
 */
interface SessionStateStore {
    fun save(state: SessionState)
    fun load(): SessionState?
    fun clear()
}
