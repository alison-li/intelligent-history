package com.github.alisonli.historyplugin.services

import com.intellij.openapi.project.Project
import com.github.alisonli.historyplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
