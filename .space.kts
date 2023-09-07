import circlet.pipelines.script.ScriptApi
import io.ktor.client.request.*

job("Deploy on server (dev)") {
    parameters {
        text("webhook-url", value = "{{ project:popper-dev-webhook }}")
        text("version", value = "{{ project:system-version }}")
    }

    startOn {
        gitPush {
            anyBranchMatching {
                +"release"
                +"develop"
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
                +"$spaceRepo:{{ version }}-dev-${"$"}JB_SPACE_EXECUTION_NUMBER"
                +"$spaceRepo:dev"
            }
        }

        kotlinScript(displayName = "Deploy on server") { api ->
            try {
                val url = api.parameters["webhook-url"]

                if (url == null) {
                    api.scheduleDeployment(
                        "Произошла проблема при деплое. Не указан вебхук (popper-dev-webhook). Произведите деплой вручную"
                    )

                    api.sendNotification(
                        """
                            :warning: :warning: Произошла проблема при деплое бека. 
                            Не указан вебхук (popper-dev-webhook). Произведите деплой вручную !!!
                            Версия деплоя: **${api.currentVersion}**
                        """.trimIndent()
                    )

                    return@kotlinScript
                }

                io.ktor.client.HttpClient { expectSuccess = true }.post(url)
                api.finishDeployment()

                api.sendNotification(
                    """
                        :zap: :zap: Новая релизная версия бека выложена !
                        Возможны ошибки при запросах, необходимо поправить если что-либо поменялось!
                        Текущая версия: **${api.currentVersion}**
                    """.trimIndent()
                )

            } catch (e: Exception) {
                api.scheduleDeployment(
                    "Произошла проблема при деплое. Проверьте корректность деплоя и повторите попытку вручную."
                )

                api.sendNotification(
                    """
                        :warning: :warning: Произошла проблема при деплое бека. 
                        Проверьте корректность деплоя и повторите попытку вручную
                        Версия деплоя: **${api.currentVersion}**
                    """.trimIndent()
                )
            }
        }
    }
}

job("Build server (prod)") {
    parameters {
        text("version", value = "{{ project:system-version }}")
    }

    host(displayName = "Build and push docker image") {
        kotlinScript(displayName = "Start deployment") { api ->
            api.space().projects.automation.deployments.start(
                project = api.projectIdentifier(),
                targetIdentifier = TargetIdentifier.Key("popper"),
                version = api.parameters["version"],
                syncWithAutomationJob = true,
            )
        }

        dockerBuildPush {
            labels["vendor"] = "remcoil"

            val spaceRepo = "pampero.registry.jetbrains.space/p/popper/popper/popper_server"
            tags {
                +"$spaceRepo:{{ version }}"
                +"$spaceRepo:latest"
            }
        }

        kotlinScript(displayName = "Send notification") { api ->
            api.sendNotification(
                """
                    :zap: :zap: Новая релизная версия бека собрана !!!
                    Текущая версия: **${api.releaseVersion}**
                """.trimIndent()
            )
        }
    }
}

val ScriptApi.currentVersion: String
    get() = "${parameters["version"]}-dev-${executionNumber()}"

val ScriptApi.releaseVersion: String
    get() = "${parameters["version"]}"

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

suspend fun ScriptApi.sendNotification(text: String) {
    space().chats.messages.sendMessage(
        channel = ChannelIdentifier.Channel(ChatChannel.FromName("Deployment Notifications")),
        content = ChatMessage.Text(text)
    )
}