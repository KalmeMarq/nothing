function exists(path: string) {
    try {
        Deno.statSync(path);
        return true;
    } catch (e) {
        return false;
    }
}

function getOsName() {
    return Deno.build.os == "windows"
        ? "windows"
        : Deno.build.os == "darwin"
        ? "osx"
        : "linux";
}

Deno.mkdirSync("./libs", { recursive: true });

const clientJson = JSON.parse(Deno.readTextFileSync("./client.json"));

function parseWithVars(value: string) {
    return value.replace(/\$\((?<varname>[a-zA-Z_0-9/-]*)\)/g, (_, b) => {
        return clientJson.variables[b];
    });
}

const cp: string[] = [];

for (const lib of clientJson["libraries"]) {
    const libLocalPath = `./libs/${parseWithVars(lib.path)}`;

    if (lib["allowIfOs"] != null) {
        if (lib["allowIfOs"] != getOsName()) continue;
    }

    cp.push(libLocalPath);

    if (!exists(libLocalPath)) {
        console.log(`Downloading ${parseWithVars(lib.name)}`);
        Deno.mkdirSync(
            libLocalPath.substring(0, libLocalPath.lastIndexOf("/")),
            {
                recursive: true,
            }
        );
        const data = await(
            await fetch(parseWithVars(lib.url), {
                headers: { "Content-Type": "application/octet-stream" },
            })
        ).arrayBuffer();
        Deno.writeFileSync(libLocalPath, new Uint8Array(data));
    } else console.log(`Checking ${parseWithVars(lib.name)}`);

    if (lib["classifier"] != null) {
        for (const [native, nativeName] of Object.entries(lib["classifier"])) {
            if (native != getOsName()) continue;

            const nativeLocalPath =
                libLocalPath.substring(0, libLocalPath.lastIndexOf(".jar")) +
                "-" +
                nativeName +
                ".jar";

            const nativeUrl = parseWithVars(
                lib.url.substring(0, lib.url.lastIndexOf(".jar")) +
                    "-" +
                    nativeName +
                    ".jar"
            );

            cp.push(nativeLocalPath);

            if (!exists(nativeLocalPath)) {
                console.log(
                    `Downloading ${parseWithVars(
                        lib.name
                    )} ${native} classifier`
                );

                Deno.mkdirSync(
                    nativeLocalPath.substring(
                        0,
                        nativeLocalPath.lastIndexOf("/")
                    ),
                    {
                        recursive: true,
                    }
                );

                const data = await(
                    await fetch(nativeUrl, {
                        headers: { "Content-Type": "application/octet-stream" },
                    })
                ).arrayBuffer();
                Deno.writeFileSync(nativeLocalPath, new Uint8Array(data));
            } else
                console.log(
                    `Checking ${parseWithVars(lib.name)} ${native} classifier`
                );
        }
    }
}

const cmd = new Deno.Command("D:/ProgramsFiles/Java/jdk-17.0.7/bin/java.exe", {
    args: [
        "-cp",
        cp.join(";") + ";" + "../build/libs/nothing-1.0.0.jar",
        clientJson.mainClass,
    ],
    stdin: "inherit",
    stderr: "inherit",
    stdout: "inherit",
});
await cmd.spawn().status;
