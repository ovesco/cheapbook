<template>
    <div>
        <theme v-model="theme" :current="theme" :show="chooseTheme"
               @close="chooseTheme = false, keys = []" />
        <a-layout>
            <a-layout-header>
                <a-menu v-model="keys" theme="dark" mode="horizontal" :style="{lineHeight: '64px'}">
                    <a-menu-item key="environments">Environments</a-menu-item>
                    <a-menu-item key="close">Close session</a-menu-item>
                    <a-menu-item key="theme" @click="chooseTheme = true">Theme</a-menu-item>
                </a-menu>
            </a-layout-header>
            <a-layout-content>
                <main>
                    <div class="toolbar">
                        <a-button-group>
                            <a-button icon="caret-right">Run</a-button>
                            <a-button>Stop</a-button>
                            <a-button>Restart</a-button>
                        </a-button-group>
                        <a-button-group>
                            <a-button>Dependencies</a-button>
                        </a-button-group>
                    </div>
                    <a-card :bodyStyle="{padding: 0}">
                        <editor :theme="theme" />
                    </a-card>
                </main>
            </a-layout-content>
        </a-layout>
    </div>
</template>

<script>
import {
    Layout,
    Menu,
    Button,
    Card,
} from 'ant-design-vue';
import Editor from '../components/Editor.vue';
import Theme from '../components/Theme.vue';

export default {
    components: {
        Editor,
        Theme,
        aLayout: Layout,
        aLayoutHeader: Layout.Header,
        aLayoutContent: Layout.Content,
        aMenu: Menu,
        aMenuItem: Menu.Item,
        aButton: Button,
        aButtonGroup: Button.Group,
        aCard: Card,
    },
    data() {
        return {
            chooseTheme: false,
            keys: [],
            theme: 'base16-light',
        };
    },
};
</script>

<style scoped lang="scss">
    main {
        max-width: 1024px;
        margin:auto;
        height: calc(100vh - 64px);
        padding-top: 2rem;

        .toolbar {
            display:flex;
            justify-content: space-between;
            margin-bottom: 1rem;
        }
    }
</style>
