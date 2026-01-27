package io.github.fourmisain.minepkg

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic

class MinepkgPublish {
	private apiKey

	MinepkgPublish(apiKey) {
		this.apiKey = apiKey
	}

	def publish(manifestJson, data) {
		def (platform, identifier) = createUnpublishedVersion(manifestJson)
		println("created unpublished ${platform} release ${identifier}")
		try {
			uploadArtifact(platform, identifier, data)
			println("uploaded artifact")
		} catch (Exception e) {
			try {
				deleteRelease(platform, identifier)
				println('deleted unpublished release')
			} catch (Exception ee) {
				e.addSuppressed(new RuntimeException("failed deleting unpublished release", ee))
			}
			throw e
		}
	}

	List<String> createUnpublishedVersion(String manifestJson) {
		def data = manifestJson.getBytes("UTF-8")
		def (_, response) = postRequest("https://api.preview.minepkg.io/v1/releases", data)
		response = new JsonSlurper().parse(response)

		def pkg = response["package"]
		def platform = pkg["platform"]
		def identifier = pkg["name"] + "@" + pkg["version"]
		return [platform, identifier]
	}

	def uploadArtifact(String platform, String identifier, byte[] fileContents) {
		postRequest("https://api.preview.minepkg.io/v1/releases/${platform}/${identifier}/upload", fileContents, "application/java-archive")
	}

	def postRequest(String url, byte[] data, String contentType = "application/json") {
		return request("POST", url, data, contentType)
	}

	def deleteRelease(String platform, String identifier) {
		return request("DELETE", "https://api.preview.minepkg.io/v1/releases/${platform}/${identifier}")
	}

	@CompileStatic
	def request(String method, String url, byte[] data = null, String contentType = "application/json") {
		HttpURLConnection c = (HttpURLConnection) new URI(url).toURL().openConnection()
		try {
			c.setRequestMethod(method)
			c.setRequestProperty("User-Agent", "minepkg gradle plugin")
			c.setRequestProperty("Authorization", "api-key ${this.apiKey}")
			c.setRequestProperty("Content-Type", contentType)

			if (data != null) {
				c.setDoOutput(true)
				c.setFixedLengthStreamingMode(data.length)
				try (def output = c.getOutputStream()) {
					output.write(data)
				}
			}

			try (def input = c.getInputStream()) {
				def response = input.getBytes()
				return [c.getResponseCode(), response]
			}
		} finally {
			c.disconnect()
		}
	}
}
