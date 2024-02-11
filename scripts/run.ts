const resolveFromHere = (path: string) =>
    import.meta.resolve(path).substring(8);

function exists(path: string) {
    try {
        Deno.statSync(path);
        return true;
    } catch (e) {
        return false;
    }
}

Deno.mkdirSync(resolveFromHere("./libs"), { recursive: true });

const natives = [];

const deps = [
    {
        repository: "https://repo.maven.apache.org/maven2/",
        libs: [
            {
                name: "org.lwjgl:lwjgl:3.3.3",
                url: "org/lwjgl/lwjgl/3.3.3/lwjgl-3.3.3.jar",
                classifier: "natives-windows",
            },
            {
                name: "org.lwjgl:lwjgl-freetype:3.3.3",
                url: "org/lwjgl/lwjgl-freetype/3.3.3/lwjgl-freetype-3.3.3.jar",
                classifier: "natives-windows",
            },
            {
                name: "org.lwjgl:lwjgl-glfw:3.3.3",
                url: "org/lwjgl/lwjgl-glfw/3.3.3/lwjgl-glfw-3.3.3.jar",
                classifier: "natives-windows",
            },
            {
                name: "org.lwjgl:lwjgl-jemalloc:3.3.3",
                url: "org/lwjgl/lwjgl-jemalloc/3.3.3/lwjgl-jemalloc-3.3.3.jar",
                classifier: "natives-windows",
            },
            {
                name: "org.lwjgl:lwjgl-openal:3.3.3",
                url: "org/lwjgl/lwjgl-openal/3.3.3/lwjgl-openal-3.3.3.jar",
                classifier: "natives-windows",
            },
            {
                name: "org.lwjgl:lwjgl-opengl:3.3.3",
                url: "org/lwjgl/lwjgl-opengl/3.3.3/lwjgl-opengl-3.3.3.jar",
                classifier: "natives-windows",
            },
            {
                name: "org.lwjgl:lwjgl-stb:3.3.3",
                url: "org/lwjgl/lwjgl-stb/3.3.3/lwjgl-stb-3.3.3.jar",
                classifier: "natives-windows",
            },
            {
                name: "io.netty:netty-all:4.1.106.Final",
                url: "io/netty/netty-all/4.1.106.Final/netty-all-4.1.106.Final.jar",
            },
            {
                name: "io.netty:netty-buffer:4.1.106.Final",
                url: "io/netty/netty-buffer/4.1.106.Final/netty-buffer-4.1.106.Final.jar",
            },
            {
                name: "io.netty:netty-handler:4.1.106.Final",
                url: "io/netty/netty-handler/4.1.106.Final/netty-handler-4.1.106.Final.jar",
            },
            {
                name: "io.netty:netty-transport-classes-epoll:4.1.106.Final",
                url: "io/netty/netty-transport-classes-epoll/4.1.106.Final/netty-transport-classes-epoll-4.1.106.Final.jar",
            },
            {
                name: "io.netty:netty-resolver:4.1.106.Final",
                url: "io/netty/netty-resolver/4.1.106.Final/netty-resolver-4.1.106.Final.jar",
            },
            {
                name: "io.netty:netty-resolver-dns:4.1.106.Final",
                url: "io/netty/netty-resolver-dns/4.1.106.Final/netty-resolver-dns-4.1.106.Final.jar",
            },
            {
                name: "io.netty:netty-codec:4.1.106.Final",
                url: "io/netty/netty-codec/4.1.106.Final/netty-codec-4.1.106.Final.jar",
            },
            {
                name: "io.netty:netty-codec-dns:4.1.106.Final",
                url: "io/netty/netty-codec-dns/4.1.106.Final/netty-codec-dns-4.1.106.Final.jar",
            },
            {
                name: "io.netty:netty-codec-http:4.1.106.Final",
                url: "io/netty/netty-codec-http/4.1.106.Final/netty-codec-http-4.1.106.Final.jar",
            },
            {
                name: "io.netty:netty-codec-http2:4.1.106.Final",
                url: "io/netty/netty-codec-http2/4.1.106.Final/netty-codec-http2-4.1.106.Final.jar",
            },
            {
                name: "io.netty:netty-common:4.1.106.Final",
                url: "io/netty/netty-common/4.1.106.Final/netty-common-4.1.106.Final.jar",
            },
            {
                name: "io.netty:netty-transport:4.1.106.Final",
                url: "io/netty/netty-transport/4.1.106.Final/netty-transport-4.1.106.Final.jar",
            },
            {
                name: "org.joml:joml:1.10.5",
                url: "org/joml/joml/1.10.5/joml-1.10.5.jar",
            },
            {
                name: "com.google.code.gson:gson:2.10.1",
                url: "com/google/code/gson/gson/2.10.1/gson-2.10.1.jar",
            },
            {
                name: "org.apache.logging.log4j:log4j-api:2.22.1",
                url: "org/apache/logging/log4j/log4j-api/2.22.1/log4j-api-2.22.1.jar",
            },
            {
                name: "org.apache.logging.log4j:log4j-core:2.22.1",
                url: "org/apache/logging/log4j/log4j-core/2.22.1/log4j-core-2.22.1.jar",
            },
            {
                name: "org.apache.logging.log4j:log4j-slf4j-impl:2.22.1",
                url: "org/apache/logging/log4j/log4j-slf4j-impl/2.22.1/log4j-slf4j-impl-2.22.1.jar",
            },
            {
                name: "org.slf4j:slf4j-api:1.7.36",
                url: "org/slf4j/slf4j-api/1.7.36/slf4j-api-1.7.36.jar",
            },
            {
                name: "io.github.spair:imgui-java-binding:1.86.11",
                url: "io/github/spair/imgui-java-binding/1.86.11/imgui-java-binding-1.86.11.jar",
            },
            {
                name: "io.github.spair:imgui-java-lwjgl3:1.86.11",
                url: "io/github/spair/imgui-java-lwjgl3/1.86.11/imgui-java-lwjgl3-1.86.11.jar",
            },
            {
                name: "io.github.spair:imgui-java-natives-windows:1.86.11",
                url: "io/github/spair/imgui-java-natives-windows/1.86.11/imgui-java-natives-windows-1.86.11.jar",
            },
        ],
    },
    {
        repository: "https://jitpack.io/",
        libs: [
            {
                name: "com.github.KalmeMarq:arg-option-parser:e8a468fef5",
                url: "com/github/KalmeMarq/arg-option-parser/e8a468fef5/arg-option-parser-e8a468fef5.jar",
            },
        ],
    },
];

const cp: string[] = [];

for (const dep of deps) {
    for (const lib of dep.libs) {
        const localPath = resolveFromHere("./libs/" + lib.url);
        cp.push(localPath);
        if (!exists(localPath)) {
            console.log(`Downloading ${lib.name}...`);

            Deno.mkdirSync(localPath.substring(0, localPath.lastIndexOf("/")), {
                recursive: true,
            });

            const data = await (
                await fetch(dep.repository + lib.url, {
                    headers: { "Content-Type": "application/octet-stream" },
                })
            ).arrayBuffer();
            Deno.writeFileSync(localPath, new Uint8Array(data));
        }

        if (lib.classifier != null) {
            const nativesUrl =
                lib.url.substring(0, lib.url.lastIndexOf(".")) +
                "-" +
                lib.classifier +
                ".jar";
            const nativesLocalPath = resolveFromHere("./libs/" + nativesUrl); // check cuz natives may be bleeding to the root dir
            cp.push(nativesLocalPath);

            if (!exists(nativesLocalPath)) {
                console.log(`Downloading ${lib.name} ${lib.classifier}...`);

                Deno.mkdirSync(
                    nativesLocalPath.substring(
                        0,
                        nativesLocalPath.lastIndexOf("/")
                    ),
                    {
                        recursive: true,
                    }
                );

                const data = await (
                    await fetch(dep.repository + nativesUrl, {
                        headers: { "Content-Type": "application/octet-stream" },
                    })
                ).arrayBuffer();
                Deno.writeFileSync(nativesLocalPath, new Uint8Array(data));
            }
        }
    }
}

const cmd = new Deno.Command("D:/ProgramsFiles/Java/jdk-17.0.7/bin/java.exe", {
    args: [
        "-cp",
        cp.join(";") + ";" + resolveFromHere("../build/libs/nothing-1.0.0.jar"),
        "me.kalmemarq.client.Main",
    ],
    stdin: "inherit",
    stderr: "inherit",
    stdout: "inherit",
});
await cmd.spawn().status;
