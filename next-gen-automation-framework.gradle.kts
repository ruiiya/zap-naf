description = "Next gen automation framework addon for ZAP"

zapAddOn {
    addOnName.set("Next gen automation framework")
    zapVersion.set("2.11.1")

    manifest {
        author.set("NDBien from UET/VNU")
    }
}

crowdin {
    configuration {
        val resourcesPath = "org/zaproxy/addon/${zapAddOn.addOnId.get()}/resources/"
        tokens.put("%messagesPath%", resourcesPath)
        tokens.put("%helpPath%", resourcesPath)
    }
}

plugins {
    kotlin("jvm") version "1.6.10"
}

dependencies {
    api(kotlin("stdlib"))
}
