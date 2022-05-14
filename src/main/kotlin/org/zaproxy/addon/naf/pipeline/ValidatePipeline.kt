package org.zaproxy.addon.naf.pipeline

import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import me.d3s34.commix.CommixValidateRequest
import me.d3s34.rfi.RfiExploiter
import me.d3s34.sqlmap.restapi.request.StartTaskRequest
import me.d3s34.sqlmap.transformParam
import me.d3s34.tplmap.TplmapRequest
import org.zaproxy.addon.naf.NafService
import org.zaproxy.addon.naf.NafState
import org.zaproxy.addon.naf.database.NafDatabase
import org.zaproxy.addon.naf.model.NafScanContext
import org.zaproxy.addon.naf.model.RfiRequest
import org.zaproxy.addon.naf.model.mapToIssue
import org.zaproxy.addon.naf.model.toNafAlert
import org.zaproxy.zap.extension.alert.ExtensionAlert
import java.net.URL
import kotlin.coroutines.CoroutineContext

class ValidatePipeline(
    val nafState: NafState,
    val nafService: NafService,
    override val coroutineContext: CoroutineContext
): NafPipeline<Unit>(NafPhase.ATTACK) {

    private val rfiExploiter = RfiExploiter()

    private val extensionAlert by lazy {
        extensionLoader
            .getExtension(ExtensionAlert::class.java)
    }

    private val nafDatabase by lazy {
        NafDatabase()
    }

    override suspend fun start(nafScanContext: NafScanContext) {

        val alerts = extensionAlert.allAlerts
        val nafAlerts = nafState.alerts.value

        alerts.forEach { alert ->
            kotlin.runCatching<Unit> {
                val historyReference = alert.historyRef
                val isValid: Boolean = when (alert.cweId) {
                    89 -> {
                        val sqlmapEngine = nafService.sqlmapEngine
                        sqlmapEngine?.verifySqlInjection(
                            StartTaskRequest(
                                url = alert.uri.toString(),
                                data = alert.postData,
                                cookie = historyReference.httpMessage.cookieParamsAsString,
                            ).transformParam(alert.param)
                        ) ?: false
                    }
                    94, 78, 97 -> {
                        val commixEngine = nafService.commixDockerEngine

                        val commInjectable = commixEngine?.validate(CommixValidateRequest(
                            url = alert.uri.toString(),
                            data = alert.postData,
                            cookies = historyReference.httpMessage.cookieParamsAsString
                        )) ?: false


                        if (commInjectable) {
                            true
                        } else {
                            val tplmapDockerEngine = nafService.tplmapDockerEngine
                            tplmapDockerEngine?.validate(
                                TplmapRequest(
                                    url = alert.uri.toString(),
                                    data = alert.postData,
                                    cookies = historyReference.httpMessage.cookieParamsAsString
                                )
                            ) ?: false
                        }
                    }
                    98 -> {
                        rfiExploiter.validate(
                            RfiRequest(
                                URL(historyReference.uri.toString()),
                                alert.param,
                                data = alert.postData,
                                cookie = historyReference.httpMessage.cookieParamsAsString,
                                remoteFileInclude = RfiExploiter.nessusRfiCheck
                            )
                        )
                    }
                    22 -> {
                        // Path traversal, LFI
                        // No need validate

                        val vectorAttack = alert.attack

                        vectorAttack.contains("etc") && vectorAttack.contains("passwd") ||
                                vectorAttack.contains("Windows", true) && vectorAttack.contains("system.ini")
                    }
                    else -> false
                }



                if (isValid) {
                    val nafAlert = nafState.alerts.updateAndGet { nafAlerts ->
                        nafAlerts.map { if (it.id == alert.alertId.toString()) it.copy(verified = true) else it }
                    }.firstOrNull { it.id == alert.alertId.toString() }

                    if (nafAlert != null) {
                        nafDatabase
                            .issueService
                            .saveNewIssue(nafAlert.mapToIssue().copy())
                    } else {

                        val newNafAlert = alert.toNafAlert().copy(verified = true)

                        nafState.alerts.update {
                            it + newNafAlert
                        }

                        nafDatabase
                            .issueService
                            .saveNewIssue(newNafAlert.mapToIssue())
                    }
                }
            }.onFailure {
                println(it.stackTrace.toString())
            }
        }
    }



}