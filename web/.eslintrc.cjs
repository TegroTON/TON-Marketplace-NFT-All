module.exports = {
    env: {
        node: true,
    },
    extends: [
        'eslint:recommended',
        "plugin:@typescript-eslint/recommended",
        'plugin:vue/vue3-recommended',
    ],
    rules: {},
    ignorePatterns: [
        "src/assets/**/*.js",
        "src/**/*generated.ts",
    ]
}
