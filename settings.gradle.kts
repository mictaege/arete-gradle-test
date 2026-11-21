rootProject.name = "arete-gradle-test"

includeBuild("../arete")
includeBuild("../arete-gradle")
includeBuild("../arete-ktx")

include("app")
include("java-specs")
include("kotlin-specs")
