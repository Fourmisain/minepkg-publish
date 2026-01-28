package io.github.fourmisain.minepkg

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.AbstractArchiveTask

abstract class MinepkgPlugin implements Plugin<Project> {
	void apply(Project project) {
		def extension = project.extensions.create('minepkg', MinepkgPluginExtension)

		project.tasks.register('minepkg') {
			group = 'publishing'
			description = 'Publishes a minepkg release.'

			doNotTrackState('nothing to track locally')

			dependsOn project.tasks.named('build')

			doLast {
				def manifestJson = extension.toJson()
				def artifact = extension.artifact.get()
				def jarFile = artifact instanceof AbstractArchiveTask ? artifact.archiveFile.get().asFile : artifact
				def data = jarFile.getBytes()

				new MinepkgPublish(extension.apiKey.get())
					.publish(manifestJson, data)
			}
		}
	}
}