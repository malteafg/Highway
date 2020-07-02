name := "Highway"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.0" % "test"

libraryDependencies ++= {
    val version = "3.2.3"
    val os = "windows" // TODO: Change to "linux" or "macos" if necessary

    Seq(
        "lwjgl",
        "lwjgl-glfw",
        "lwjgl-opengl"
        // TODO: Add more modules here
    ).flatMap {
        module => {
            Seq(
                "org.lwjgl" % module % version,
                "org.lwjgl" % module % version classifier s"natives-$os"
            )
        }
    }
}