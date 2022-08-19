<template>
    <div>
        <Header/>
        <mobile-menu/>
        <main>
            <slot></slot>
        </main>
        <Footer/>
        <back-to-top/>
    </div>
</template>

<script>
    import Header from './header/Header'
    import Footer from './footer/Footer'
    import MobileMenu from './header/MobileMenu'
    import BackToTop from '@/components/layouts/footer/BackToTop'

    export default {
        name: 'Layout',
        components: {BackToTop, MobileMenu, Footer, Header}
    }
</script>