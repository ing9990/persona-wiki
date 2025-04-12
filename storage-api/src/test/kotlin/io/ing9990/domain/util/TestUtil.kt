package io.ing9990.domain.util

class TestUtil {
    fun String.normalize(): String = java.text.Normalizer.normalize(this, java.text.Normalizer.Form.NFC)
}
