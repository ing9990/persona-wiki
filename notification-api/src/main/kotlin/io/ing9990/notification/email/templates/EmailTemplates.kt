package io.ing9990.notification.email.templates

interface EmailTemplates {
    fun getTemplateName(): String

    fun getDefaultVariables(): Map<String, Any>

    fun withVariable(
        key: String,
        value: Any,
    ): EmailTemplates

    fun withVariables(variables: Map<String, Any>): EmailTemplates
}
