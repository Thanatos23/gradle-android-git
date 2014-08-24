package com.github.ksoichiro.gag

import org.gradle.api.Plugin
import org.gradle.api.Project


class GagPlugin implements Plugin<Project> {
    void apply(Project target) {
        target.extensions.create("git", GagPluginExtension, target)
        target.git.extensions.create("gitDependencies", Dependencies, target)

        target.task('update', type: UpdateTask)

        target.task('listConfig') << {
            target.git.gitDependencies.repos.each() { repo ->
                println "dependency:"
                println "  location: ${repo.location}"
                println "  name: ${repo.name}"
                println "  libraryProject: ${repo.libraryProject}"
                println "  groupId: ${repo.groupId}"
                println "  artifactId: ${repo.artifactId}"
                println "  commit: ${repo.commit}"
                println "  tag: ${repo.tag}"
                println "  gradleVersion: ${repo.gradleVersion}"
            }
        }
    }
}

class GagPluginExtension {
    String directory = ".gag"

    Project project

    GagPluginExtension(Project project) {
        this.project = project
    }
}

class Dependencies {
    Project project
    List<Repo> repos = []

    Dependencies(Project project) {
        this.project = project
    }

    void repo(Map<String, ?> map) {
        def r = new Repo()
        project.configure(r) {
            location = map["location"]
            name = map["name"]
            libraryProject = map["libraryProject"]
            groupId = map["groupId"]
            artifactId = map["artifactId"]
            commit = map["commit"]
            tag = map["tag"]
            gradleVersion = map["gradleVersion"]
        }
        r.resolveVersion()
        repos.add(r)
    }

    void repo(Closure closure) {
        def r = new Repo()
        project.configure(r, closure)
        r.resolveVersion()
        repos.add(r)
    }
}

class Repo {
    String location
    String name
    String libraryProject
    String groupId
    String artifactId
    String commit
    String tag
    String gradleVersion
    String resolvedVersion

    void resolveVersion() {
        if (commit == null) {
            resolvedVersion = tag
        } else {
            resolvedVersion = commit
        }
    }
}
