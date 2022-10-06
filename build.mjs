import esbuild from 'esbuild'
import stylePlugin from 'esbuild-style-plugin'
import tailwindcss from 'tailwindcss'
import autoprefixer from "autoprefixer";

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
        ],
        plugins: [
            stylePlugin({
                postcss: {
                    plugins: [tailwindcss, autoprefixer]
                }
            })
        ],
        watch: {
            onRebuild(error, result) {
                if (error) console.error('watch build failed:', error)
                else {
                    console.log('watch build succeeded:', result)
                }
            },
        }
    })
    .catch(() => process.exit(1));
