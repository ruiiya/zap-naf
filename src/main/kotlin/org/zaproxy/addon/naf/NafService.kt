package org.zaproxy.addon.naf

import kotlinx.coroutines.flow.MutableStateFlow
import me.d3s34.commix.CommixDockerEngine
import me.d3s34.nuclei.NucleiEngine
import me.d3s34.sqlmap.SqlmapApiEngine
import me.d3s34.tplmap.TplmapDockerEngine
import me.d3s34.metasploit.MetasploitDockerEngine
import org.zaproxy.addon.naf.model.NafConfig

interface NafService {

    val nafConfig: MutableStateFlow<NafConfig>

    val nucleiEngine: NucleiEngine?
    val nucleiRootTemplatePath: String

    val sqlmapEngine: SqlmapApiEngine?
    val sqlmapUrl: String
    val sqlmapCSVLocation: String

    val commixDockerEngine: CommixDockerEngine?

    val tplmapDockerEngine: TplmapDockerEngine?

    val metasploitDockerEngine: MetasploitDockerEngine?

    val saveConfig: () -> Unit
}