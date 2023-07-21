package io.github.fourmisain.minepkg

import groovy.json.JsonOutput

class MinepkgPluginExtension {
	def apiKey
	def artifact

	private manifest = [
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
	private pkg = manifest['package']
	private req = manifest['requirements']

	def setName(String name) {
		pkg['name'] = name
	}

	def setVersion(String version) {
		pkg['version'] = version
	}

	def setPlatform(String platform) {
		pkg['platform'] = platform
	}

	def setLicense(String license) {
		pkg['license'] = license
	}

	def setMinecraftVersionRange(String versionRange) {
		req['minecraft'] = versionRange
	}

	def setFabricLoaderVersionRange(String versionRange) {
		req['fabricLoader'] = versionRange
	}

	def require(String modId, String versionRange = '*') {
		manifest['dependencies'][modId] = versionRange
	}

	protected def validateEntry(String category, String key) {
		if (!manifest[category][key]) throw new IllegalStateException("${key} not set")
	}

	String toJson() {
		['name', 'version', 'platform',].forEach { validateEntry('package', it) }
		validateEntry('requirements', 'minecraft')

		if (pkg['platform'] == 'fabric') {
			if (!req['fabricLoader']) req['fabricLoader'] = '*'
		}

		return JsonOutput.toJson(manifest)
	}
}
