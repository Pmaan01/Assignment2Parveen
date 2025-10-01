package com.android_assignments.assignment_2_parveen.util

open class Event<out T>(private val content: T) {
    private var handled = false
    fun getIfNotHandled(): T? = if (handled) null else { handled = true; content }
}
