import circlet.pipelines.script.ScriptApi
import io.ktor.client.request.*

job("Deploy on server") {
    parameters {
        text("webhook-url", value = "{{ project:popper-dev-webhook }}")
        text("major-version", value = "{{ project:popper-major-version }}")
    }

    startOn {
        gitPush {
            branchFilter {
                +Regex("release")
            }
        }
    }

    host(displayName = "Build and push docker image") {
        kotlinScript(displayName = "Start deployment") { api ->
            api.startDeployment()
        }

        dockerBuildPush {
            labels["vendor"] = "remcoil"

            val spaceRepo = "pampero.registry.jetbrains.space/p/popper/popper/popper_server"
            tags {
                +"$spaceRepo:{{ major-version }}.${"$"}JB_SPACE_EXECUTION_NUMBER"
                +"$spaceRepo:latest"
            }
        }

        kotlinScript(displayName = "Deploy on server") { api ->
            try {
                val url = api.parameters["webhook-url"]

                if (url == null) {
                    api.scheduleDeployment(
                        "Произошла проблема при деплое. Не указан вебхук (popper-dev-webhook). Произведите деплой вручную"
                    )
                    return@kotlinScript
                }

                io.ktor.client.HttpClient { expectSuccess = true }.post(url)
                api.finishDeployment()

            } catch (e: Exception) {
                api.scheduleDeployment(
                    "Произошла проблема при деплое. Проверьте корректность деплоя и повторите попытку вручную."
                )
            }
        }
    }
}

val ScriptApi.currentVersion: String
    get() = "${parameters["major-version"]}.${executionNumber()}"

suspend fun ScriptApi.startDeployment() {
    space().projects.automation.deployments.start(
        project = projectIdentifier(),
        targetIdentifier = TargetIdentifier.Key("popper"),
        version = currentVersion,
        syncWithAutomationJob = false,
    )
}

suspend fun ScriptApi.scheduleDeployment(reason: String) {
    val target = TargetIdentifier.Key("popper")

    space().projects.automation.deployments.delete(
        project = projectIdentifier(),
        targetIdentifier = target,
        deploymentIdentifier = DeploymentIdentifier.Version(currentVersion)
    )

    space().projects.automation.deployments.schedule(
        project = projectIdentifier(),
        targetIdentifier = target,
        version = currentVersion,
        description = reason
    )
}

suspend fun ScriptApi.finishDeployment() {
    space().projects.automation.deployments.finish(
        project = projectIdentifier(),
        targetIdentifier = TargetIdentifier.Key("popper"),
        version = currentVersion
    )
}