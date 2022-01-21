/**
* JetBrains Space Automation
* This Kotlin-script file lets you automate build activities
* For more info, see https://www.jetbrains.com/help/space/automation.html
*/

job("Build and run tests") {
    container(displayName = "Gradle build", image = "openjdk:11") {
        kotlinScript { api ->
            // here goes complex logic
            api.gradlew("test")
        }
        shellScript {
            content = """
                    ./gradlew build
                    cp -r build $mountDir/share
                """
        }
    }
    
    docker {
        beforeBuildScript {
            content = "cp -r  $mountDir/share build"
        }
        build {
            context = "docker"
            file = "./docker/Dockerfile"
        }

        push("rem-coil.registry.jetbrains.space/docker/popper") {
            tags("0.\$JB_SPACE_EXECUTION_NUMBER", "lts")
        }
    }
}
