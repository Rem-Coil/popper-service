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
    
    git("web") {
        refSpec = "release"
    	container(displayName = "Web build", image = "cirrusci/flutter:2.8.1") {
        shellScript {
        		content = """
                	cd /mnt/space/work/web
            		flutter build web
                    cp -r build/web $mountDir/share
                    ls $mountDir/share
                """
        	}
        }
    }
    
    container(displayName = "Gradle build", image = "gradle:6.9.2-jdk17-alpine") {
        shellScript {
            content = """
            		
                    cp -r -f $mountDir/share/web src/main/resources
                    ls src/main/resources/web
                    pwd
                    ls src/main/resources
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
