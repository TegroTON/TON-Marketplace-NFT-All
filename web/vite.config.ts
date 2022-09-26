import {defineConfig} from 'vite';
import vue from '@vitejs/plugin-vue';
import {NgmiPolyfill} from "vite-plugin-ngmi-polyfill";
import eslint from 'vite-plugin-eslint';

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [
        vue(),
        NgmiPolyfill(),
        eslint(),
    ],
    resolve: {
        alias: {}
    },
    optimizeDeps: {include: ['lodash.throttle', 'lodash.orderby']},
})
