package io.github.fourmisain.minepkg

import groovy.json.JsonOutput
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property

abstract class MinepkgPluginExtension {
	MinepkgPluginExtension() {
		getPlatform().convention("fabric")
	}

	abstract Property<String> getApiKey();
	abstract Property<Object> getArtifact();

	abstract Property<String> getName();
	abstract Property<String> getVersion();
	abstract Property<String> getPlatform();
	abstract Property<String> getLicense();
	abstract Property<String> getMinecraftVersionRange();
	abstract Property<String> getFabricLoaderVersionRange();

	protected abstract MapProperty<String, String> getDependencies();

	def require(String modId, String versionRange = '*') {
		dependencies.put(modId, versionRange)
	}

	String toJson() {
		def manifest = [
			manifestVersion: 0,
			package        : [
				type       : 'mod',
				platform   : 'fabric',
			],
			requirements   : [:],
			dependencies   : [:],
			meta           : [
				published: false
			]
		]

		def ifPresent = { p, closure -> if (p.isPresent()) closure.call(p.get()) }

		def pkg = manifest['package']
		pkg['name'] = name.get()
		pkg['version'] = version.get()
		pkg['platform'] = platform.get()
		ifPresent(license) { pkg['license']  = it }

		def req = manifest['requirements']
		req['minecraft'] = minecraftVersionRange.get()
		ifPresent(fabricLoaderVersionRange) { req['fabricLoader'] = it }

		manifest['dependencies'] += dependencies.get()

		if (pkg['platform'] == 'fabric') {
			if (!req['fabricLoader']) req['fabricLoader'] = '*'
		}

		return JsonOutput.toJson(manifest)
	}

	String toPrettyJson() {
		return JsonOutput.prettyPrint(toJson())
	}
}
