<template>
    <div>
        <drawer title="Theme" placement="right" :visible="show" @close="$emit('close')">
            <div>
                <div v-for="t in themes" :key="t">
                    <a-button :type="t === current ? 'primary' : 'dashed'" block
                              :style="{marginBottom: '0.5rem'}"
                              @click="changeTheme(t)">{{ t }}</a-button>
                </div>
            </div>
        </drawer>
    </div>
</template>

<script>
import { Drawer, Button } from 'ant-design-vue';

export default {
    components: {
        Drawer,
        aButton: Button,
    },
    data() {
        return {
            themes: [
                'base16-light',
                'monokai',
                'paraiso-light',
                'rubyblue',
            ],
        };
    },
    props: ['show', 'value', 'current'],
    methods: {
        changeTheme(theme) {
            this.$emit('input', theme);
        },
    },
};
</script>
