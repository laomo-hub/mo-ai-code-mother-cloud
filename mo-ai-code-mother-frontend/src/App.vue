<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
</script>

<template>
  <BasicLayout />
</template>

<style>
/* 全局样式优化 */
* {
  box-sizing: border-box;
}

body {
  margin: 0;
  padding: 0;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen', 'Ubuntu', 'Cantarell',
    'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  overflow-x: hidden;
}

html {
  overflow-x: hidden;
}

/* Ant Design 主色覆盖为橙色 */
.ant-btn-primary {
  background: linear-gradient(135deg, #ff6b35, #ff8c42) !important;
  border-color: #ff6b35 !important;
  box-shadow: 0 2px 10px rgba(255, 107, 53, 0.3);
}

.ant-btn-primary:hover {
  background: linear-gradient(135deg, #e8552a, #ff7a33) !important;
  border-color: #e8552a !important;
  box-shadow: 0 4px 18px rgba(255, 107, 53, 0.45);
}

/* Ghost 按钮（下载代码） */
.ant-btn-primary.ant-btn-background-ghost {
  color: #ff6b35 !important;
  border-color: #ff6b35 !important;
  background: transparent !important;
}

.ant-btn-primary.ant-btn-background-ghost:hover {
  color: #e8552a !important;
  border-color: #e8552a !important;
  background: transparent !important;
}

/* 默认按钮（应用详情） */
.ant-btn-default:hover {
  color: #ff6b35 !important;
  border-color: #ff6b35 !important;
}

/* 链接按钮（编辑模式、新窗口打开等） */
.ant-btn-link {
  color: #e8552a !important;
}

.ant-btn-link:hover {
  color: #ff6b35 !important;
}

.ant-pagination .ant-pagination-item-active {
  border-color: #ff6b35;
}

.ant-pagination .ant-pagination-item-active a {
  color: #ff6b35;
}

/* Ant Design 菜单选中态覆盖为橙色 */
.ant-menu-horizontal > .ant-menu-item-selected {
  color: #e8552a !important;
}

.ant-menu-horizontal > .ant-menu-item-selected::after {
  border-bottom-color: #e8552a !important;
}

.ant-menu-horizontal > .ant-menu-item:hover {
  color: #e8552a !important;
}

.ant-menu-horizontal > .ant-menu-item:hover::after {
  border-bottom-color: #e8552a !important;
}

/* 全局链接颜色 */
a {
  color: #e8552a;
}

a:hover {
  color: #ff6b35;
}
</style>
