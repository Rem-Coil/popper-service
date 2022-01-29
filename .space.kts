/**
* JetBrains Space Automation
* This Kotlin-script file lets you automate build activities
* For more info, see https://www.jetbrains.com/help/space/automation.html
*/

job("Build and run tests") {
    
    startOn {
        gitPush {
            branchFilter {
                +"release"
            }
        }
    }
    
    git("web/release") {
    	container(displayName = "Web build", image = "cirrusci/flutter:2.8.1") {
        shellScript {
        		content = """
            		flutter build web
                """
        	}
        }
    }
    
    container(displayName = "Gradle build", image = "gradle:6.9.2-jdk17-alpine") {
        shellScript {
            content = """
            		./gradlew test
                    ./gradlew installDist
                    cp -r build $mountDir/share
                """
        }
    }
    
    docker {
        beforeBuildScript {
            content = "cp -r $mountDir/share build"
        }
        build {
            context = "docker"
            file = "./docker/Dockerfile"
        }

        push("rem-coil.registry.jetbrains.space/p/popper/popper/main-service") {
            tags("0.0.\$JB_SPACE_EXECUTION_NUMBER", "latest")
        }
    }
}
