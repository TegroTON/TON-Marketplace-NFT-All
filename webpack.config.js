const path = require('path');
const webpack = require("webpack");

module.exports = {
    entry: './web/ts/index.ts',
    output: {
        filename: 'index.js',
        path: path.resolve(__dirname, './src/main/resources/static/bundle/'),
    },
    mode: "development",
    module: {
        rules: [
            {
                test: /\.tsx?$/,
                use: 'ts-loader',
                exclude: /node_modules/,
            },
            {
                test: /\.css$/i,
                use: ['style-loader', 'css-loader'],
            },
        ],
    },
    plugins: [
        new webpack.ProvidePlugin({
            Buffer: ['buffer', 'Buffer'],
        }),
    ],
    resolve: {
        extensions: ['.tsx', '.ts', '.js'],
    },
};
