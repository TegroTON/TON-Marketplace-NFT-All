const path = require('path');
const webpack = require("webpack");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");


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
                use: [MiniCssExtractPlugin.loader, 'css-loader'],
            },
        ],
    },
    plugins: [
        new webpack.ProvidePlugin({
            Buffer: ['buffer', 'Buffer'],
        }),
        new MiniCssExtractPlugin({
            filename: 'index.css'
        }),
    ],
    resolve: {
        extensions: ['.tsx', '.ts', '.js'],
    },
};
