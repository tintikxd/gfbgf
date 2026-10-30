
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
		import org.gradle.api.tasks.bundling.Jar
		import org.gradle.api.file.DuplicatesStrategy
		import org.gradle.api.tasks.compile.JavaCompile
		import org.gradle.api.publish.maven.MavenPublication

		plugins {
			id("net.fabricmc.fabric-loom-remap")
			`maven-publish`
			id("org.jetbrains.kotlin.jvm") version "2.4.20"
		}

repositories {
	mavenCentral()

	exclusiveContent {
		forRepository {
			maven {
				name = "Modrinth"
				url = uri("https://api.modrinth.com/maven")
			}
		}
		filter {
			includeGroup("maven.modrinth")
		}
	}

	maven {
		name = "Shedaniel"
		url = uri("https://maven.shedaniel.me/")
	}

	maven {
		name = "JitPack"
		url = uri("https://jitpack.io")
	}

	maven {
		name = "Modrinth"
		url = uri("https://api.modrinth.com/maven")
	}

	maven {
		name = "GeckoLib"
		url = uri("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")
		content {
			includeGroupByRegex("software\\.bernie.*")
			includeGroup("com.eliotlash.mclib")
		}
	}
}

loom {
	splitEnvironmentSourceSets()

	mods {
		register("magic") {
			sourceSet(sourceSets.main.get())
			sourceSet(sourceSets.getByName("client"))
		}
	}
}

fabricApi {
	configureDataGeneration {
		client = true
	}
}

dependencies {
	// Minecraft y Fabric
	minecraft("com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}")
	mappings(loom.officialMojangMappings())

	modImplementation(
		"net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}"
	)

	modImplementation(
		"net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}"
	)

	modImplementation(
		"net.fabricmc:fabric-language-kotlin:${providers.gradleProperty("fabric_kotlin_version").get()}"
	)

	// Dependencias de Modrinth
	modImplementation("maven.modrinth:zJpHMkdD:155jtqJi")
	modImplementation("maven.modrinth:kkmrDlKT:kmob8AJ4")

	// Immersive Portals
	modImplementation("com.github.iPortalTeam.ImmersivePortalsMod:imm_ptl_core:v3.3.9-mc1.20.1") {
		exclude(group = "net.fabricmc.fabric-api")
		isTransitive = false
	}

	modImplementation("com.github.iPortalTeam.ImmersivePortalsMod:q_misc_util:v3.3.9-mc1.20.1") {
		exclude(group = "net.fabricmc.fabric-api")
		isTransitive = false
	}

	// Cloth Config
	modImplementation("me.shedaniel.cloth:cloth-config-fabric:11.0.99") {
		exclude(group = "net.fabricmc.fabric-api")
	}

	// GeckoLib
	modImplementation(
		"software.bernie.geckolib:geckolib-fabric-${providers.gradleProperty("minecraft_version").get()}:${providers.gradleProperty("geckolib_version").get()}"
	)

	// McLib
	implementation("com.eliotlash.mclib:mclib:20")

	// Night Config: biblioteca que contiene ConfigFormat
	implementation("com.electronwill.night-config:core:3.6.6")
	implementation("com.electronwill.night-config:toml:3.6.6")
}

tasks.processResources {
	val version = project.version
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand("version" to version)
	}

	duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.withType<JavaCompile>().configureEach {
	options.release = 17
}

kotlin {
	compilerOptions {
		jvmTarget = JvmTarget.JVM_17
	}
}

java {
	withSourcesJar()
	sourceCompatibility = JavaVersion.VERSION_17
	targetCompatibility = JavaVersion.VERSION_17
}

tasks.jar {
	val projectName = project.name
	inputs.property("projectName", projectName)

	from("LICENSE") {
		rename { "${it}_$projectName" }
	}

	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

publishing {
	publications {
		register<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	repositories {
		// Repositorios de publicación, si los necesitás.
	}
}

tasks.named<Jar>("sourcesJar") {
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
