package com.promode.android17hardening.ui

/** Simplified status shown per item on the demo screen. */
enum class CheckStatus(val label: String) {
    UNKNOWN("Not run"),
    RUNNING("Running..."),
    PASS("Pass"),
    DENIED("Denied"),
    FAIL("Fail")
}
