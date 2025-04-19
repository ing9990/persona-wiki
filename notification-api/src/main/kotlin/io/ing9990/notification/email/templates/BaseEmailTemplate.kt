package io.ing9990.notification.email.templates

abstract class BaseEmailTemplate : EmailTemplates {
    protected val variables: MutableMap<String, Any> = mutableMapOf()

    init {
        variables["appName"] = "국민사형투표"
        variables["year"] =
            java.time.Year
                .now()
                .value
        variables["supportEmail"] = "vote4.korean@gmail.com"
        variables["baseUrl"] = "https://xn--3e0b39y4pd92v9pct9c.com"

        // 템플릿별 기본 변수 추가
        variables.putAll(getDefaultVariables())
    }

    /**
     * 템플릿 변수를 추가하거나 업데이트합니다.
     */
    override fun withVariable(
        key: String,
        value: Any,
    ): EmailTemplates {
        val newTemplate = this.clone()
        newTemplate.variables[key] = value
        return newTemplate
    }

    /**
     * 여러 템플릿 변수를 추가하거나 업데이트합니다.
     */
    override fun withVariables(variables: Map<String, Any>): EmailTemplates {
        val newTemplate = this.clone()
        newTemplate.variables.putAll(variables)
        return newTemplate
    }

    /**
     * 여러 템플릿 변수를 직접 현재 객체에 추가합니다.
     * 이 메서드는 불변 패턴을 따르지 않고 현재 객체를 변경합니다.
     */
    fun addVariables(newVariables: Map<String, Any>) {
        variables.putAll(newVariables)
    }

    /**
     * 템플릿의 복제본을 생성합니다 (템플릿을 불변객체처럼 사용하기 위함)
     */
    protected abstract fun clone(): BaseEmailTemplate

    /**
     * 모든 변수를 포함한 최종 변수 맵을 반환합니다.
     */
    fun getAllVariables(): Map<String, Any> = variables.toMap()
}
