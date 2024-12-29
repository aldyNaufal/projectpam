package com.example.projectpam.user

data class AppUser(
    val userId: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val gender: String = "",
    val phoneNumber: String = "",
    val profilePhotoUrl: String = ""

)


val dummyUsers = listOf(
    AppUser(
        userId = "001",
        name = "Chris Evans",
        username = "chris_evans",
        email = "chris.evans@example.com",
        gender = "Male",
        phoneNumber = "+1-555-0101",
        profilePhotoUrl = "https://via.placeholder.com/150"
    ),
    AppUser(
        userId = "002",
        name = "Emma Watson",
        username = "emma_watson",
        email = "emma.watson@example.com",
        gender = "Female",
        phoneNumber = "+44-555-0202",
        profilePhotoUrl = "https://via.placeholder.com/150"
    ),
    AppUser(
        userId = "003",
        name = "Michael Jordan",
        username = "mike_jordan",
        email = "michael.jordan@example.com",
        gender = "Male",
        phoneNumber = "+1-555-0303",
        profilePhotoUrl = "https://via.placeholder.com/150"
    ),
    AppUser(
        userId = "004",
        name = "Serena Williams",
        username = "serena_tennis",
        email = "serena.williams@example.com",
        gender = "Female",
        phoneNumber = "+1-555-0404",
        profilePhotoUrl = "https://via.placeholder.com/150"
    ),
    AppUser(
        userId = "005",
        name = "Ryan Reynolds",
        username = "ryan_reynolds",
        email = "ryan.reynolds@example.com",
        gender = "Male",
        phoneNumber = "+1-555-0505",
        profilePhotoUrl = "https://via.placeholder.com/150"
    ),
    AppUser(
        userId = "006",
        name = "Scarlett Johansson",
        username = "scarlett_jo",
        email = "scarlett.johansson@example.com",
        gender = "Female",
        phoneNumber = "+1-555-0606",
        profilePhotoUrl = "https://via.placeholder.com/150"
    ),
    AppUser(
        userId = "007",
        name = "John Doe",
        username = "john_anonymous",
        email = "john.doe@example.com",
        gender = "Male",
        phoneNumber = "+1-555-0707",
        profilePhotoUrl = "https://via.placeholder.com/150"
    ),
    AppUser(
        userId = "008",
        name = "Jane Smith",
        username = "jane_smith",
        email = "jane.smith@example.com",
        gender = "Female",
        phoneNumber = "+1-555-0808",
        profilePhotoUrl = "https://via.placeholder.com/150"
    ),
    AppUser(
        userId = "009",
        name = "Robert Downey Jr.",
        username = "rdj_official",
        email = "robert.downey@example.com",
        gender = "Male",
        phoneNumber = "+1-555-0909",
        profilePhotoUrl = "https://via.placeholder.com/150"
    ),
    AppUser(
        userId = "010",
        name = "Natalie Portman",
        username = "natalie_p",
        email = "natalie.portman@example.com",
        gender = "Female",
        phoneNumber = "+1-555-1010",
        profilePhotoUrl = "https://via.placeholder.com/150"
    )
)