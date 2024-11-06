package com.example.cvproject.activites.activity.dataclass

data class Notification(
    val message: NotificationData? = null
)

data class NotificationData(
    val token: String? = null,
    val data: HashMap<String, String>
)
