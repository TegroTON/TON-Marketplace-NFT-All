<template>
  <div>
    <slot></slot>
  </div>
</template>

<script lang="ts">
import $ from 'jquery';
import 'slick-carousel';
import {defineComponent} from "vue";

export default defineComponent({
  props: {
    options: {
      type: Object,
      default: function () {
        return {};
      },
    },
  },
  mounted: function () {
    this.create();
  },
  destroyed: function () {
    $(this.$el).slick('unslick');
  },
  methods: {
    create: function () {
      const $slick = $(this.$el);
      $slick.on('afterChange', this.onAfterChange);
      $slick.on('beforeChange', this.onBeforeChange);
      $slick.on('breakpoint', this.onBreakpoint);
      $slick.on('destroy', this.onDestroy);
      $slick.on('edge', this.onEdge);
      $slick.on('init', this.onInit);
      $slick.on('reInit', this.onReInit);
      $slick.on('setPosition', this.onSetPosition);
      $slick.on('swipe', this.onSwipe);
      $slick.on('lazyLoaded', this.onLazyLoaded);
      $slick.on('lazyLoadError', this.onLazyLoadError);
      $slick.slick(this.options);
    },
    destroy: function () {
      const $slick = $(this.$el);
      $slick.off('afterChange', this.onAfterChange);
      $slick.off('beforeChange', this.onBeforeChange);
      $slick.off('breakpoint', this.onBreakpoint);
      $slick.off('destroy', this.onDestroy);
      $slick.off('edge', this.onEdge);
      $slick.off('init', this.onInit);
      $slick.off('reInit', this.onReInit);
      $slick.off('setPosition', this.onSetPosition);
      $slick.off('swipe', this.onSwipe);
      $slick.off('lazyLoaded', this.onLazyLoaded);
      $slick.off('lazyLoadError', this.onLazyLoadError);
      $(this.$el).slick('unslick');
    },
    reSlick: function () {
      this.destroy();
      this.create();
    },
    next: function () {
      $(this.$el).slick('slickNext');
    },
    prev: function () {
      $(this.$el).slick('slickPrev');
    },
    pause: function () {
      $(this.$el).slick('slickPause');
    },
    play: function () {
      $(this.$el).slick('slickPlay');
    },
    goTo: function (index: number, dontAnimate: boolean): any {
      $(this.$el).slick('slickGoTo', index, dontAnimate);
    },
    currentSlide: function () {
      return $(this.$el).slick('slickCurrentSlide');
    },
    add: function (element: any, index: number, addBefore: boolean) {
      $(this.$el).slick('slickAdd', element, index, addBefore);
    },
    remove: function (index: number, removeBefore: boolean) {
      $(this.$el).slick('slickRemove', index, removeBefore);
    },
    filter: function (filterData: any) {
      $(this.$el).slick('slickFilter', filterData);
    },
    unfilter: function () {
      $(this.$el).slick('slickUnfilter');
    },
    getOption: function (option: string) {
      $(this.$el).slick('slickGetOption', option);
    },
    setOption: function (option: string, value: any, refresh: boolean) {
      $(this.$el).slick('slickSetOption', option, value, refresh);
    },
    setPosition: function () {
      $(this.$el).slick('setPosition');
    },
    // Events
    onAfterChange: function (event: any, slick: any, currentSlide: any) {
      this.$emit('afterChange', event, slick, currentSlide);
    },
    onBeforeChange: function (event: any, slick: any, currentSlide: any, nextSlide: any) {
      this.$emit('beforeChange', event, slick, currentSlide, nextSlide);
    },
    onBreakpoint: function (event: any, slick: any, breakpoint: any) {
      this.$emit('breakpoint', event, slick, breakpoint);
    },
    onDestroy: function (event: any, slick: any) {
      this.$emit('destroy', event, slick);
    },
    onEdge: function (event: any, slick: any, direction: any) {
      this.$emit('edge', event, slick, direction);
    },
    onInit: function (event: any, slick: any) {
      this.$emit('init', event, slick);
    },
    onReInit: function (event: any, slick: any) {
      this.$emit('reInit', event, slick);
    },
    onSetPosition: function (event: any, slick: any) {
      this.$emit('setPosition', event, slick);
    },
    onSwipe: function (event: any, slick: any, direction: any) {
      this.$emit('swipe', event, slick, direction);
    },
    onLazyLoaded: function (event: any, slick: any, image: any, imageSource: any) {
      this.$emit('lazyLoaded', event, slick, image, imageSource);
    },
    onLazyLoadError: function (event: any, slick: any, image: any, imageSource: any) {
      this.$emit('lazyLoadError', event, slick, image, imageSource);
    },
  },
})
</script>
