package com.javadEsl.pixel.helper.extensions


/**
 * add protocol to url and normalize it
 */
val String.normalized: String
    get() {
        if (!this.startsWith("https://") && !this.startsWith("http://")) {
            return "http://$this"
        }
        return this
    }


