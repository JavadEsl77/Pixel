package com.javadEsl.pixel

import androidx.fragment.app.Fragment
import com.tapadoo.alerter.Alerter

/**
 * show short alert with [Alerter] library
 *
 * @param message
 */
fun Fragment.alert(message: String) =
    requireActivity().alert(message = message)