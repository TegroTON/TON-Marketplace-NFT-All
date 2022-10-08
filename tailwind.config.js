const path = require('path');

/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        path.join(__dirname, "/src/reactMain/kotlin/**/*.kt"),
        path.join(__dirname, "/src/reactMain/resources/**/*.html"),
    ],
    theme: {
        extend: {
            colors: {
                transparent: 'transparent',
                white: '#ffffff',
                dark: {
                    700: '#111826',
                    900: '#050b1c',
                },
                gray: {
                    500: '#848a9c',
                    700: '#464a59',
                    900: '#1c2533',
                },
                border: {
                    dark: '#111927',
                    soft: 'rgb(255 255 255 / 0.02)',
                },
                yellow: {
                    DEFAULT: '#ffd12e',
                    hover: '#ffb82e',
                    gradient: {
                        start: '#ffe176',
                        end: '#fff1bd'
                    }
                },
                hover: {
                    DEFAULT: 'rgb(255 255 255 / 0.03)',
                },
                soft: {
                    DEFAULT: 'rgb(255 255 255 / 0.05)'
                },
                green: {
                    DEFAULT: '#13ff77',
                    soft: 'rgb(41 255 107 / 0.1)'
                },
                pruple: {
                    DEFAULT: '#8829ff',
                    soft: 'rgb(136 41 255 / 0.1)'
                },
                ref: {
                    DEFAULT: '#ff2968',
                    soft: ' rgb(255 41 104 / 0.1)'
                },
                aqua: {
                    DEFAULT: '#2af1ff',
                }
            },
            fontFamily: {
                roboto: "'Roboto', sans-serif",
                raleway: "'Raleway', sans-serif",
            },
        },
    },
    plugins: [],
}
