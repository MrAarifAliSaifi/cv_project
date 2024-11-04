package com.example.cvproject.activites.activity.dataclass

import com.google.auth.oauth2.GoogleCredentials
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

object AccessToken {

    private val firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging"

    fun getAccessToken(): String? {
        try {
            val jsonString = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"cvprojectblinkit\",\n" +
                    "  \"private_key_id\": \"bd442884b4a728439fbef711020715faa1ef2c6f\",\n" +
                    "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvwIBADANBgkqhkiG9w0BAQEFAASCBKkwggSlAgEAAoIBAQCNwGq43QqGZDrv\\n4rTJLhWORcs6H4p1SP0l9BQQnYn5X5wpxpQzg1ROazkJuU8mJCTEjPSh8ONvY+TV\\nOCspCANYC6DayIx8FDE1p3r1NAvUIFJ+yCqFakHBFWR2IKrGNOq84ouDhiKnCh+N\\nA8bOHEH9dZW99RprqO3W8cznyznWRjtuCEehqMP6R0zfJ3dEvRA0IflbAsKT2wwW\\n+kH8uORTPTWr01PwHqDXKHSWHnPW6mf5YX8+FtD/QL0OuqU2rg/N8900aEKo5x4C\\nNO+17PxIVb/kgMTwwv7jjdQ02zA//XI0DfP/gkA59N2VnytT/wCuAbxXL11XmUT/\\nLUxZ7CifAgMBAAECggEAC2A7UF2w6AsoLbNAdfrEURgp0J7SYko6wq59Y7344jH5\\nmuKsZxvtiqORPsYH45Q3/Swgv9RQtFJMfY4V7KOPfSPlw1kf6R9HuboYpJAKOPpl\\nFHak7vMRltjMKKzDd8esZP87WhFMO6KU2KDKdiUPaLNkkouWlLhIah0aqCmHmToI\\nGSxMmaeWpm2gohlKLVKFx7J88pJLKFNxGfTQKiNb3Sspy340Fcw6zx9X/xDvvJgW\\ndAvCMuU3nO/4jCjJAwC4T75MnRn8bBbN4Ve6tn1WS1i3cblV/guA+ntRwMRoUD+S\\ncBAszAkTr3kCYSObd8JlBgHeT9JBosJdJmhZPULYiQKBgQDEKHStiWVp+Kfp0kX+\\nmFhtDtanyuoeoabUoiKWKN5OgrZMFnsQdgO4c0NWIBdFe0l0wfHHLzjX28e9/EFY\\nEK7y1YdE5NA1Yq9xXa8Urd/PDTmffbxYBctM6xzsfOQHtdTMm6GEoAoAgfTZ5SoA\\nWmlwSJnafFy5G7KPelPJBf736QKBgQC4/u52cYWOuCLNoo54FXhemiveRqj4tKzo\\nK/UjJ2CC2tH4IRZhN3SPTMq1KNvlMx3FlA1pB1OkSuT0pk3stig6LS5lksJMsVAu\\n4v2KtX+L3kOWPmDzML3emuZX+we6VaUxsknJPSkq4knDMHI3yFt7ftfXQkzQn669\\nExMcf8DPRwKBgQCV+MBMNXHd9YibknKFVoC00NV48ADTbpAYneWHnLNqnzE1NzLb\\nwP7TNPzUijCApOPiaq2ptgblslkifgyPgiHf8zU6jR5K0qqEKDZFvzIQIXJe6Ejp\\nm3R12IQCovHMm06ZKJvyxFEhVjqXpUfZgUZ/3GIU02Qo+m4omEQau4TWkQKBgQCm\\nQKzrku/1J/RXG1yv/JOVaTY9ZwEZqb2uVTrToeKxFpgWRx4GtkCxO9D8Z8DexUZf\\nNsOqfOQlRQ1n9EEZl0alqc8Fh5PIOp2V1XcH/j6m41OYJW0ZgNdRw6F4tefnBGsW\\nM2TuTmG94Wlq2hH7obrdTEwLbtD7uFXgI9FMDl+npwKBgQCgdkOjGHgKy73DkcDp\\nm0MXvYvDbNQoPKr2IN+9p2HyacxX4+Rx+S9MHfOVpoM45TqmP/NTPBiDScmp1GXa\\nDydM7beQ5duH9A4C5VW10UrIieyVe+s0+qSo3wDkSsPIUksAyfF5LH9XkYlwNTDp\\n/pEwETqv1+GTcqel0nUHHZeW6w==\\n-----END PRIVATE KEY-----\\n\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-w3fe5@cvprojectblinkit.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"108409327010216405523\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-w3fe5%40cvprojectblinkit.iam.gserviceaccount.com\",\n" +
                    "  \"universe_domain\": \"googleapis.com\"\n" +
                    "}"

            val stream = ByteArrayInputStream(jsonString.toByteArray(StandardCharsets.UTF_8))
            val googleCredential = GoogleCredentials.fromStream(stream)
                .createScoped(arrayListOf(firebaseMessagingScope))
            googleCredential.refresh()
            return googleCredential.accessToken.tokenValue
        } catch (e: IOException) {
            return null
        }
    }
}