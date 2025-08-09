package site.praytogether.praytogetherapi.modules.member.presentation.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateMemberRequest(
    @field:NotBlank(message = "Name is required")
    @field:Size(max = 20, message = "Name must be less than 20 characters")
    val name: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email format")
    @field:Size(max = 50, message = "Email must be less than 50 characters")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    val password: String
)

data class UpdateMemberRequest(
    @field:Size(max = 20, message = "Name must be less than 20 characters")
    val name: String? = null,

    @field:Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    val password: String? = null
)