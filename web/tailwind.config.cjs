/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        'index.html',
        './src/**/*.vue',
    ],
    theme: {
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
                gradient: 'linear-gradient(270deg, #fff1bd 0%, #ffe176 100%)',
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
        extend: {},
        fontFamily: {
            roboto: "'Roboto', sans-serif",
            raleway: "'Raleway', sans-serif",
        },
    },
    plugins: [
        require('@tailwindcss/forms'),
        require('@tailwindcss/typography'),
    ],
}
