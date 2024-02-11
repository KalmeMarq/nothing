const resolveFromHere = (path: string) =>
    import.meta.resolve(path).substring(8);

const buildGradle = Deno.readTextFileSync(resolveFromHere("../build.gradle"));
const buildGradleDepsSection = buildGradle
    .substring(
        buildGradle.indexOf("dependencies {") + "dependencies {".length,
        buildGradle.indexOf("}", buildGradle.indexOf("dependencies {"))
    )
    .trim()
    .split("\n")
    .map((line) => line.trim())
    .filter((line) => line.length > 0);

console.log(buildGradleDepsSection);
