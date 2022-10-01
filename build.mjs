import esbuild from 'esbuild'

esbuild
    .build({
        entryPoints: ["./web/ts/index.ts"],
        mainFields: ["browser", "module", "main"],
        bundle: true,
        minify: true,
        logLevel: "info",
        outdir: "./src/main/resources/static/bundle/",
        loader: {
            '.png': "dataurl",
            '.svg': "dataurl",
        },
        inject: [
            "esbuild.inject.js"
        ]
    })
    .catch(() => process.exit(1));
