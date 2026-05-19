package com.example.domain.model.profile

enum class ProfilePostStatus(val value: String, val label: String) {
    ALL("all", "전체"),
    DRAFT("draft", "대기"),
    IN_PROGRESS("in_progress", "진행 중"),
    COMPLETED("completed", "완료"),
}
