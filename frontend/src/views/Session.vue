<template>
    <div>
        <theme v-model="theme" :current="theme" :show="chooseTheme"
               @close="chooseTheme = false, keys = []" />
        <environments :show="chooseEnvironment" @close="chooseEnvironment = false, keys = []" />
        <dependencies v-if="$store.state.env !== null" :show="updateDeps"
                      @close="updateDeps = false, keys = []" />
        <a-layout>
            <a-layout-header>
                <a-menu v-model="keys" theme="dark" mode="horizontal" :style="{lineHeight: '64px'}">
                    <a-menu-item key="environments" @click="chooseEnvironment = true">
                        Environments
                    </a-menu-item>
                    <a-menu-item @click="$store.commit('setToken', null)" key="close">
                        Close session
                    </a-menu-item>
                    <a-menu-item key="theme" @click="chooseTheme = true">Theme</a-menu-item>
                </a-menu>
            </a-layout-header>
            <a-layout-content>
                <main>
                    <div class="toolbar" v-if="$store.state.env">
                        <a-button-group class="mr-2">
                            <a-button icon="caret-right" @click="run"
                                      :loading="$store.state.running"
                                      :disabled="$store.state.running">Run</a-button>
                            <a-button @click="stop" :disabled="!$store.state.running">
                                Stop
                            </a-button>
                        </a-button-group>
                        <a-button-group>
                            <a-button @click="updateDeps = true">Dependencies</a-button>
                        </a-button-group>
                    </div>
                    <a-card :bodyStyle="{padding: 0}">
                        <editor :theme="theme" />
                    </a-card>
                    <div style="height:5px;"></div>
                    <a-card :bodyStyle="{padding: '5px'}" v-if="$store.state.output">
                        <div>
                            <p class="m-0" v-for="item in format($store.state.output.output)"
                               :key="item">{{ item }}</p>
                        </div>
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
    Spin,
} from 'ant-design-vue';
import Editor from '../components/Editor.vue';
import Theme from '../components/Theme.vue';
import Environments from '../components/Environments.vue';
import Dependencies from '../components/Dependencies.vue';

export default {
    components: {
        Editor,
        Theme,
        Environments,
        Dependencies,
        aLayout: Layout,
        aSpin: Spin,
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
            chooseEnvironment: false,
            updateDeps: false,
        };
    },
    methods: {
        async run() {
            await this.$store.dispatch('run');
        },
        async stop() {
            await this.$store.dispatch('stop');
        },
        format(output) {
            return output.map(l => l.replace(/(\r\n|\n|\r)/gm, '').trim()).filter(l => !l.startsWith('|'));
        },
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
