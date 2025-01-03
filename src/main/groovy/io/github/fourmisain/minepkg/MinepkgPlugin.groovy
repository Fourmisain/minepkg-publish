package io.github.fourmisain.minepkg


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.AbstractArchiveTask

class MinepkgPlugin implements Plugin<Project> {
	void apply(Project project) {
		project.task('minepkg') {
			group = 'publishing'
			description = 'Publishes a minepkg release.'

			doNotTrackState("nothing to track locally")

			def extension = project.extensions.create('minepkg', MinepkgPluginExtension)

			dependsOn project.tasks.named("build")

			doLast {
				def manifestJson = extension.toJson()
				def jarFile = extension.artifact instanceof AbstractArchiveTask ? extension.artifact.archiveFile.get().asFile : extension.artifact
				def data = jarFile.getBytes()

				new MinepkgPublish(extension.apiKey)
					.publish(manifestJson, data)
			}
		}
	}
}