package com.promode.android17hardening.sms

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OtpMessageParserTest {

    @Test
    fun `extracts a 6 digit code from a typical retriever message`() {
        val message = "<#> Your ExampleApp code is 123456\nFA+9qCX9VSu"
        assertEquals("123456", OtpMessageParser.extractCode(message))
    }

    @Test
    fun `extracts a 4 digit code`() {
        assertEquals("4821", OtpMessageParser.extractCode("Your code: 4821"))
    }

    @Test
    fun `returns null when no digits are present`() {
        assertNull(OtpMessageParser.extractCode("Thanks for signing up!"))
    }

    @Test
    fun `returns null for blank or null input`() {
        assertNull(OtpMessageParser.extractCode(""))
        assertNull(OtpMessageParser.extractCode(null))
        assertNull(OtpMessageParser.extractCode("   "))
    }

    @Test
    fun `ignores digit runs longer than 8 such as phone numbers`() {
        // A long digit run (e.g. an order number) shouldn't be mistaken for the OTP; this
        // documents current behavior rather than asserting the regex is exhaustive.
        assertNull(OtpMessageParser.extractCode("Order 123456789 shipped"))
    }

    @Test
    fun `picks the first matching run when multiple are present`() {
        assertEquals("55512", OtpMessageParser.extractCode("code 55512 ref 999"))
    }
}
