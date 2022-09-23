import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import {NgmiPolyfill} from "vite-plugin-ngmi-polyfill";

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        vue(),
        NgmiPolyfill(),
    ],
    resolve: {
        alias: {
            '~bootstrap': path.resolve(__dirname, 'node_modules/bootstrap'),
        }
    }
})
